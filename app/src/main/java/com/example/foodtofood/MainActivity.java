package com.example.foodtofood;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SharedPreferences sharedPrefs;
    private int theme, stringStart = 0;
    final int STANDARD_REQUEST_CODE = 1;
    Button btn_ingredients, btn_recipes;
    private String recipeString = "water", typeString = "main dish";
    CheckBox cb_ingredients;
    RadioButton rb_main, rb_side, rb_dessert, rb_salad, rb_appetizer, rb_breakfast, rb_snack;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<Ingredient> ingredientList;
    Ingredient ingredient;
    ArrayAdapter<Ingredient> arrayAdapter;
    ArrayList<String> keys;
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
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ingredient = new Ingredient();
        keys = new ArrayList<>();

        btn_ingredients = findViewById(R.id.btn_ingredients);
        btn_recipes = findViewById(R.id.btn_recipes);
        rb_main = findViewById(R.id.rb_main);
        rb_side = findViewById(R.id.rb_side);
        rb_appetizer = findViewById(R.id.rb_appetizer);
        rb_dessert = findViewById(R.id.rb_dessert);
        rb_breakfast = findViewById(R.id.rb_breakfast);
        rb_salad = findViewById(R.id.rb_salad);
        rb_snack = findViewById(R.id.rb_snack);
        cb_ingredients = findViewById(R.id.cb_ingredients);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Ingredients");

        ingredientList = new ArrayList<>();

        btn_recipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Recipe.class);
                if(cb_ingredients.isChecked()) {
                    intent.putExtra("ingredients", recipeString);
                }
                intent.putExtra("type", typeString);

                startActivityForResult(intent, STANDARD_REQUEST_CODE);
            }
        });
        btn_ingredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Ingredients.class);
                startActivityForResult(intent, STANDARD_REQUEST_CODE);
            }
        });

        EventHandler eventHandler = new EventHandler();
        rb_main.setOnCheckedChangeListener(eventHandler);
        rb_side.setOnCheckedChangeListener(eventHandler);
        rb_appetizer.setOnCheckedChangeListener(eventHandler);
        rb_dessert.setOnCheckedChangeListener(eventHandler);
        rb_breakfast.setOnCheckedChangeListener(eventHandler);
        rb_salad.setOnCheckedChangeListener(eventHandler);
        rb_snack.setOnCheckedChangeListener(eventHandler);
        cb_ingredients.setOnCheckedChangeListener(eventHandler);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ingredient = ds.getValue(Ingredient.class);
                    if(ingredient.isChecked() == true) {
                        recipeString += ", " + ingredient.getName();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("tyler", "onStop");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("tyler", "onStart");
        Log.d("tyler", "night checked? " + theme);
    }

    @Override
    protected void onResume() {
        super.onResume();


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
                case R.id.rb_main:
                    Log.d("tyler", "breakfast checked? " + b);
                    if (b == true) {
                        typeString = "main course";
                    }
                    break;
                case R.id.rb_appetizer:
                    Log.d("tyler", "breakfast checked? " + b);
                    if (b == true) {
                        typeString = "appetizer";
                    }
                    break;
                case R.id.rb_breakfast:
                    Log.d("tyler", "breakfast checked? " + b);
                    if (b == true) {
                        typeString = "breakfast";
                    }
                    break;
                case R.id.rb_dessert:
                    Log.d("tyler", "breakfast checked? " + b);
                    if (b == true) {
                        typeString = "dessert";
                    }
                    break;
                case R.id.rb_salad:
                    Log.d("tyler", "breakfast checked? " + b);
                    if (b == true) {
                        typeString = "salad";
                    }
                    break;
                case R.id.rb_side:
                    Log.d("tyler", "breakfast checked? " + b);
                    if (b == true) {
                        typeString = "side dish";
                    }
                    break;
                case R.id.rb_snack:
                    Log.d("tyler", "breakfast checked? " + b);
                    if (b == true) {
                        typeString = "snack";
                    }
                    break;


            }
        }
    }


}
