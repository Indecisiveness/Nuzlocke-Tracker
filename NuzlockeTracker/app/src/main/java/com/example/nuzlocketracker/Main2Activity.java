package com.example.nuzlocketracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Iterator;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    private void fillSpace(){
        TextView myText = (TextView) findViewById(R.id.AllPokemon);
        SharedPreferences global = getSharedPreferences("Current", 0);
        SharedPreferences myPokes = getSharedPreferences((global.getString("recent","")),0);
        Log.d("FileName", global.getString("recent",""));

        HashSet<String> empty = new HashSet<>();
        HashSet<String> allPokenames =(HashSet<String>) myPokes.getStringSet("catches",empty);
        StringBuilder s = new StringBuilder();


        Iterator<String> seqPoke = allPokenames.iterator();

        while(seqPoke.hasNext()){
            String name = seqPoke.next();
            Log.d("Pokename",name);
            s.append(name);
        }
        myText.setText(s);
    }


}
