package com.mmslab15.maria.senspler.Services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.mmslab15.maria.senspler.Classes.Tone;

import java.io.IOException;

public class SoundService extends Service {

    MediaRecorder mRecorder;
    final static String LOG_TAG = "Recorder:";
    private String mFileName =  Environment.DIRECTORY_DOCUMENTS + "/Senspler" + "/audiorecordtest.3gp";
    private int timeToPlay = 1000;
    private Tone sound;

    public SoundService() {
        sound = new Tone(2000,timeToPlay);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        getSample();

        stopRecording();
        return START_REDELIVER_INTENT;

    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    private void getSample() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
        sound.run();
        Handler stopScheduler = new Handler();
        stopScheduler.postDelayed(new Runnable() {
            public void run() {
                stopRecording();
                stopSelf();
            }
        }, timeToPlay);
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
}
