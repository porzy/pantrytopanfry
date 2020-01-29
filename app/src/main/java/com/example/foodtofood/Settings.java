package com.example.foodtofood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;

public class Settings extends AppCompatActivity {

    private int theme = 1;
    private SharedPreferences sharedPrefs;
    private Switch swNight;
    private Toolbar toolbar;
    final int STANDARD_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefs = getSharedPreferences("general prefs", MODE_PRIVATE);
        theme = sharedPrefs.getInt("theme", 1);
        int test = getIntent().getIntExtra("theme", 3);
        if(test == 1 || test ==2){
            theme = test;
        }

        if(theme == 1){
            setTheme(R.style.MyLightTheme);
        } else if (theme == 2){
            setTheme(R.style.MyDarkTheme);
        }

        setContentView(R.layout.activity_settings);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        swNight = findViewById(R.id.swNight);

        Log.d("tyler", "night checked? " + theme);
        Log.d("tyler", " " + test);
        if(theme == 1){
            swNight.setChecked(false);
        } else if (theme == 2){
            swNight.setChecked(true);
        }


        EventHandler eventHandler = new EventHandler();
        swNight.setOnCheckedChangeListener(eventHandler);

    }
    @Override
    public void onBackPressed() {
        //need an Intent just as a container for the data
        //to pass back
        Intent intent = new Intent();

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt("theme", theme);

        editor.apply();

        //set the result and finish (returning to the previous activity)
        setResult(RESULT_OK, intent);
        finish();
        //super.onBackPressed(); //calls finish()
    }

    //nested class to handle UI events
    class EventHandler implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            switch (compoundButton.getId()) {

                case R.id.swNight:

                    Log.d("tyler", "night checked? " + b);
                    if (b == true){
                        theme = 2;
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putInt("theme", theme);
                        Log.d("tyler", "night checked? " + theme);
                        editor.apply();
                        recreate();
                    }
                    else {
                        theme = 1;
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putInt("theme", theme);
                        Log.d("tyler", "night checked? " + theme);
                        editor.apply();
                        recreate();
                    }

                    break;

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar, menu);
        return true;
    }

    //to handle events
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_settings) {
            //explicit intent
            Intent i = new Intent(this, Settings.class);
            startActivityForResult(i, STANDARD_REQUEST_CODE);
            Log.d("tyler", "onactivity");
            return true;
        }

        return super.onOptionsItemSelected(item);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {

            theme = sharedPrefs.getInt("theme", 1);

            recreate();

            Log.d("tyler", "test");
        } else { //must be RESULT_CANCELLED
        }

    }

}
