package com.mmslab15.maria.senspler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import com.mmslab15.maria.senspler.Services.SamplerService;

public class ReadingsInProgressActivity extends Activity {
    public static final String EXTRA_DATAPART = "com.mmslab15.maria.senspler.SAMPLES_TAG";
    private Intent intent;
    final Handler samplingTime = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_readings_in_progress);
        Intent intent = getIntent();
        String partOfData = intent.getStringExtra(MainActivity.EXTRA_partOfResults);
        startSampling(partOfData);

    }

    private void startSampling(String partOfData) {
        intent = new Intent(this, SamplerService.class);
        intent.putExtra(EXTRA_DATAPART, partOfData);
        startService(intent);
    }

    public void discontinueSampling(View view) {
        stopService(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
