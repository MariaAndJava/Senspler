package com.mmslab15.maria.senspler.Services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.IBinder;

import com.mmslab15.maria.senspler.MainActivity;


public class SamplerService extends Service implements Runnable {
    public static final String EXTRA_PARTOFDATA = "com.mmslab15.maria.senspler.SAMPLES_TAG2";
    private Intent intent;
    final private Handler readingsScheduler = new Handler();
    final private int duration = 305000; //in msec 305000
    final private int msecUntilStart = 5000; //in msec 5000

    public SamplerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String tag = intent.getStringExtra(MainActivity.EXTRA_partOfResults);
        this.intent = new Intent(this, SensorReaderService.class);
        this.intent.putExtra(EXTRA_PARTOFDATA, tag);

     /*   intent3 = new Intent(this, SoundService.class);
        intent3.putExtra(EXTRA_PARTOFDATA, tag);*/

        startSampling();
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopService(intent);
    }

    private void startSampling() {
        readingsScheduler.postDelayed(new Runnable() {
            public void run() {
                final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                startService(intent);
            }

        }, msecUntilStart);
        readingsScheduler.postDelayed(new Runnable() {
            public void run() {
                stopService(intent);
            }
        }, duration);
    }

    @Override
    public void run() {
    }
}