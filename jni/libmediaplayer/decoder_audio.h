#ifndef FFMPEG_DECODER_AUDIO_H
#define FFMPEG_DECODER_AUDIO_H

#include "decoder.h"

typedef void (*AudioDecodingHandler) (int16_t*,int);

class DecoderAudio : public IDecoder
{
public:
    DecoderAudio(AVStream* stream);

    ~DecoderAudio();

    AudioDecodingHandler		onDecode;

private:
    int16_t*                    mSamples;
    int                         mSamplesSize;
	AVStream* 					mStream;
    bool                        prepare();
    bool                        decode(void* ptr);
    bool                        process(AVPacket *packet);
	
	double 						synchronize(int16_t * mSamples,int mSamplesSize,double pts);
};

#endif //FFMPEG_DECODER_AUDIO_H
