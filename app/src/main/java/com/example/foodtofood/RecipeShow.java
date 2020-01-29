package com.example.foodtofood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class RecipeShow extends AppCompatActivity {
    private Toolbar toolbar;
    private SharedPreferences sharedPrefs;
    private int theme;
    final int STANDARD_REQUEST_CODE = 1;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefs = getSharedPreferences("general prefs", MODE_PRIVATE);

        theme = sharedPrefs.getInt("theme", 1);
        if(theme == 1){
            setTheme(R.style.MyLightTheme);
            Log.d("tyler", "light theme");

        } else if (theme == 2){
            setTheme(R.style.MyDarkTheme);
            Log.d("tyler", "dark theme");
        }
        setContentView(R.layout.activity_recipe_show);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        WebView view = (WebView) findViewById(R.id.webView);
        view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        view.getSettings().setJavaScriptEnabled(true);
        view.loadUrl(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar, menu);
        return true;
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
}
