#include <android/log.h>
#include "decoder_audio.h"

#define TAG "FFMpegAudioDecoder"

DecoderAudio::DecoderAudio(AVStream* stream) : IDecoder(stream)
{
	mStream = stream;
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
    int size = mSamplesSize;
    int len = avcodec_decode_audio3(mStream->codec, mSamples, &size, packet);

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
int  DecoderAudio::synchronize( int16_t * mSamples,int mSamplesSize,double pts)
 {
  int n;
  double ref_clock;

  n = 2 * mStream->codec->channels;
  
 // if(is->av_sync_type != AV_SYNC_AUDIO_MASTER) {
    double diff, avg_diff;
    int wanted_size, min_size, max_size, nb_samples;
    
    ref_clock = get_video_clock(is);
    diff = get_audio_clock(is) - ref_clock;

    if(diff < AV_NOSYNC_THRESHOLD) {
      // accumulate the diffs
      is->audio_diff_cum = diff + is->audio_diff_avg_coef
	* is->audio_diff_cum;
      if(is->audio_diff_avg_count < AUDIO_DIFF_AVG_NB) {
	is->audio_diff_avg_count++;
      } else {
	avg_diff = is->audio_diff_cum * (1.0 - is->audio_diff_avg_coef);
	if(fabs(avg_diff) >= is->audio_diff_threshold) {
	  wanted_size = samples_size + ((int)(diff * mStream->codec->sample_rate) * n);
	  min_size = samples_size * ((100 - SAMPLE_CORRECTION_PERCENT_MAX) / 100);
	  max_size = samples_size * ((100 + SAMPLE_CORRECTION_PERCENT_MAX) / 100);
	  if(wanted_size < min_size) {
	    wanted_size = min_size;
	  } else if (wanted_size > max_size) {
	    wanted_size = max_size;
	  }
	  if(wanted_size < samples_size) {
	    /* remove samples */
	    samples_size = wanted_size;
	  } else if(wanted_size > samples_size) {
	    uint8_t *samples_end, *q;
	    int nb;

	    /* add samples by copying final sample*/
	    nb = (samples_size - wanted_size);
	    samples_end = (uint8_t *)samples + samples_size - n;
	    q = samples_end + n;
	    while(nb > 0) {
	      memcpy(q, samples_end, n);
	      q += n;
	      nb -= n;
	    }
	    samples_size = wanted_size;
	  }
	}
      }
    } else {
      /* difference is TOO big; reset diff stuff */
      is->audio_diff_avg_count = 0;
      is->audio_diff_cum = 0;
    }
  //}
  return samples_size;
}

double DecoderAudio::get_video_clock(VideoState *is) {
  double delta;

  delta = (av_gettime() - is->video_current_pts_time) / 1000000.0;
  return is->video_current_pts + delta;
}

double DecoderAudio::get_audio_clock(VideoState *is) {
  double pts;
  int hw_buf_size, bytes_per_sec, n;

  pts = is->audio_clock; /* maintained in the audio thread */
  hw_buf_size = is->audio_buf_size - is->audio_buf_index;
  bytes_per_sec = 0;
  n = is->audio_st->codec->channels * 2;
  if(is->audio_st) {
    bytes_per_sec = is->audio_st->codec->sample_rate * n;
  }
  if(bytes_per_sec) {
    pts -= (double)hw_buf_size / bytes_per_sec;
  }
  return pts;
}
