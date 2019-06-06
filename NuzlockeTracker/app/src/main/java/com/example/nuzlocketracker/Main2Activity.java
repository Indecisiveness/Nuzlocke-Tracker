package com.example.nuzlocketracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.resources.TextAppearance;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class Main2Activity extends AppCompatActivity{

    View.OnClickListener myClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Pokemon Box");

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
                addPokemon(view);
            }
        });
    }

    public void goPokemon(View v) {
        Button b = (Button) v;
        String name = b.getText().toString();
        Intent goPoke = new Intent(this, PokemonDetailScreen.class);
        goPoke.putExtra("name", name);
        startActivity(goPoke);
    }

    public void addPokemon(View v) {
        PokemonAddDialogFragment getPoke = new PokemonAddDialogFragment();
        getPoke.show(getSupportFragmentManager(),"PokemonAddDialogFragment");
    }

    private void newPoke(String pokeName){
        Log.d("newPoke","entered");

        SharedPreferences global = getSharedPreferences("Current",0);
        SharedPreferences saveFile = getSharedPreferences(global.getString("recent",""),0);

        HashSet<String> live = new HashSet<>();
        HashSet<String> caught = new HashSet<>();

        live.addAll(saveFile.getStringSet("live",live));
        caught.addAll(saveFile.getStringSet("catches",caught));

        live.add(pokeName);
        caught.add(pokeName);

        SharedPreferences.Editor savEd = saveFile.edit();
        savEd.putStringSet("live",live);
        savEd.putStringSet("catches",caught);
        savEd.commit();

        Intent goPoke = new Intent(this, PokemonDetailScreen.class);
        goPoke.putExtra("name", pokeName);
        startActivity(goPoke);
    }

    public void goRoutes(View v) {
        Intent routeMove = new Intent(this, RouteListActivity.class);
        startActivity(routeMove);
    }

    private void fillSpace() {
        LinearLayout myBox = findViewById(R.id.PokeContainer);
        SharedPreferences global = getSharedPreferences("Current", 0);
        SharedPreferences myPokes = getSharedPreferences((global.getString("recent", "")), 0);

        HashSet<String> empty = new HashSet<>();
        HashSet<String> livePokeenames = new HashSet<>(myPokes.getStringSet("live", empty));

        HashSet<String> deadPokenames = new HashSet<>(myPokes.getStringSet("deaths", empty));

        for (String name : livePokeenames) {

            Button pokeButton = new Button(this);
            pokeButton.setText(name);
            pokeButton.setOnClickListener(myClick);
            myBox.addView(pokeButton);
        }

        for (String name : deadPokenames) {
            Button deadPoke = new Button(this);
            deadPoke.setText(name);
            deadPoke.setBackgroundColor(Color.RED);
            deadPoke.setActivated(false);
            myBox.addView(deadPoke);
        }


    }

    public void findName(String name){
        new checkName().execute(name);
    }

    public class checkName extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... pokeName) {
            String val = "";
            try {
                URL pokeURL = new URL("https://pokeapi.co/api/v2/pokemon/" + pokeName[0].toLowerCase());
                HttpsURLConnection myConnection = (HttpsURLConnection) pokeURL.openConnection();
                Log.d("ResponseCode", Integer.toString(myConnection.getResponseCode()));

                if (myConnection.getResponseCode() == 200) {
                    val = pokeName[0];
                }
            } catch (Exception e) {
                Log.d("Error", "checkName failed");
                val = "";
            } finally {
                return val;
            }
        }

        @Override
        protected void onPostExecute(String pokeName) {

            if (!pokeName.equals("")){
                newPoke(pokeName);
            }
        }


    }


}
