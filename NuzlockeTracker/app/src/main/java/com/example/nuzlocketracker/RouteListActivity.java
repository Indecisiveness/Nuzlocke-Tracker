package com.example.nuzlocketracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.nuzlocketracker.dummy.DummyContent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.net.ssl.HttpsURLConnection;

import static android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS;
import static android.text.TextUtils.CAP_MODE_WORDS;
import static com.example.nuzlocketracker.RouteDetailFragment.ARG_ITEM_ID;

/**
 * An activity representing a list of Routes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RouteDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RouteListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private class Route {
        String name;
        boolean caught;
        String locURL;

    }

    private SharedPreferences global;
    private SharedPreferences saveFile;


    HashMap<String,Route> currRoute = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        global = getSharedPreferences("Current",0);
        saveFile = getSharedPreferences(global.getString("recent",""), 0);



        if (findViewById(R.id.route_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.route_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        callRouteList();

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        HashMap<String,Route> copyRoute = (HashMap<String,Route>) currRoute.clone();
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, copyRoute, mTwoPane));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final RouteListActivity mParentActivity;
        private final SortedSet<String> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Route item = (Route) view.getTag();


                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(RouteDetailFragment.ARG_ITEM_ID, item.name);
                    arguments.putString(RouteDetailFragment.ARG_URL_ID, item.locURL);
                    arguments.putBoolean(RouteDetailFragment.ARG_BOOL_ID,item.caught);
                    RouteDetailFragment fragment = new RouteDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.route_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, RouteDetailActivity.class);
                    intent.putExtra(RouteDetailFragment.ARG_ITEM_ID, item.name);
                    intent.putExtra(RouteDetailFragment.ARG_URL_ID, item.locURL);
                    intent.putExtra(RouteDetailFragment.ARG_BOOL_ID,item.caught);

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(RouteListActivity parent,
                                      HashMap<String,Route> routes,
                                      boolean twoPane) {
            mValues = new TreeSet<String>();
            mValues.addAll(routes.keySet());
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.route_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Object[] myRoutes = mValues.toArray();

            HashMap<String,Route> currRoute = mParentActivity.currRoute;

            char[] cleanText = myRoutes[position].toString().toCharArray();
            cleanText[0] = Character.toUpperCase(cleanText[0]);
            for (int i = 0; i<cleanText.length;i++){
                if (cleanText[i] == '-'){
                    cleanText[i] = ' ';
                    cleanText[i+1] = Character.toUpperCase(cleanText[i+1]);
                }
            }


            holder.mIdView.setText(new String(cleanText));

            if (currRoute.get(myRoutes[position]).caught){
                holder.mContentView.setChecked(true);
            }
            else {
                holder.mContentView.setChecked(false);
            }



            holder.itemView.setTag(currRoute.get(myRoutes[position]));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        


        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final CheckBox mContentView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = view.findViewById(R.id.checkBox);
            }
        }
    }

    public void capture(View button){

        HashSet<String> empty = new HashSet<>();
        HashSet<String> routes = new HashSet<>();
        HashSet<String> pokemon = new HashSet<>();

        HashSet<String> livePoke = new HashSet<>();

        pokemon.addAll(saveFile.getStringSet("catches", empty));
        livePoke.addAll(saveFile.getStringSet("live",empty));

        RadioGroup pokeRadio = findViewById(R.id.PokemonRadios);
        RadioButton pickedPoke = pokeRadio.findViewById(pokeRadio.getCheckedRadioButtonId());
        if(pickedPoke!=null) {
            pokemon.add(pickedPoke.getText().toString());
            livePoke.add(pickedPoke.getText().toString());
        }

        routes.addAll(saveFile.getStringSet("routeList", empty));
        String routeName = global.getString("currRoute","");

        routes.add(routeName);


        SharedPreferences.Editor saveEdit = saveFile.edit();
        saveEdit.putStringSet("catches",pokemon);
        saveEdit.putStringSet("routeList", routes);
        saveEdit.putStringSet("live",livePoke);

        saveEdit.commit();


        Route rt = currRoute.get(routeName);;
        rt.caught = true;
        currRoute.put(routeName,rt);
        setupRecyclerView((RecyclerView)findViewById(R.id.route_list));

        Intent goPoke = new Intent(this, PokemonDetailScreen.class);
        goPoke.putExtra("name",pickedPoke.getText().toString());
        startActivity(goPoke);


    }

    public void KO(View button){
        HashSet<String> empty = new HashSet<>();
        HashSet<String> routes = new HashSet<>();

        routes.addAll(saveFile.getStringSet("routeList",empty));
        String routeName = global.getString("currRoute","");
        routes.add(routeName);
        SharedPreferences.Editor saveEdit = saveFile.edit();
        saveEdit.putStringSet("routeList", routes);

        saveEdit.commit();

        Route rt = currRoute.get(routeName);;
        rt.caught = true;
        currRoute.put(routeName,rt);
        setupRecyclerView((RecyclerView)findViewById(R.id.route_list));

    }

public void openParty(View view){
    Intent forward = new Intent(this, Main2Activity.class);
    startActivity(forward);
}


    private void callRouteList(){
        int vers = global.getInt("recGame",0);
        String versName = global.getString("gameName","");

        try {
            if (vers != 0) {
                URL versLoc = new URL("https://pokeapi.co/api/v2/version/" + vers + "/");
                new RESTCall().execute(versLoc);
            }
        }
        catch (Exception e){
            Log.d("Error", e.getMessage());
        }
    }

    private class RESTCall extends AsyncTask<URL, Void, Void> {
        @Override
        protected Void doInBackground(URL... versLoc) {
            try {
                HttpsURLConnection myConnection = (HttpsURLConnection) versLoc[0].openConnection();
                if (myConnection.getResponseCode() == 200) {
                    InputStream read = myConnection.getInputStream();
                    java.util.Scanner scan = new java.util.Scanner(read).useDelimiter("\\A");
                    String jsonString = scan.next();
                    scan.close();
                    JSONObject version = new JSONObject(jsonString);
                    JSONObject versGroup = (JSONObject) version.get("version_group");
                    String versGroupName = versGroup.getString("name");
                    SharedPreferences.Editor gEd = global.edit();
                    gEd.putString("versGroup",versGroupName);
                    gEd.commit();

                    URL versURL = new URL(versGroup.getString("url"));
                    HttpsURLConnection subCon = (HttpsURLConnection) versURL.openConnection();
                    if (subCon.getResponseCode() == 200) {
                        InputStream readSub = subCon.getInputStream();
                        java.util.Scanner subScan = new java.util.Scanner(readSub).useDelimiter("\\A");
                        String allVers = subScan.next();
                        subScan.close();
                        JSONObject allGroup = new JSONObject(allVers);
                        JSONArray locales = (JSONArray) allGroup.get("regions");
                        for (int i = 0; i < locales.length(); i++) {
                            URL region = new URL(locales.getJSONObject(i).get("url").toString());
                            new RouteRESTCall().execute(region);
                        }
                    }
                }
            } catch (Exception e) {
                Log.d("Error", e.getMessage());
            }
            finally {
                return null;
            }
        }

    }

    private class RouteRESTCall extends AsyncTask<URL, Void, HashMap<String,Route>> {
        @Override
        protected HashMap<String,Route> doInBackground(URL... versURL) {
            HashSet<String> blank = new HashSet<>();
            HashMap<String,Route> rtHold = new HashMap<>();
            try{
                HttpsURLConnection regCon = (HttpsURLConnection) versURL[0].openConnection();
                if (regCon.getResponseCode() == 200) {
                    InputStream readReg = regCon.getInputStream();
                    java.util.Scanner regScan = new java.util.Scanner(readReg).useDelimiter("\\A");
                    String allLocs = regScan.next();
                    regScan.close();
                    JSONObject regLocs = new JSONObject(allLocs);
                    JSONArray locs = (JSONArray) regLocs.get("locations");
                    for (int i = 0; i<locs.length(); i++){
                        String rtName = locs.getJSONObject(i).getString("name");
                        Route newRt = new Route();
                        newRt.name = rtName;
                        if (saveFile.getStringSet("routeList", blank).contains(rtName)){
                            newRt.caught = true;
                        }
                        else {
                            newRt.caught = false;
                        }
                        newRt.locURL = locs.getJSONObject(i).getString("url");
                        rtHold.put(rtName,newRt);
                    }

                    if (!regLocs.isNull("next")) {
                        new RESTCall().execute(new URL((String) regLocs.get("next")));
                    }
                }
            } catch (Exception e) {
                Log.d("Error",e.getMessage());
            }
            finally {
                return rtHold;
            }
        }

        @Override
        protected void onPostExecute(HashMap<String,Route> rtHold) {
            currRoute.putAll(rtHold);
            View recyclerView = findViewById(R.id.route_list);
            assert recyclerView != null;
            setupRecyclerView((RecyclerView) recyclerView);
        }
    }

}
