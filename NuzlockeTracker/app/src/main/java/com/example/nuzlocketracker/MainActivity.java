package com.example.nuzlocketracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.navigation.Navigation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ArrayList<String> versionList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        versionList.add("Version");

        try {
            buildVersionSpinner();
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadSpinner();

        Spinner loader = (Spinner)findViewById(R.id.FileOpener);
        loader.setOnItemSelectedListener(this);
        Spinner games = (Spinner) findViewById(R.id.VersionSpinner);
        games.setOnItemSelectedListener(this);
    }

    void buildVersionSpinner() throws Exception {
        String urlString = "https://pokeapi.co/api/v2/version";
        URL versURL = new URL(urlString);
        new RESTCall().execute(versURL);
    }

    void updateSpinner() {
        Spinner versSpin = (Spinner) findViewById(R.id.VersionSpinner);
        ArrayAdapter<String> versAdapt = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, versionList);
        versSpin.setAdapter(versAdapt);
    }

    void loadSpinner(){
        Spinner loadSpin = (Spinner) findViewById(R.id.FileOpener);
        SharedPreferences files = getSharedPreferences("Files",0);
        int noFiles = files.getInt("count",0);
        ArrayList<String> nameList = new ArrayList<>();
        nameList.add("Select...");
        for (int i = 0; i<noFiles; i++){
            nameList.add(files.getString((""+i), "Name"));
        }
        ArrayAdapter<String> loadAdapt = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, nameList);
        loadSpin.setAdapter(loadAdapt);
    }


    public void cont(View v){
        SharedPreferences global = getSharedPreferences("Current", 0);
        String fileName = global.getString("recent", null);
        int gameVal = global.getInt("recGame", 0);
        if (fileName == null || gameVal == 0){
            return;
        }
        SharedPreferences file = getSharedPreferences(fileName,0);
        updateCurrent(file);
        Intent forward = new Intent(this, RouteListActivity.class);
        startActivity(forward);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        SharedPreferences page = getPreferences(0);
        SharedPreferences.Editor gEdit = page.edit();


        if (parent.getId() == R.id.VersionSpinner) {
            gEdit.putInt("GameID", pos);
            gEdit.putString("GameName", parent.getItemAtPosition(pos).toString());
        }
        if (parent.getId() == R.id.FileOpener){
            gEdit.putString("FileName", parent.getItemAtPosition(pos).toString());
            SharedPreferences file = getSharedPreferences(parent.getItemAtPosition(pos).toString(),0);
        }

        gEdit.commit();

    }
    public void onNothingSelected(AdapterView<?> parent){}


    public void begin(View v){
        Spinner gamePick = (Spinner) findViewById(R.id.VersionSpinner);
        EditText nameBox = (EditText) findViewById(R.id.editText);
        Editable name = nameBox.getText();


        if (!name.toString().equals("Name")) {
            SharedPreferences global = getSharedPreferences("Current", 0);
            SharedPreferences.Editor newGlobal = global.edit();
            newGlobal.putString("recent", name.toString());
            newGlobal.commit();
            SharedPreferences file = getSharedPreferences(name.toString(), 0);
            if (file.getString("name", null) != null) {
                return;
            }
            else {
                SharedPreferences.Editor fileMake = file.edit();
                fileMake.putString("name", name.toString());
                fileMake.putInt("gameNo", getPreferences(0).getInt("GameID", 0));
                fileMake.putString("GameName", getPreferences(0).getString("GameName", ""));
                HashSet<String> catches = new HashSet<>();
                HashSet<String> routeList = new HashSet<>();
                fileMake.putStringSet("catches", catches);
                fileMake.putStringSet("routeList", routeList);
                fileMake.commit();

                SharedPreferences local = getSharedPreferences("Files",0);
                SharedPreferences.Editor locEd = local.edit();
                int count = local.getInt("count", 0);
                locEd.putString(""+count, name.toString());
                count++;
                locEd.putInt("count", count);
                locEd.commit();

                updateCurrent(file);
                Intent forward = new Intent(this, RouteListActivity.class);
                startActivity(forward);
            }
        }

    }

    public void load(View v){
        SharedPreferences page = getPreferences(0);
        String fileName = page.getString("FileName","Name");
        SharedPreferences file = getSharedPreferences(fileName, 0);
        updateCurrent(file);
        Intent forward = new Intent(this, RouteListActivity.class);
        startActivity(forward);
    }

    private void updateCurrent(SharedPreferences openFile){
        SharedPreferences global = getSharedPreferences("Current",0);
        SharedPreferences.Editor newGlobal = global.edit();
        Set<String> empty = new HashSet<>();

        newGlobal.putString("recent", openFile.getString("name", "MyName"));
        newGlobal.putInt("recGame", openFile.getInt("gameNo", 0));
        newGlobal.putString("gameName", openFile.getString("GameName",""));
        newGlobal.commit();

    }





    private class RESTCall extends AsyncTask<URL, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(URL... versURL) {
            ArrayList<String> versList = new ArrayList<>();
            try{
            HttpsURLConnection myConnection = (HttpsURLConnection) versURL[0].openConnection();
                if (myConnection.getResponseCode() == 200) {
                    InputStream read =myConnection.getInputStream();
                    java.util.Scanner scan = new java.util.Scanner(read).useDelimiter("\\A");
                    String jsonString = scan.next();
                    scan.close();
                    read.close();
                    JSONObject versions = new JSONObject(jsonString);
                    JSONArray versArray = (JSONArray) versions.get("results");
                    for (int i = 0; i<versArray.length(); i++){
                        String s = (String) versArray.getJSONObject(i).get("name");
                        versList.add(s);
                    }
                    if (!versions.isNull("next")){
                        new RESTCall().execute(new URL((String)versions.get("next")));
                    }
                }
                return versList;
            } catch (Exception e) {
                ArrayList<String> error = new ArrayList<String>();
                Log.d("Error",e.getMessage());
                return error;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            versionList.addAll(strings);
            updateSpinner();
        }
    }



}