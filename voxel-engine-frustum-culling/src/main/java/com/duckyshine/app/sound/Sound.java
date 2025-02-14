package com.duckyshine.app.sound;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.system.MemoryStack;

import static org.lwjgl.system.MemoryStack.*;

import static org.lwjgl.system.MemoryUtil.*;

import static org.lwjgl.stb.STBVorbis.*;

import static org.lwjgl.openal.AL11.*;

public class Sound {
    private final float GAIN = 0.3f;

    private int bufferId;
    private int sourceId;
    private int audioFormat;

    private boolean isPlaying;

    private String filepath;

    private ShortBuffer audioBuffer;

    public Sound(String filepath) {
        this.audioFormat = -1;

        this.isPlaying = false;

        this.filepath = filepath;

        this.initialise();
    }

    private void initialise() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer channelsBuffer = stack.mallocInt(1);
            IntBuffer sampleRateBuffer = stack.mallocInt(1);

            this.audioBuffer = stb_vorbis_decode_filename(this.filepath, channelsBuffer, sampleRateBuffer);

            if (this.audioBuffer == null) {
                return;
            }

            int channels = channelsBuffer.get(0);
            int sampleRate = sampleRateBuffer.get(0);

            this.setAudioFormat(channels);

            this.setupAudioParameters(sampleRate);
        }
    }

    private void setupAudioParameters(int sampleRate) {
        this.bufferId = alGenBuffers();

        alBufferData(this.bufferId, this.audioFormat, this.audioBuffer, sampleRate);

        this.sourceId = alGenSources();

        alSourcei(this.sourceId, AL_BUFFER, this.bufferId);
        alSourcei(this.sourceId, AL_POSITION, 0);
        alSourcef(this.sourceId, AL_GAIN, this.GAIN);

        if (this.audioBuffer != null) {
            memFree(this.audioBuffer);
        }

        this.audioBuffer = null;
    }

    private void setAudioFormat(int channels) {
        switch (channels) {
            case 1:
                this.audioFormat = AL_FORMAT_MONO16;
                break;
            case 2:
                this.audioFormat = AL_FORMAT_STEREO16;
                break;
            default:
                break;
        }
    }

    public void delete() {
        this.deleteSources();

        this.deleteBuffers();
    }

    private void deleteSources() {
        if (this.sourceId != 0) {
            alDeleteSources(this.sourceId);

            this.sourceId = 0;
        }
    }

    private void deleteBuffers() {
        if (this.bufferId != 0) {
            alDeleteBuffers(this.bufferId);

            this.bufferId = 0;
        }
    }

    public void play() {
        int state = alGetSourcei(this.sourceId, AL_SOURCE_STATE);

        if (state == AL_STOPPED) {
            this.isPlaying = false;

            alSourcei(this.sourceId, AL_POSITION, 0);
        }

        if (!this.isPlaying) {
            alSourcePlay(this.sourceId);

            this.isPlaying = true;
        }
    }

    public void stop() {
        if (this.isPlaying) {
            alSourceStop(this.sourceId);

            this.isPlaying = false;
        }
    }

    public String getFilepath() {
        return this.filepath;
    }

    public boolean isPlaying() {
        int state = alGetSourcei(this.sourceId, AL_SOURCE_STATE);

        if (state == AL_STOPPED) {
            this.isPlaying = false;
        }

        return this.isPlaying;
    }
}
