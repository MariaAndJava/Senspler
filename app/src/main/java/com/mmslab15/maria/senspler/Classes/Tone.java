package com.mmslab15.maria.senspler.Classes;

/**
 * Created by Maria on 10.09.2015.
 */

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class Tone implements Runnable {

    public static int SAMPLE_RATE = 44100;
    private int hz;
    private int msec;

    public Tone(int hz,int msec){
        this.hz = hz;
        this.msec = msec;
    }

    public void run() {
        sound(hz, msec, 1);
    }

    public void sound(int hz, int msecs, int vol) {

        short[] buf = new short[(int) SAMPLE_RATE * msecs / 1000];

        for (int i = 0; i < buf.length; i++) {
            double angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
            buf[i] = (short) (Math.sin(angle) * 127.0 * vol*100);
        }

        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC,
                (int) SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                android.media.AudioFormat.ENCODING_PCM_16BIT, buf.length*2,//256*1024,
                AudioTrack.MODE_STATIC);
        track.write(buf, 0, buf.length);

        track.play();
    }

}