package com.duckyshine.app.sound;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import org.lwjgl.openal.*;

import com.duckyshine.app.math.RandomNumber;

import com.duckyshine.app.utility.FileUtility;
import com.duckyshine.app.utility.ResourceFinder;

import com.duckyshine.app.debug.Debug;

import static org.lwjgl.openal.ALC11.*;

public class SoundPlayer {
    private long audioDevice;
    private long audioContext;

    private String deviceName;

    private Sound music;

    private List<String> playlist;

    private Map<String, Sound> cache;

    public SoundPlayer() {
        this.music = null;

        this.cache = new HashMap<>();

        this.playlist = new ArrayList<>();

        this.initialise();
    }

    private void initialise() {
        int[] attributes = { 0 };

        this.deviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        this.audioDevice = alcOpenDevice(this.deviceName);
        this.audioContext = alcCreateContext(this.audioDevice, attributes);

        alcMakeContextCurrent(this.audioContext);

        ALCCapabilities alcCapabilities = ALC.createCapabilities(this.audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);

        if (!alCapabilities.OpenAL10) {
            assert false : "Audio library is not supported";
        }

        this.initialisePlaylist();
    }

    private void initialisePlaylist() {
        List<String> files = ResourceFinder.getFiles("sound/music/");

        if (files != null) {
            this.playlist.addAll(files);
        }
    }

    public void playMusic() {
        if (this.music == null || !this.music.isPlaying()) {
            this.music = this.getRandomMusic();
            Debug.debug("Now playing: " + FileUtility.getFilename(this.music.getFilepath()));
            this.music.play();
        }
    }

    private Sound getRandomMusic() {
        int index = RandomNumber.getRandomInteger(this.playlist.size());

        String filepath = this.playlist.get(index);

        if (cache.containsKey(filepath)) {
            return cache.get(filepath);
        }

        Sound music = new Sound(filepath);

        this.cache.put(filepath, music);

        return music;
    }

    public void cleanup() {
        for (Sound sound : cache.values()) {
            sound.delete();
        }

        alcDestroyContext(this.audioContext);

        alcCloseDevice(this.audioDevice);
    }
}
