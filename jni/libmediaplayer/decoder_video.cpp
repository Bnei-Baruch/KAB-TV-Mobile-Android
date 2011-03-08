#include <android/log.h>
#include "decoder_video.h"

#define TAG "FFMpegVideoDecoder"
# define SYNC "VIDEO_SYNC"


DecoderVideo::DecoderVideo(AVStream* stream) : IDecoder(stream)
{
	 mStream->codec->get_buffer = getBuffer;
	 mStream->codec->release_buffer = releaseBuffer;
}

DecoderVideo::~DecoderVideo()
{
	av_free(mFrame);
	__android_log_print(ANDROID_LOG_INFO, TAG, "destructor called of DecoderVideo");
}

bool DecoderVideo::prepare()
{
	mFrame = avcodec_alloc_frame();
	if (mFrame == NULL) {
		return false;
	}
	return true;
}

double DecoderVideo::dequeue(int packetsNum)
{
__android_log_print(ANDROID_LOG_INFO, TAG, "dequeue packets");
	AVPacket pkt;
	double pts;
	for(int i=1;i<=packetsNum;i++)
	{
		if(mQueue->size()>0){
			mQueue->get(&pkt,true);
			pts = pkt.pts;
			av_free_packet(&pkt);
			}
		else
			return 0;
	}
	return pts;
}

double DecoderVideo::synchronize(AVFrame *src_frame, double pts) {

	double frame_delay;

	if (pts != 0) {
		/* if we have pts, set video clock to it */
		IDecoder::mVideoClock = pts;
	} else {
		/* if we aren't given a pts, set it to the clock */
		pts = IDecoder::mVideoClock;
	}
	/* update the video clock */
	frame_delay = av_q2d(mStream->codec->time_base);
	/* if we are repeating a frame, adjust clock accordingly */
	frame_delay += src_frame->repeat_pict * (frame_delay * 0.5);
	__android_log_print(ANDROID_LOG_INFO, TAG, "frame delay :%0.3f",frame_delay);
	IDecoder::mVideoClock += frame_delay;
	__android_log_print(ANDROID_LOG_INFO, TAG, "pst in video thread:%0.3f",pts);
	__android_log_print(ANDROID_LOG_INFO, TAG, "mVideoClock in video thread:%0.3f", mVideoClock);
	return pts;
}

bool DecoderVideo::process(AVPacket *packet)
{
    int	completed;
    double pts = 0;

	// Decode video frame
	for(;;){
	pts = 0;
	global_video_pkt_pts = packet->pts;
	avcodec_decode_video(mStream->codec,
						 mFrame,
						 &completed,
						 packet->data, 
						 packet->size);
	
	if (packet->dts == AV_NOPTS_VALUE && mFrame->opaque
			&& *(uint64_t*) mFrame->opaque != AV_NOPTS_VALUE) {
		pts = *(uint64_t *) mFrame->opaque;
		__android_log_print(ANDROID_LOG_INFO, TAG, "using opaque pts");
		__android_log_print(ANDROID_LOG_INFO, TAG, "pts value :%0.3f",pts);
	} else if (packet->dts != AV_NOPTS_VALUE && packet->dts>0) {
		pts = packet->dts;
		__android_log_print(ANDROID_LOG_INFO, TAG, "getting pts from dts");
		__android_log_print(ANDROID_LOG_INFO, TAG, "dts value :%0.3f",packet->dts);
		__android_log_print(ANDROID_LOG_INFO, TAG, "pts value :%0.3f",pts);
	} else {
		pts = 0;
		__android_log_print(ANDROID_LOG_INFO, TAG, "NO PTS VALUE setting 0");
	}
	pts *= av_q2d(mStream->time_base);

	if (completed) {
		pts = synchronize(mFrame, pts);

		onDecode(mFrame, pts);

		return true;
	}
	}
	
	return false;
}

bool DecoderVideo::decode(void* ptr)
{
	AVPacket        pPacket;
	
	__android_log_print(ANDROID_LOG_INFO, TAG, "decoding video");
	
    while(mRunning)
    {
		//pthread_mutex_lock(&globalMutex);
		//wait on condition variable to play other wise suspend
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
	
    __android_log_print(ANDROID_LOG_INFO, TAG, "decoding video ended");
	
    // Free the RGB image
    av_free(mFrame);

    return true;
}

/* These are called whenever we allocate a frame
 * buffer. We use this to store the global_pts in
 * a frame at the time it is allocated.
 */
int DecoderVideo::getBuffer(struct AVCodecContext *c, AVFrame *pic) {
	int ret = avcodec_default_get_buffer(c, pic);
	uint64_t *pts = (uint64_t *)av_malloc(sizeof(uint64_t));
	*pts = global_video_pkt_pts;
	 __android_log_print(ANDROID_LOG_INFO, SYNC, "new buffer pts value: %0.3f",*pts);
	 __android_log_print(ANDROID_LOG_INFO, SYNC, "NOPTSVALUE IS: %0.3f",AV_NOPTS_VALUE);
	 
	pic->opaque = pts;
	return ret;
}
void DecoderVideo::releaseBuffer(struct AVCodecContext *c, AVFrame *pic) {
	if (pic)
		av_freep(&pic->opaque);
	avcodec_default_release_buffer(c, pic);
}
