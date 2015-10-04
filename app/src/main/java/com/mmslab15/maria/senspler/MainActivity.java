package com.mmslab15.maria.senspler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.mmslab15.maria.senspler.Services.SamplerService;

public class MainActivity extends Activity {
    public final static String EXTRA_partOfResults = "com.mmslab15.maria.senspler.ADDITIONAL";

    private static final  String[] contextDropdownItems = new String[]{"Hand", "Pocket", "Bag", "Table", "not applicable"};
    private static final  String[] heightDopdownItems = new String[]{"155-164cm", "165-174cm", "175-184cm", "185-194cm", "not applicable"};
    private static final  String[] movementDropdownItems = new String[]{"still/supported", "moving", "standing unaided","not applicable"};
    private static final  String[] usedDropdownItems = new String[]{"used", "unused", "mixed"};
    private static final  String[] typingDropdownItems = new String[]{"typing", "not typing", "not applicable"};
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        initialiseScreen();
        return true;
    }

    private void initialiseScreen(){
        Spinner contextDropDown = (Spinner) findViewById(R.id.spinner);
        Spinner heightDropdown = (Spinner) findViewById(R.id.spinner2);
        Spinner movementDropdown = (Spinner) findViewById(R.id.spinner3);
        Spinner usedDropdown = (Spinner) findViewById(R.id.spinner4);
        Spinner typingDropdown = (Spinner) findViewById(R.id.spinner5);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, contextDropdownItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contextDropDown.setAdapter(adapter);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, heightDopdownItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heightDropdown.setAdapter(adapter2);

        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, movementDropdownItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        movementDropdown.setAdapter(adapter3);

        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, usedDropdownItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        usedDropdown.setAdapter(adapter4);

        ArrayAdapter<String> adapter5 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typingDropdownItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typingDropdown.setAdapter(adapter5);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
        Handle action bar item clicks here. The action bar will
        automatically handle clicks on the Home/Up button, so long
        as you specify a parent activity in AndroidManifest.xml.
        */
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    /** Called when user pushes the start button */
    public void startRecording(View view) {
//        Intent intent = new Intent(this, ReadingsInProgressActivity.class);
        intent = new Intent(this, SamplerService.class);
        Spinner contextDropDown = (Spinner) findViewById(R.id.spinner);
        Spinner heightDropdown = (Spinner) findViewById(R.id.spinner2);
        Spinner movementDropdown = (Spinner) findViewById(R.id.spinner3);
        Spinner usedDropdown = (Spinner) findViewById(R.id.spinner4);
        Spinner typingDropdown = (Spinner) findViewById(R.id.spinner5);
        EditText editText = (EditText) findViewById(R.id.editText);
        String additional = TextUtils.isEmpty(editText.getText().toString())?"none":editText.getText().toString();
        String context = contextDropDown.getSelectedItem().toString();
        String movement = heightDropdown.getSelectedItem().toString();
        String height = movementDropdown.getSelectedItem().toString();
        String used = usedDropdown.getSelectedItem().toString();
        String typing = typingDropdown.getSelectedItem().toString();
        String partOfData = ", " + context +", "+ movement+", "+ height+", "+used+", "+typing+", "+ additional;
        intent.putExtra(EXTRA_partOfResults, partOfData);
        setContentView(R.layout.activity_readings_in_progress);
        startService(intent);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_main);
                initialiseScreen();
            }
        }, 306000);
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(intent);
        android.os.Debug.stopMethodTracing();
    }



    public void discontinueSampling(View view) {
        stopService(intent);
        setContentView(R.layout.activity_main);
        initialiseScreen();
    }
}
