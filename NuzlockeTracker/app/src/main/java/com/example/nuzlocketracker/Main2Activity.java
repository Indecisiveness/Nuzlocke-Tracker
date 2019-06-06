package com.example.nuzlocketracker;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.resources.TextAppearance;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class Main2Activity extends AppCompatActivity {

    View.OnClickListener myClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myClick = new View.OnClickListener() {
            public void onClick(View v) {
                goPokemon(v);
            }
        };

        fillSpace();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void goPokemon(View v){
        Button b = (Button)v;
        String name = b.getText().toString();
        Intent goPoke = new Intent(this,PokemonDetailScreen.class);
        goPoke.putExtra("name",name);
        startActivity(goPoke);
    }

    public void addPokemon(View v){


    }


    private void fillSpace(){
        LinearLayout myBox = findViewById(R.id.PokeContainer);
        SharedPreferences global = getSharedPreferences("Current", 0);
        SharedPreferences myPokes = getSharedPreferences((global.getString("recent","")),0);
        Log.d("FileName", global.getString("recent",""));

        HashSet<String> empty = new HashSet<>();
        HashSet<String> allPokenames = new HashSet<>(myPokes.getStringSet("catches",empty));

        HashSet<String> deadPokenames = new HashSet<>(myPokes.getStringSet("deaths",empty));

        for (String name: allPokenames){

            if (!deadPokenames.contains(name)) {
                Button pokeButton = new Button(this);
                pokeButton.setText(name);
                pokeButton.setOnClickListener(myClick);
                myBox.addView(pokeButton);
            }
        }

        for (String name:deadPokenames){
            Button deadPoke = new Button(this);
            deadPoke.setText(name);
            deadPoke.setBackgroundColor(Color.RED);
            deadPoke.setActivated(false);
            myBox.addView(deadPoke);
        }



    }



}
