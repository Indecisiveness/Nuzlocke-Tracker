package com.example.nuzlocketracker;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

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

    public void releasePoke(View v){
        SharedPreferences global = getSharedPreferences("Current",0);
        SharedPreferences saveFile = getSharedPreferences(global.getString("recent",""),0);

        HashSet<String> deceased = new HashSet<>();

        deceased.addAll(saveFile.getStringSet("deaths",deceased));

        deceased.add(getIntent().getStringExtra("name"));

        SharedPreferences.Editor saveEd = saveFile.edit();
        saveEd.putStringSet("deaths",deceased);
        saveEd.commit();
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

    private void populateEvo(HashMap<String,String[]> evoMap){
        Set<String> names = evoMap.keySet();

        Log.d("Names", names.toString());

        LinearLayout evoTable = findViewById(R.id.evoLayout);

        TableLayout basic = new TableLayout(this);

        TableLayout stage1 = new TableLayout(this);

        TableLayout stage2 = new TableLayout(this);

        boolean basicUsed = false;

        boolean stage1Used = false;

        boolean stage2Used = false;


        for(String name:names){
            String[] evoCond = evoMap.get(name);
            if (evoCond[0]!=null && evoCond[0].equals("Basic")){
                basicUsed = true;
                TableRow pokeRow = new TableRow(this);
                TextView pokeName = new TextView(this);
                pokeName.setText(name);

                pokeRow.addView(pokeName);
                basic.addView(pokeRow);
            }
            else {
                   TableRow pokeRow = new TableRow(this);
                   TextView pokeName = new TextView(this);
                   TextView method = new TextView(this);
                   pokeName.setText(name+"    ");

                    String s = "";
                    if (evoCond[16]!=null && evoCond[16].equals("level-up")){
                        s = s.concat("level");
                        if (evoCond[0]!=null) {
                            if (evoCond[0].equals("1")) {
                                s = s.concat(" female");
                            }
                            if (evoCond[0].equals("2")) {
                                s = s.concat(" male");
                            }
                        }
                        if (evoCond[1]!=null){
                            s = s.concat(" while holding "+evoCond[1]);
                        }
                        if (evoCond[3]!=null){
                            s = s.concat(" while knowing "+evoCond[3]);
                        }
                        if (evoCond[4]!=null){
                            s = s.concat(" knowing move of type "+evoCond[4]);
                        }
                        if (evoCond[5]!=null){
                            s = s.concat(" in area " +evoCond[5]);
                        }
                        if (evoCond[6]!=null){
                            s = s.concat(" with affection at least "+evoCond[6]);
                        }
                        if (evoCond[7]!=null){
                            s = s.concat(" with beauty at least "+evoCond[7]);
                        }
                        if (evoCond[8]!=null){
                            s = s.concat(" with happiness at least "+evoCond[8]);
                        }
                        if (evoCond[9]!=null){
                            s = s.concat(" to level "+evoCond[9]);
                        }
                        if (evoCond[10]!=null && evoCond[10].equals("true")){
                            s = s.concat(" while raining");
                        }
                        if (evoCond[11]!=null){
                            s = s.concat(" with " +evoCond[11] + " in party");
                        }
                        if (evoCond[12]!= null){
                            s = s.concat(" with Pokemon of type " + evoCond[12]);
                        }
                        if (evoCond[13]!= null){
                            if (evoCond[13].equals("-1")){
                                s = s.concat(" with Defense greater than Attack");
                            }
                            else if (evoCond[13].equals("0")){
                                s = s.concat(" with equal Defense and Attack");
                            }
                            else if (evoCond[13].equals("1")){
                                s = s.concat(" with Attack greater than Defense");
                            }
                        }
                        if (!evoCond[14].equals("")){
                            s = s.concat(" during "+evoCond[14]);
                        }
                        if (evoCond[17].equals("true")){
                            s = s.concat(" while upside down");
                        }
                    }
                    else if (evoCond[16]!=null && evoCond[16].equals("trade")){
                        s = s.concat("Trade");
                        if (evoCond[1]!=null){
                            s = s.concat(" while holding "+evoCond[1]);
                        }
                        if (evoCond[15]!=null){
                            s = s.concat(" in exchange for "+evoCond[15]);
                        }
                    }
                    else if (evoCond[16]!=null && evoCond[16].equals("use-item")){
                        s = s.concat("Use");
                        if (evoCond[2]!=null){
                            s = s.concat(" "+evoCond[2]);
                        }
                    }
                    method.setText(s);
                    pokeRow.addView(pokeName);
                    pokeRow.addView(method);
                if (evoCond[18]!=null && evoCond[18].equals("Stage 1")) {
                    stage1Used = true;
                    stage1.addView(pokeRow);
                }
                else if(evoCond[18]!=null && evoCond[18].equals(("Stage 2"))){
                    stage2Used = true;
                    stage2.addView(pokeRow);
                }
            }
        }
        if (basicUsed) {
            evoTable.addView(basic);
        }
        if (stage1Used) {
            evoTable.addView(stage1);
        }
        if (stage2Used) {
            evoTable.addView(stage2);
        }

    }


    private class pokeDeets extends AsyncTask<String, Void, int[]> {
        @Override
        protected int[] doInBackground(String... pokeName) {
            int[] stats = new int[6];

            new pokeEvos().execute(pokeName);

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

    private class pokeEvos extends AsyncTask<String,Void,HashMap<String,String[]>> {
        @Override
        protected HashMap<String, String[]> doInBackground(String... pokeName) {
            HashMap<String,String[]> evoOrder = new HashMap<>();
            try {
                URL pokeLink = new URL("https://pokeapi.co/api/v2/pokemon-species/" + pokeName[0]);
                HttpsURLConnection myConnection = (HttpsURLConnection) pokeLink.openConnection();
                if(myConnection.getResponseCode() == 200){
                    InputStream read = myConnection.getInputStream();
                    java.util.Scanner scan = new java.util.Scanner(read).useDelimiter("\\A");
                    String jsonString = scan.next();
                    scan.close();
                    JSONObject pokeObject = new JSONObject(jsonString);
                    URL evoChain = new URL(pokeObject.getJSONObject("evolution_chain").getString("url"));
                    HttpsURLConnection evoConnect = (HttpsURLConnection) evoChain.openConnection();
                    if(myConnection.getResponseCode() == 200) {
                        InputStream reader = evoConnect.getInputStream();
                        java.util.Scanner scanner = new java.util.Scanner(reader).useDelimiter("\\A");
                        String jString = scanner.next();
                        scanner.close();
                        JSONObject evoObject = new JSONObject(jString);
                        JSONObject allSpecies = evoObject.getJSONObject("chain");
                        Log.d("basicName",allSpecies.getJSONObject("species").getString("name"));

                        String[] basic = new String[1];
                        basic[0] = "Basic";
                        evoOrder.put(allSpecies.getJSONObject("species").getString("name"),basic);

                        JSONArray evoObj = allSpecies.getJSONArray("evolves_to");
                        for(int i = 0; i<evoObj.length(); i++){
                            JSONObject evoLink = evoObj.getJSONObject(i);
                            Log.d("stage1",evoLink.getJSONObject("species").getString("name"));
                            JSONObject evoMethod = evoLink.getJSONArray("evolution_details").getJSONObject(0);
                            String[] method = new String[19];
                            if (!evoMethod.isNull("gender")) {
                                method[0] = Integer.toString(evoMethod.getInt("gender"));
                            }
                            else {method[0] = null;}
                            if (evoMethod.isNull("held_item")){
                                method[1] = null;
                            }
                            else{
                                method[1] = evoMethod.getJSONObject("held_item").getString("name");
                            }
                            if (evoMethod.isNull("item")){
                                method[2] = null;
                            }
                            else{
                                method[2] = evoMethod.getJSONObject("item").getString("name");
                            }
                            if (evoMethod.isNull("known_move")){
                                method[3] = null;
                            }
                            else{
                                method[3] = evoMethod.getJSONObject("known_move").getString("name");
                            }
                            if (evoMethod.isNull("known_move_type")){
                                method[4] = null;
                            }
                            else{
                                method[4] = evoMethod.getJSONObject("known_move_type").getString("name");
                            }
                            if (evoMethod.isNull("location")){
                                method[5] = null;
                            }
                            else{
                                method[5] = evoMethod.getJSONObject("location").getString("name");
                            }
                            if (!evoMethod.isNull("min_affection")) {
                                method[6] = Integer.toString(evoMethod.getInt("min_affection"));
                            }
                            else {method[6] = null;}
                            if (!evoMethod.isNull("min_beauty")) {
                                method[7] = Integer.toString(evoMethod.getInt("min_beauty"));
                            }
                            else {method[7] = null;}
                            if (!evoMethod.isNull("min_happiness")) {
                                method[8] = Integer.toString(evoMethod.getInt("min_happiness"));
                            }
                            else {method[8] = null;}
                            if (!evoMethod.isNull("min_level")) {
                                method[9] = Integer.toString(evoMethod.getInt("min_level"));
                            }
                            else {method[9] = null;}
                            if (!evoMethod.isNull("needs_overworld_rain")) {
                                method[10] = Boolean.toString(evoMethod.getBoolean("needs_overworld_rain"));
                            }
                            if (evoMethod.isNull("party_species")){
                                method[11] = null;
                            }
                            else{
                                method[11] = evoMethod.getJSONObject("party_species").getString("name");
                            }
                            if (evoMethod.isNull("party_type")){
                                method[12] = null;
                            }
                            else{
                                method[12] = evoMethod.getJSONObject("party_type").getString("name");
                            }
                            if (!evoMethod.isNull("relative_physical_stats")) {
                                method[13] = Integer.toString(evoMethod.getInt("relative_physical_stats"));
                            }
                            else{method[13] = null;}
                            if (!evoMethod.isNull("time_of_day")) {
                                method[14] = evoMethod.getString("time_of_day");
                            }
                            if (evoMethod.isNull("trade_species")){
                                method[15] = null;
                            }
                            else{
                                method[15] = evoMethod.getJSONObject("trade_species").getString("name");
                            }
                            if (evoMethod.isNull("trigger")){
                                method[16] = null;
                            }
                            else{
                                method[16] = evoMethod.getJSONObject("trigger").getString("name");
                            }
                            if (!evoMethod.isNull("turn_upside_down")) {
                                method[17] = Boolean.toString(evoMethod.getBoolean("turn_upside_down"));
                            } else {method[17] = null;}
                            method[18] = "Stage 1";
                            evoOrder.put(evoLink.getJSONObject("species").getString("name"),method);

                            JSONArray evoStage2 = evoLink.getJSONArray("evolves_to");
                            for(int j = 0; j<evoStage2.length(); j++) {
                                JSONObject finalLink = evoStage2.getJSONObject(j);
                                JSONObject finalMethod = finalLink.getJSONArray("evolution_details").getJSONObject(0);
                                String[] methArr = new String[19];
                                if (!finalMethod.isNull("gender")) {
                                    methArr[0] = Integer.toString(finalMethod.getInt("gender"));
                                } else {methArr[0] = null;}
                                if (finalMethod.isNull("held_item")) {
                                    methArr[1] = null;
                                } else {
                                    methArr[1] = finalMethod.getJSONObject("held_item").getString("name");
                                }
                                if (finalMethod.isNull("item")) {
                                    methArr[2] = null;
                                } else {
                                    methArr[2] = finalMethod.getJSONObject("item").getString("name");
                                }
                                if (finalMethod.isNull("known_move")) {
                                    methArr[3] = null;
                                } else {
                                    methArr[3] = finalMethod.getJSONObject("known_move").getString("name");
                                }
                                if (finalMethod.isNull("known_move_type")) {
                                    methArr[4] = null;
                                } else {
                                    methArr[4] = finalMethod.getJSONObject("known_move_type").getString("name");
                                }
                                if (finalMethod.isNull("location")) {
                                    methArr[5] = null;
                                } else {
                                    methArr[5] = finalMethod.getJSONObject("location").getString("name");
                                }
                                if (!finalMethod.isNull("min_affection")) {
                                    methArr[6] = Integer.toString(finalMethod.getInt("min_affection"));
                                } else {methArr[6] = null;}
                                if (!finalMethod.isNull("min_beauty")) {
                                    methArr[7] = Integer.toString(finalMethod.getInt("min_beauty"));
                                } else {methArr[7] = null;}
                                if (!finalMethod.isNull("min_happiness")) {
                                    methArr[8] = Integer.toString(finalMethod.getInt("min_happiness"));
                                } else {methArr[8] = null;}
                                if (!finalMethod.isNull("min_level")) {
                                    methArr[9] = Integer.toString(finalMethod.getInt("min_level"));
                                } else{methArr[9] = null;}
                                if (!finalMethod.isNull("needs_overworld_rain")) {
                                    methArr[10] = Boolean.toString(finalMethod.getBoolean("needs_overworld_rain"));
                                } else{methArr[10] = null;}
                                if (finalMethod.isNull("party_species")) {
                                    methArr[11] = null;
                                } else {
                                    methArr[11] = finalMethod.getJSONObject("party_species").getString("name");
                                }
                                if (finalMethod.isNull("party_type")) {
                                    methArr[12] = null;
                                } else {
                                    methArr[12] = finalMethod.getJSONObject("party_type").getString("name");
                                }
                                if (!finalMethod.isNull("relative_physical_stats")) {
                                    methArr[13] = Integer.toString(finalMethod.getInt("relative_physical_stats"));
                                } else {methArr[13] = null;}
                                methArr[14] = finalMethod.getString("time_of_day");
                                if (finalMethod.isNull("trade_species")) {
                                    methArr[15] = null;
                                } else {
                                    methArr[15] = finalMethod.getJSONObject("trade_species").getString("name");
                                }
                                if (finalMethod.isNull("trigger")) {
                                    methArr[16] = null;
                                } else {
                                    methArr[16] = finalMethod.getJSONObject("trigger").getString("name");
                                }
                                if (!finalMethod.isNull("turn_upside_down")) {
                                    methArr[17] = Boolean.toString(finalMethod.getBoolean("turn_upside_down"));
                                } else{methArr[17] = null;}
                                methArr[18] = "Stage 2";
                                evoOrder.put(finalLink.getJSONObject("species").getString("name"), methArr);
                            }
                        }
                    }
                }
            }
            catch (Exception e){
                Log.d("Error",e.getMessage());
            }
            finally {
                return evoOrder;
            }
        }
        protected void onPostExecute(HashMap<String, String[]> evoOrder) {
            populateEvo(evoOrder);
        }

    }

}

