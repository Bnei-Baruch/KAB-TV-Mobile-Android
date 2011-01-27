#include <android/log.h>
#include "decoder_audio.h"

#define TAG "FFMpegAudioDecoder"
# define SYNC_AUDIO "AUDIO_SYNC"

#define AV_SYNC_THRESHOLD 0.01
#define AV_NOSYNC_THRESHOLD 10.0
#define AUDIO_DIFF_AVG_NB 20
#define SDL_AUDIO_BUFFER_SIZE 1024

#define SAMPLE_CORRECTION_PERCENT_MAX 10
DecoderAudio *DecoderAudio::myself = NULL;

DecoderAudio::DecoderAudio(AVStream* stream) : IDecoder(stream)
{
	mStream = stream;
	mAudio_diff_avg_coef = exp(log(0.01 / AUDIO_DIFF_AVG_NB));
	mAudio_diff_threshold = 2.0 * SDL_AUDIO_BUFFER_SIZE / mStream->codec->sample_rate;
	DecoderAudio::myself = this;
}

DecoderAudio::~DecoderAudio()
{
  delete mSamples;
  __android_log_print(ANDROID_LOG_INFO, TAG, "destructor called of DecoderAudio");
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
	int len1=0, data_size=0, n,sync_size;
    int size = mSamplesSize;
	uint8_t*  data = packet->data;
	int pktsize = packet->size;
	len1=0;
	do //decoding + sync per frame in the packet until we finish all frames then return
	{
	 __android_log_print(ANDROID_LOG_INFO, TAG, "before decoding audio frame packet size is:%d ",packet->size);
	  size = mSamplesSize;
      data_size = avcodec_decode_audio3(mStream->codec, mSamples, &size, packet);
	  __android_log_print(ANDROID_LOG_INFO, TAG, "decoding audio frame size data_size: %d and size:%d",data_size,size);
	  
	  
	if(data_size<0)
	 {
	/* If error, output silence */
	continue; //go to next frame
	
      } else {
	
	
		
		
		 packet->data += data_size;
         packet->size -= data_size;
		// memcpy(mTempBuf+len1, mSamples, data_size);
		 
		
		 __android_log_print(ANDROID_LOG_INFO, TAG, "decoding audio frame size added: %d and new packet size:%d ",len1,packet->size);
      }
	
	} while (packet->size>0);
	
	
	 packet->data = data;
     packet->size = pktsize;
	//sync_size = synchronize(mSamples,
	//			       len1, pts);
	//__android_log_print(ANDROID_LOG_INFO, TAG, "after sync audio suze is: %d",sync_size);
	
	//if(sync_size>data_size)
	//	sync_size=data_size;
	pts = packet->dts;
	pts *= av_q2d(mStream->time_base);
	if(pts != AV_NOPTS_VALUE)
		IDecoder::mAudioClock =  pts;
      //*pts_ptr = pts;
      n = 2 * mStream->codec->channels;
      IDecoder::mAudioClock += (double)data_size /
	(double)(n * mStream->codec->sample_rate);
	__android_log_print(ANDROID_LOG_INFO, TAG, "IDecoder::mAudioClock after decode:%0.3f ",IDecoder::mAudioClock);
	
	//memcpy(mSamples, mSamples, sync_size);
	//mSamplesSize = sync_size;
    //call handler for posting buffer to os audio driver
    
/*	 if(packet->pts != AV_NOPTS_VALUE) 
      IDecoder::mAudioClock = av_q2d(mStream->codec->time_base)*packet->pts;
	__android_log_print(ANDROID_LOG_INFO, TAG, "IDecoder::mAudioClock:%0.3f ",IDecoder::mAudioClock);	
	 __android_log_print(ANDROID_LOG_INFO, TAG, "packet->pts:%0.3f ",packet->pts);	
__android_log_print(ANDROID_LOG_INFO, TAG, "av_q2d:%0.3f ",av_q2d(mStream->codec->time_base));		 */
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
  return IDecoder::mVideoClock + delta;
}

double DecoderAudio::get_audio_clock() {
  static double pts;
  static int hw_buf_size, bytes_per_sec, n;

  pts = IDecoder::mAudioClock; /* maintained in the audio thread */
   __android_log_print(ANDROID_LOG_INFO, SYNC_AUDIO, "pts in audio: %0.3f",pts);
  hw_buf_size = DecoderAudio::myself->mSamplesSize;
   __android_log_print(ANDROID_LOG_INFO, SYNC_AUDIO, "mSamplesSize: %d",hw_buf_size);
  bytes_per_sec = 0;
  n = DecoderAudio::myself->mStream->codec->channels * 2;
  if(DecoderAudio::myself->mStream) {
    bytes_per_sec = DecoderAudio::myself->mStream->codec->sample_rate * n;
	 __android_log_print(ANDROID_LOG_INFO, SYNC_AUDIO, "bytes_per_sec: %d",bytes_per_sec);
  }
  if(bytes_per_sec) {
    pts += (double)hw_buf_size / bytes_per_sec;
	 __android_log_print(ANDROID_LOG_INFO, SYNC_AUDIO, "pts in Audio: %0.3f",pts);
  }
  return pts;
}
