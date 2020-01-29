package com.example.foodtofood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class Recipe extends AppCompatActivity {
    private Toolbar toolbar;
    private SharedPreferences sharedPrefs;
    private int theme;
    final int STANDARD_REQUEST_CODE = 1;
    private String ingredientsString;
    private String typeString = "main course";
    private ArrayList<Recipes> listRecipe;
    private RecipeAdapter recipeAdapter;
    private Runnable viewRecipe;
    private ListView lv_recipe;
    private int  recipeNumber = 10;

    private TextView textView;


    class ProcessRecipesTask extends AsyncTask {
        private String inputData;
        private URL url;
        private InputStream inputStream;

        //initialization block
        {

            try {

                if(ingredientsString != null){
                    url = new URL("https://api.spoonacular.com/recipes/complexSearch?apiKey=068c1b209b954ebb8cb365b0965a50ad&sort=min-missing-ingredients&addRecipeInformation=true&ignorePantry=true&includeingredients="+ingredientsString+"&fillIngredients=true&type="+typeString);

                    //url = new URL("https://api.spoonacular.com/recipes/findByIngredients?apiKey=068c1b209b954ebb8cb365b0965a50ad&number=1&ranking=2&ignorePantry=true&ingredients="+ingredientsString);
                } else{
                    url = new URL("https://api.spoonacular.com/recipes/complexSearch?apiKey=068c1b209b954ebb8cb365b0965a50ad&addRecipeInformation=true&fillIngredients=true&sort=random&type="+typeString);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(Recipe.this, "Processing Recipes", Toast.LENGTH_LONG).show();
        }

        //the "main" method of the async task
        //does NOT have access to the UI thread.
        @Override
        protected Object doInBackground(Object[] objects) {

            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                Log.d("tyler", "doInBackground: "+url);
                while (line != null){
                    line = bufferedReader.readLine();
                    inputData = inputData + line;
                }
                int count = recipeNumber;
                listRecipe = new ArrayList<Recipes>(10);

                String checkNull = "null";

                if(inputData.startsWith(checkNull)){
                    inputData = inputData.substring(checkNull.length(), inputData.length());
                }

                JSONObject jo = new JSONObject(inputData);
                JSONArray ja = jo.getJSONArray("results");
                for(int i = 0; i<ja.length(); i++){
                    jo = (JSONObject) ja.getJSONObject(i);
                    listRecipe.add(new Recipes (jo.get("title").toString(), jo.get("missedIngredientCount").toString(), jo.get("sourceUrl").toString()));
                    Log.d("tyler", "doInBackground: create");
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Toast.makeText(Recipe.this, "Processing complete!", Toast.LENGTH_LONG).show();


            //makeRecipes();
            recipeAdapter = new RecipeAdapter(Recipe.this, R.layout.list_item, listRecipe);
            lv_recipe.setAdapter(recipeAdapter);
            //textView.setText(ingredientsString);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefs = getSharedPreferences("general prefs", MODE_PRIVATE);
        recipeNumber = sharedPrefs.getInt("feedNumber", 10);
        theme = sharedPrefs.getInt("theme", 1);
        if(theme == 1){
            setTheme(R.style.MyLightTheme);
            Log.d("tyler", "light theme");

        } else if (theme == 2){
            setTheme(R.style.MyDarkTheme);
            Log.d("tyler", "dark theme");
        }
        setContentView(R.layout.activity_recipe);
        Intent intent = getIntent();
        ingredientsString = intent.getStringExtra("ingredients");
        typeString = intent.getStringExtra("type");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ProcessRecipesTask processRecipesTask = new ProcessRecipesTask();
        processRecipesTask.execute();

        lv_recipe = (ListView) findViewById(R.id.lv_recipes);
        //textView = findViewById(R.id.textView);
        //textView.setText(ingredientsString);


        lv_recipe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(Recipe.this, RecipeShow.class);
                intent.putExtra("url", listRecipe.get(i).getLink());

                startActivity(intent);
            }
        });
    }

    /*private void makeRecipes(){

        int count = recipeNumber;
        listRecipe = new ArrayList<Recipes>(10);
        for(int i = 0; i < count; i++) {
            listRecipe.add(new Recipes (title.get(i), id.get(i), link.get(i)));

        }

    }*/

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

    private class RecipeAdapter extends ArrayAdapter<Recipes> {

        private ArrayList<Recipes> items;

        public RecipeAdapter(Context context, int textViewResourceId, ArrayList<Recipes> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        //This method is called once for every item in the ArrayList as the list is loaded.
        //It returns a View -- a list item in the ListView -- for each item in the ArrayList
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }
            Recipes o = items.get(position);
            if (o != null) {
                TextView tt = (TextView) v.findViewById(R.id.toptext);
                TextView bt = (TextView) v.findViewById(R.id.bottomtext);

                if (tt != null) {
                    tt.setText(o.getTitle());

                }
                if (bt != null) {
                    if(ingredientsString !=null) {
                        bt.setText("Missing ingredients: " + o.getMissing());
                    }
                }
            }
            return v;
        }
    }

    class Recipes {
        private String title, spoonacularSourceUrl;
        private String missing;


        public Recipes(String title, String missing, String spoonacularSourceUrl) {
            this.title = title;
            this.missing = missing;
            this.spoonacularSourceUrl = spoonacularSourceUrl.replace("http","https");

        }

        public String getTitle() { return title; }
        public String getMissing() { return missing; }
        public String getLink() { return spoonacularSourceUrl; }

    }

}
