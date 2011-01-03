#include <android/log.h>
#include "decoder_audio.h"

#define TAG "FFMpegAudioDecoder"


#define AV_SYNC_THRESHOLD 0.01
#define AV_NOSYNC_THRESHOLD 10.0
#define AUDIO_DIFF_AVG_NB 20
#define SDL_AUDIO_BUFFER_SIZE 1024

#define SAMPLE_CORRECTION_PERCENT_MAX 10


DecoderAudio::DecoderAudio(AVStream* stream) : IDecoder(stream)
{
	mStream = stream;
	mAudio_diff_avg_coef = exp(log(0.01 / AUDIO_DIFF_AVG_NB));
	mAudio_diff_threshold = 2.0 * SDL_AUDIO_BUFFER_SIZE / codecCtx->sample_rate;
}

DecoderAudio::~DecoderAudio()
{
}

bool DecoderAudio::prepare()
{
    mSamplesSize = AVCODEC_MAX_AUDIO_FRAME_SIZE;
    mSamples = (int16_t *) av_malloc(mSamplesSize);
    if(mSamples == NULL) {
    	return false;
    }
    return true;
}

bool DecoderAudio::process(AVPacket *packet)
{
	double pts;
	int len1, data_size, n;
    int size = mSamplesSize;
    int len = avcodec_decode_audio3(mStream->codec, mSamples, &size, packet);

	
	 pts = mAudioClock;
      //*pts_ptr = pts;
      n = 2 * mStream->codec->channels;
      mAudioClock += (double)size /
	(double)(n * mStream->codec->sample_rate);
	
	
    //call handler for posting buffer to os audio driver
    onDecode(mSamples, size);

    return true;
}

bool DecoderAudio::decode(void* ptr)
{
    AVPacket        pPacket;

    __android_log_print(ANDROID_LOG_INFO, TAG, "decoding audio");

    while(mRunning)
    {
        if(mQueue->get(&pPacket, true) < 0)
        {
            mRunning = false;
            return false;
        }
        if(!process(&pPacket))
        {
            mRunning = false;
            return false;
        }
        // Free the packet that was allocated by av_read_frame
        av_free_packet(&pPacket);
    }

    __android_log_print(ANDROID_LOG_INFO, TAG, "decoding audio ended");

    // Free audio samples buffer
    av_free(mSamples);
    return true;
}

/* Add or subtract samples to get a better sync, return new
   audio buffer size */
double  DecoderAudio::synchronize( int16_t * samples,int samplesSize,double pts)
 {
  int n;
  double ref_clock;

  n = 2 * mStream->codec->channels;
  
 // if(is->av_sync_type != AV_SYNC_AUDIO_MASTER) {
    double diff, avg_diff;
    int wanted_size, min_size, max_size, nb_samples;
    
    ref_clock = global_video_pkt_pts;
    diff = get_audio_clock() - ref_clock;

    if(diff < AV_NOSYNC_THRESHOLD) {
      // accumulate the diffs
      mAudio_diff_cum = diff + mAudio_diff_avg_coef
	* mAudio_diff_cum;
      if(mAudio_diff_avg_count < AUDIO_DIFF_AVG_NB) {
		mAudio_diff_avg_count++;
      } else {
	avg_diff = mAudio_diff_cum * (1.0 - mAudio_diff_avg_coef);
	if(fabs(avg_diff) >= mAudio_diff_threshold) {
	  wanted_size = samplesSize + ((int)(diff * mStream->codec->sample_rate) * n);
	  min_size = samplesSize * ((100 - SAMPLE_CORRECTION_PERCENT_MAX) / 100);
	  max_size = samplesSize * ((100 + SAMPLE_CORRECTION_PERCENT_MAX) / 100);
	  if(wanted_size < min_size) {
	    wanted_size = min_size;
	  } else if (wanted_size > max_size) {
	    wanted_size = max_size;
	  }
	  if(wanted_size < samplesSize) {
	    /* remove samples */
	    samplesSize = wanted_size;
	  } else if(wanted_size > samplesSize) {
	    uint8_t *samples_end, *q;
	    int nb;

	    /* add samples by copying final sample*/
	    nb = (samplesSize - wanted_size);
	    samples_end = (uint8_t *)samples + samplesSize - n;
	    q = samples_end + n;
	    while(nb > 0) {
	      memcpy(q, samples_end, n);
	      q += n;
	      nb -= n;
	    }
	    samplesSize = wanted_size;
	  }
	}
      }
    } else {
      /* difference is TOO big; reset diff stuff */
      mAudio_diff_avg_count = 0;
      mAudio_diff_cum = 0;
    }
  //}
  return samplesSize;
}

double DecoderAudio::get_video_clock() {
  double delta;

  delta = (av_gettime() - global_video_pkt_pts) / 1000000.0;
  return mVideoClock + delta;
}

double DecoderAudio::get_audio_clock() {
  double pts;
  int hw_buf_size, bytes_per_sec, n;

  pts = mAudioClock; /* maintained in the audio thread */
  hw_buf_size = audio_buf_size - >audio_buf_index;
  bytes_per_sec = 0;
  n = mStream->codec->channels * 2;
  if(mStream) {
    bytes_per_sec = mStream->codec->sample_rate * n;
  }
  if(bytes_per_sec) {
    pts -= (double)hw_buf_size / bytes_per_sec;
  }
  return pts;
}
