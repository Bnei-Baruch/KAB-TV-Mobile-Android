#ifndef FFMPEG_DECODER_AUDIO_H
#define FFMPEG_DECODER_AUDIO_H

#include "decoder.h"

typedef void (*AudioDecodingHandler) (int16_t*,int);

class DecoderAudio : public IDecoder
{
public:
    DecoderAudio(AVStream* stream);

    ~DecoderAudio();
	static double get_audio_clock();
    AudioDecodingHandler		onDecode;
	static DecoderAudio * myself;
	int16_t*                    mSamples;
    int                         mSamplesSize;
private:
   

	
	double						mAudio_diff_avg_coef;
	double						mAudio_diff_avg_count;
	double						mAudio_diff_threshold;
	
    bool                        prepare();
	double 						get_video_clock();
    bool                        decode(void* ptr);
    bool                        process(AVPacket *packet);
	double						mAudio_diff_cum;
	double 						synchronize(int16_t * mSamples,int mSamplesSize,double pts);
};

#endif //FFMPEG_DECODER_AUDIO_H
