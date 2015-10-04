package com.mmslab15.maria.senspler.Services;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class SensorReaderService extends Service implements SensorEventListener, Runnable {
    final private Handler readingsScheduler = new Handler();
    final private SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy-HH:mm:ss:SSS");
    private String deviceContext;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor light;
    private File fileNoWindowing;
    private File fileWindowing;
    private float facing;
    private float pointing;
    private float upright;
    private String results;
    private boolean end = false;
    private float lux = 50;
    private SensorReaderService sensorReaderService;
    private ArrayList<SampleBag> sensorEvents = new ArrayList();
    private float[] valuesX = new float[20];
    private float[] valuesY = new float[20];
    private float[] valuesZ = new float[20];
    private float[] valuesUpright = new float[20];
    private float[] valuesLight = new float[20];
    private float[] valuesPointing = new float[20];
    private float[] valuesFacing = new float[20];

    public SensorReaderService() {
        sensorReaderService = this;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fileNoWindowing = getFile("SensplerSamples NW" + sdf.format(new Date(System.currentTimeMillis())) + ".arff");
        fileWindowing = getFile("SensplerSamples W" + sdf.format(new Date(System.currentTimeMillis())) + ".arff");
        deviceContext = intent.getStringExtra(SamplerService.EXTRA_PARTOFDATA);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(this, accelerometer,
                50000);
        sensorManager.registerListener(this, light,
                50000);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        readingsScheduler.removeCallbacksAndMessages(this);
        end = true;
        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
        finish();
    }

    @Override
    public void run() {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            sensorEvents.add(new SampleBag(event.values[0],event.values[1],event.values[2], new Date(System.currentTimeMillis()), lux));
            Log.w("bj!", event.values[0]+ "--" +event.values[1]+ "--" +event.values[2]);

        }
        if (end) {
            sensorManager.unregisterListener(this);
        }
        lux = (event.sensor.getType() == Sensor.TYPE_LIGHT) ? event.values[0] : lux;

    }

    public void finish() {
        int i = 0;
        for (SampleBag bag : sensorEvents) {
            float accX =  bag.accX;
            float accY =  bag.accY;
            float accZ =  bag.accZ;
            facing = (accZ < 0) ? 0 : 1; //0 is down, 1 is up
            pointing = (accY < 0) ? 0 : 1; //0 is down, 1 is up
            upright = (Math.abs(accX) < 5) ? 0 : 1; //0 is upright, 1 is upright
            results = sdf.format(bag.timestamp) + ", " + accX + ", " + accY + ", " + accZ + ", " + facing + ", " + pointing + ", " + upright + ", " + (int) bag.lux + ", " + deviceContext;
            writeToFile(results, fileNoWindowing);

            if (i < 20) {
                valuesX[i] =  accX;
                valuesY[i]=accY;
                valuesZ[i]=accZ;
                valuesFacing[i]=facing;
                valuesPointing[i]=pointing;
                valuesUpright[i]=upright;
                valuesLight[i]=lux;
                i++;
            } else {
                writeToFile(sdf.format(bag.timestamp) + computeWindowedResults(valuesX, valuesY, valuesZ, valuesFacing, valuesPointing, valuesUpright, valuesLight), fileWindowing);
                Arrays.fill(valuesX, 0);
                Arrays.fill(valuesY, 0);
                Arrays.fill(valuesZ, 0);
                Arrays.fill(valuesFacing, 0);
                Arrays.fill(valuesLight, 0);
                Arrays.fill(valuesPointing, 0);
                Arrays.fill(valuesUpright, 0);
                i = 0;
            }

        }
        Log.w("tag", "" + sensorEvents.size());
    }

    private String computeWindowedResults(float[] valuesX, float[] valuesY, float[] valuesZ, float[] valuesFacing, float[] valuesPointing, float[] valuesTilted, float[] valuesLight) {
        String res = ", " + median(valuesLight) + ", " + zeroCrossings(valuesX) + ", " + zeroCrossings(valuesY) + ", " + zeroCrossings(valuesZ) + ", " + signalMagnitudeArea(valuesX, valuesY, valuesZ) + ", " + mean(valuesX) + ", " + mean(valuesY) + ", " + mean(valuesZ) + ", " + vote(valuesFacing) + ", " + vote(valuesPointing) + ", " + vote(valuesTilted) +  deviceContext;
        return res;
    }


    private float signalMagnitudeArea(float[] valuesX, float[] valuesY, float[] valuesZ) {
        float sma = 0;
        for (int i = 0; i < 20; i++) {
            sma = Math.abs( valuesX[i]) +  Math.abs(valuesY[i]) +  Math.abs(valuesZ[i]);
        }
        return sma /20f;
    }

    private int zeroCrossings(float[] values) {
        int zc = 0;
        float median = median(values);
        for (int i = 0; i < 18; i++) {
            boolean case1 = (values[i] <= median & median < values[i+1])?true:false;
            boolean case2 = (values[i] > median & median >= values[i+1])?true:false;
            if ( case1|| case2) {
                zc++;
            }
        }
        return zc;
    }

    public int vote(float[] values) {
        float vote = 0;
        for  (int i=0; i < 20; i++) {
            vote = vote + values[i];
        }
        return (vote < 10) ? 0 : 1;
    }

    public float mean(float[] valuesLight) {
        float mean = 0;
        for (int i=0; i < 20; i++) {
            mean = mean + valuesLight[i];
        }
        return mean / 20f;
    }

    private float median(float[] valuesLight) {
        Arrays.sort(valuesLight);
        return valuesLight[9];
    }


    public void writeToFile(String sensorReading, File file) {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.append(sensorReading);
            writer.newLine();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getFile(String filename) {
        // isExternalStorageAvailable();
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    private class SampleBag {
        public Date timestamp = null;
        public SensorEvent event = null;
        public float lux;
        public float accX;
        public float accY;
        public float accZ;

        public SampleBag(float accX,float accY,float accZ, Date timestamp, float lux) {
            this.timestamp = timestamp;
            this.accX = accX;
            this.accY = accY;
            this.accZ = accZ;
            this.lux = lux;
        }
    }
}


