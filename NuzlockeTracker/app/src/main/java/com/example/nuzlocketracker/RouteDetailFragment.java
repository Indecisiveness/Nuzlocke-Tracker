package com.example.nuzlocketracker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.nuzlocketracker.dummy.DummyContent;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.net.ssl.HttpsURLConnection;

/**
 * A fragment representing a single Route detail screen.
 * This fragment is either contained in a {@link RouteListActivity}
 * in two-pane mode (on tablets) or a {@link RouteDetailActivity}
 * on handsets.
 */
public class RouteDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_URL_ID = "item_url";
    public static final String ARG_BOOL_ID = "item_bool";

    private SharedPreferences global;
    private SharedPreferences saveFile;

    private class Route {
        String name;
        boolean caught;
        String locURL;

    }

    private SortedSet<String> pokemon =  Collections.synchronizedSortedSet(new TreeSet<String>());
    private ArrayList<String>[] pokeFloors;

    /**
     * The dummy content this fragment is presenting.
     */
    private Route mItem;

    private boolean caught;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RouteDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        global = getActivity().getSharedPreferences("Current",0);
        saveFile = getActivity().getSharedPreferences(global.getString("recent",""), 0);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            String itemName = (getArguments().getString(ARG_ITEM_ID));

            SharedPreferences.Editor glEd= global.edit();
            glEd.putString("currRoute", itemName);
            glEd.commit();

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(itemName);
            }
        }
        if (getArguments().containsKey(ARG_URL_ID)) {
            String itemURL = (getArguments().getString(ARG_URL_ID));
            populate(itemURL);
        }
        if (getArguments().containsKey(ARG_BOOL_ID)){
            caught = getArguments().getBoolean(ARG_BOOL_ID);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.route_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.route_detail)).setText(mItem.locURL);
        }

        return rootView;
    }

    private void populate(String URL) {
        try {
            new AreaRESTCall().execute(new URL(URL));
        } catch (Exception e) {
            Log.d("error", e.getMessage());
        }
    }


    private void fillBox(){
        Log.d("RDF", "fillBox");

        RadioGroup pokeRadio = (RadioGroup) getActivity().findViewById(R.id.PokemonRadios);
        RadioGroup alreadyGot = (RadioGroup) getActivity().findViewById(R.id.AlreadyGot);
        SortedSet<String> currPokes =  Collections.synchronizedSortedSet(new TreeSet<String>());

        for (ArrayList<String> s:pokeFloors) {
            currPokes.addAll(s);
        }

        for (String p:currPokes){
            if (!pokemon.contains(p)) {
                RadioButton pokeRad = new RadioButton(getActivity());
                pokeRad.setId(View.generateViewId());
                pokeRad.setText(p);
                if (saveFile.getStringSet("catches", pokemon).contains(p)) {
                    pokeRad.setClickable(false);
                    pokeRad.setPaintFlags(pokeRad.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    alreadyGot.addView(pokeRad);
                } else {
                    pokeRadio.addView(pokeRad);
                }
            }
        }

        pokemon = currPokes;

        if (caught){
            Button capture = getActivity().findViewById(R.id.addPoke);
            capture.setClickable(false);
        }

    }


    public void capture(View button){
        Log.d("RDF", "capture");

        HashSet<String> empty = new HashSet<>();
        HashSet<String> pokemon = new HashSet<>();
        HashSet<String> routes = new HashSet<>();

        pokemon.addAll(saveFile.getStringSet("catches", empty));
        RadioGroup pokeRadio = (RadioGroup) getActivity().findViewById(R.id.PokemonRadios);
        RadioButton pickedPoke = (RadioButton) pokeRadio.getChildAt(pokeRadio.getCheckedRadioButtonId());
        pokemon.add(pickedPoke.getText().toString());

        routes.addAll(saveFile.getStringSet("routeList", empty));

        String itemName = getActivity().getIntent().getStringExtra(ARG_ITEM_ID);
        routes.add(itemName);


        SharedPreferences.Editor saveEdit = saveFile.edit();
        saveEdit.putStringSet("catches",pokemon);
        saveEdit.putStringSet("routeList", routes);

        saveEdit.commit();


    }

    public void KO(View button){
        HashSet<String> empty = new HashSet<>();
        HashSet<String> routes = new HashSet<>();


        routes.addAll(saveFile.getStringSet("routeList", empty));
        String itemName = getActivity().getIntent().getStringExtra(ARG_ITEM_ID);
        routes.add(itemName);


        SharedPreferences.Editor saveEdit = saveFile.edit();
        saveEdit.putStringSet("routeList", routes);

        saveEdit.commit();

    }


    private class AreaRESTCall extends AsyncTask<URL, Void, Void> {
        @Override
        protected Void doInBackground(URL... routeURL) {
            try {
                HttpsURLConnection encountCon = (HttpsURLConnection) routeURL[0].openConnection();
                if (encountCon.getResponseCode() == 200) {
                    InputStream readReg = encountCon.getInputStream();
                    java.util.Scanner regScan = new java.util.Scanner(readReg).useDelimiter("\\A");
                    String allLocs = regScan.next();
                    regScan.close();
                    JSONObject locData = new JSONObject(allLocs);
                    JSONArray areaData = (JSONArray) locData.get("areas");
                    for (int i = 0; i < areaData.length(); i++) {
                        new EncountRESTCall().execute(new URL(areaData.getJSONObject(i).getString("url")));
                    }
                    if (!locData.isNull("next")) {
                        new AreaRESTCall().execute(new URL((String) locData.get("next")));
                    }
                }
            } catch (Exception e) {
            } finally {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Void nothing) {
        }
    }

    private class EncountRESTCall extends AsyncTask<URL, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(URL... routeURL) {
            ArrayList<String> pokemonList = new ArrayList<>();
            String version = global.getString("gameName", "");
            try {
                HttpsURLConnection encountCon = (HttpsURLConnection) routeURL[0].openConnection();
                if (encountCon.getResponseCode() == 200) {
                    InputStream readReg = encountCon.getInputStream();
                    java.util.Scanner regScan = new java.util.Scanner(readReg).useDelimiter("\\A");
                    String allEncounters = regScan.next();
                    regScan.close();
                    readReg.close();
                    JSONObject locData = new JSONObject(allEncounters);
                    JSONArray encounterData = (JSONArray) locData.get("pokemon_encounters");
                    for (int i = 0; i < encounterData.length(); i++) {
                        JSONObject pokemon = encounterData.getJSONObject(i);
                        boolean versionMatch = false;
                        JSONArray versionDetails = (JSONArray) pokemon.get("version_details");
                        for (int j = 0; j < versionDetails.length(); j++) {
                            JSONObject versionDetailsItem = (JSONObject) versionDetails.getJSONObject(j).get("version");
                            if (version.equals(versionDetailsItem.get("name").toString())) {
                                versionMatch = true;
                                j = versionDetails.length();
                            }
                        }
                        if (versionMatch) {

                            JSONObject jsonPokemon = (JSONObject) pokemon.get("pokemon");

                            String pokeName = jsonPokemon.getString("name");

                            pokemonList.add(pokeName);
                        }
                    }
                    if (!locData.isNull("next")) {
                        new EncountRESTCall().execute(new URL((String) locData.get("next")));
                    }
                }
            } catch (Exception e) {
                Log.d("Error", e.getMessage());
            } finally {
                return pokemonList;
            }
        }

            @Override
            protected void onPostExecute (ArrayList<String> pokeList) {
                if (pokeFloors == null) {
                    pokeFloors = new ArrayList[1];
                    pokeFloors[0] = pokeList;
                } else {
                    int len = pokeFloors.length;
                    ArrayList<String>[] newFloors = new ArrayList[len + 1];
                    for (int i = 0; i < len; i++) {
                        newFloors[i] = pokeFloors[i];
                    }
                    newFloors[len] = pokeList;
                    pokeFloors = newFloors;
                }
                fillBox();
            }

        }


}

