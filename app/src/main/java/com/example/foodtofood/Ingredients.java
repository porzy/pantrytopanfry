package com.example.foodtofood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Ingredients extends AppCompatActivity {
    private Toolbar toolbar;
    private SharedPreferences sharedPrefs;
    private int theme;
    final int STANDARD_REQUEST_CODE = 1;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<Ingredient> arrayList;
    IngredientsAdapter ingredientsAdapter;
    Ingredient ingredient;
    ArrayAdapter<Ingredient> arrayAdapter;
    ArrayList<String> keys;
    private ListView lv_ingredient;
    private Button btn_search;
    private EditText et_search;

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
        setContentView(R.layout.activity_ingredients);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ingredient = new Ingredient();
        keys = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Ingredients");

        arrayList = new ArrayList<>();
        lv_ingredient = findViewById(R.id.lv_ingredient);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ingredient = ds.getValue(Ingredient.class);
                    arrayList.add(ingredient);
                    keys.add(ds.getKey());
                    Log.d("tyler", "onDataChange: ");

                }
                ingredientsAdapter = new IngredientsAdapter(Ingredients.this, R.layout.ingredient_item, arrayList);
                lv_ingredient.setAdapter(ingredientsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //ingredientsAdapter = new IngredientsAdapter(this, R.layout.ingredient_item, arrayList);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String value = dataSnapshot.getValue().toString();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    private class IngredientsAdapter extends ArrayAdapter<Ingredient> {

        private ArrayList<Ingredient> items;

        public IngredientsAdapter(Context context, int textViewResourceId, ArrayList<Ingredient> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        //This method is called once for every item in the ArrayList as the list is loaded.
        //It returns a View -- a list item in the ListView -- for each item in the ArrayList
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.ingredient_item, null);
            }
            Ingredient o = items.get(position);
            if (o != null) {
                TextView tt = (TextView) v.findViewById(R.id.toptext);
                CheckBox cb = (CheckBox) v.findViewById(R.id.checkbox);

                if (tt != null) {
                    tt.setText(o.getName());
                }

                if (cb != null){

                        cb.setOnCheckedChangeListener(null);
                        cb.setChecked(o.isChecked());
                        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                databaseReference.child(keys.get(position)).child("checked").setValue(b);
                                //Log.d("tyler", "onItemCheckedStateChanged: "+b);
                            }
                        });
                    }
                }
            return v;
        }
    }
}
