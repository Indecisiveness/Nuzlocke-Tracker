package com.example.nuzlocketracker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.net.ssl.HttpsURLConnection;

public class PokemonDetailScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_detail_screen);

        String pokeName = getIntent().getStringExtra("name");

        TextView nameBox = findViewById(R.id.pokeName);
        nameBox.setText(pokeName);


        new pokeDeets().execute(pokeName);

    }

    private void populateMoves(HashMap<String, ArrayList<String>> moveMap){
        ArrayList<String> levelUp = moveMap.get("levelUp");
        ArrayList<String> moveLevels = moveMap.get("moveLevels");
        ArrayList<String> tutor = moveMap.get("tutor");
        ArrayList<String> machine = moveMap.get("machine");

        TableLayout levelTable = findViewById(R.id.pokeLevelMoves);
        TableLayout machineTable = findViewById(R.id.pokeMachineMoves);
        TableLayout tutorTable = findViewById(R.id.pokeTutorMoves);



        int len = levelUp.size();

        for (int i = 0; i<len; i++){
            String level = moveLevels.get(i);
            String moveName = levelUp.get(i);

            TextView nameBox = new TextView(this);
            nameBox.setText(moveName);

            TextView levelBox = new TextView(this);
            levelBox.setText(level);


            TableRow moveEntry = new TableRow(this);
            moveEntry.addView(nameBox);
            moveEntry.addView(levelBox);

            levelTable.addView(moveEntry);
        }

        for(String s:machine){
            TextView nameBox = new TextView(this);
            nameBox.setText(s);

            TableRow moveEntry = new TableRow(this);
            moveEntry.addView(nameBox);

            machineTable.addView(moveEntry);
        }

        for(String s:tutor){
            TextView nameBox = new TextView(this);
            nameBox.setText(s);
            TableRow moveEntry = new TableRow(this);
            moveEntry.addView(nameBox);

            tutorTable.addView(moveEntry);
        }


    }

    private void populateStats(int[] stats){
        TextView hp = findViewById(R.id.hpVal);
        hp.setText(Integer.toString(stats[0]));
        TextView attack = findViewById(R.id.attackVal);
        attack.setText(Integer.toString(stats[1]));
        TextView defense = findViewById(R.id.defenseVal);
        defense.setText(Integer.toString(stats[2]));
        TextView spAtt = findViewById(R.id.spAttVal);
        spAtt.setText(Integer.toString(stats[3]));
        TextView spDef = findViewById(R.id.spDefVal);
        spDef.setText(Integer.toString(stats[4]));
        TextView spd = findViewById(R.id.spdVal);
        spd.setText(Integer.toString(stats[5]));
    }


    private class pokeDeets extends AsyncTask<String, Void, int[]> {
        @Override
        protected int[] doInBackground(String... pokeName) {
            int[] stats = new int[6];
            try {


                URL pokeLink= new URL("https://pokeapi.co/api/v2/pokemon/" + pokeName[0]);
                HttpsURLConnection myConnection = (HttpsURLConnection) pokeLink.openConnection();
                if (myConnection.getResponseCode() == 200) {
                    InputStream read = myConnection.getInputStream();
                    java.util.Scanner scan = new java.util.Scanner(read).useDelimiter("\\A");
                    String jsonString = scan.next();
                    scan.close();
                    JSONObject pokeObject = new JSONObject(jsonString);
                    JSONArray pokeMoves = (JSONArray) pokeObject.get("moves");
                    new moveArranger().execute(pokeMoves);

                    JSONArray pokeStats = (JSONArray) pokeObject.get("stats");
                    for (int i = 0; i < pokeStats.length(); i++) {
                        if (pokeStats.getJSONObject(i).getJSONObject("stat").getString("name").equals("hp")) {
                            stats[0] = pokeStats.getJSONObject(i).getInt("base_stat");
                        }
                        if (pokeStats.getJSONObject(i).getJSONObject("stat").getString("name").equals("attack")) {
                            stats[1] = pokeStats.getJSONObject(i).getInt("base_stat");
                        }
                        if (pokeStats.getJSONObject(i).getJSONObject("stat").getString("name").equals("defense")) {
                            stats[2] = pokeStats.getJSONObject(i).getInt("base_stat");
                        }
                        if (pokeStats.getJSONObject(i).getJSONObject("stat").getString("name").equals("special-attack")) {
                            stats[3] = pokeStats.getJSONObject(i).getInt("base_stat");
                        }
                        if (pokeStats.getJSONObject(i).getJSONObject("stat").getString("name").equals("special-defense")) {
                            stats[4] = pokeStats.getJSONObject(i).getInt("base_stat");
                        }
                        if (pokeStats.getJSONObject(i).getJSONObject("stat").getString("name").equals("speed")) {
                            stats[5] = pokeStats.getJSONObject(i).getInt("base_stat");
                        }
                    }
                }
            } catch (Exception e) {
                Log.d("Error", e.getMessage());
            }
            finally {
                return stats;
            }
        }
        @Override
        protected void onPostExecute(int[] stats) {
            populateStats(stats);
        }

    }

    private class moveArranger extends AsyncTask<JSONArray, Void, HashMap<String,ArrayList<String>>> {
        @Override
        protected HashMap<String, ArrayList<String>> doInBackground(JSONArray... pokeMoves) {
            HashMap<String, ArrayList<String>> moveMap = new HashMap<>();
            ArrayList<String> levelUp = new ArrayList<>();
            ArrayList<String> machine = new ArrayList<>();
            ArrayList<String> tutor = new ArrayList<>();
            ArrayList<String> moveLevels = new ArrayList<>();
            String versionGroup = (getSharedPreferences("Current",0).getString("versGroup",""));


            try {
                int moveCount = pokeMoves[0].length();
                for (int i = 0; i < moveCount; i++) {
                    JSONObject move = pokeMoves[0].getJSONObject(i);
                    JSONArray learnMethod = move.getJSONArray("version_group_details");
                    String methodName = "";
                    int level = 0;
                    for (int j= 0; j<learnMethod.length(); j++){
                        JSONObject method = learnMethod.getJSONObject(j);
                        if (method.getJSONObject("version_group").getString("name").equals(versionGroup)){
                            methodName = method.getJSONObject("move_learn_method").getString("name");
                            if(methodName.equals("level-up")){
                                level = method.getInt("level_learned_at");
                                Log.d("MoveLevel", Integer.toString(level));
                                moveLevels.add(Integer.toString(level));
                                Log.d("moveName", move.getJSONObject("move").getString("name"));
                                levelUp.add(move.getJSONObject("move").getString("name"));
                            }
                            if(methodName.equals("tutor")){
                                tutor.add(move.getJSONObject("move").getString("name"));
                            }
                            if(methodName.equals("machine")){
                                machine.add(move.getJSONObject("move").getString("name"));
                            }
                        }
                    }
                }

            } catch(Exception e) {
                Log.d("Error", e.getMessage());
            }
        finally{
                moveMap.put("levelUp", levelUp);
                moveMap.put("tutor",tutor);
                moveMap.put("machine",machine);
                moveMap.put("moveLevels",moveLevels);

                return moveMap;
            }
        }
        @Override
        protected void onPostExecute(HashMap<String, ArrayList<String>> moveMap) {
            populateMoves(moveMap);
        }

    }
}

