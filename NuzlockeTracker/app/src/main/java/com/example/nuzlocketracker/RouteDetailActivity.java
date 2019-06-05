package com.example.nuzlocketracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.HashSet;
import java.util.TreeSet;

import static com.example.nuzlocketracker.RouteDetailFragment.ARG_ITEM_ID;

/**
 * An activity representing a single Route detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RouteListActivity}.
 */
public class RouteDetailActivity extends AppCompatActivity {


    private SharedPreferences global;
    private SharedPreferences saveFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);


        global = getSharedPreferences("Current",0);
        saveFile = getSharedPreferences(global.getString("recent",""), 0);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ARG_ITEM_ID,
                    getIntent().getStringExtra(ARG_ITEM_ID));
            RouteDetailFragment fragment = new RouteDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.route_detail_container, fragment)
                    .commit();
        }


    }

    public void capture(View button){
        Log.d("RDA", "capture");

        HashSet<String> empty = new HashSet<>();

        HashSet<String> pokemon = new HashSet<>();
        HashSet<String> routes = new HashSet<>();

        pokemon.addAll(saveFile.getStringSet("catches", empty));
        RadioGroup pokeRadio = (RadioGroup) findViewById(R.id.PokemonRadios);
        RadioButton pickedPoke = (RadioButton) pokeRadio.getChildAt(pokeRadio.getCheckedRadioButtonId());
        pokemon.add(pickedPoke.getText().toString());

        routes.addAll(saveFile.getStringSet("routeList", empty));

        String itemName = getIntent().getStringExtra(ARG_ITEM_ID);
        routes.add(itemName);


        SharedPreferences.Editor saveEdit = saveFile.edit();
        saveEdit.putStringSet("catches",pokemon);
        saveEdit.putStringSet("routeList", routes);

        saveEdit.commit();

        SharedPreferences current = getSharedPreferences("Current",0);
        SharedPreferences.Editor curEd = current.edit();
        curEd.putStringSet("catches",pokemon);
        curEd.putStringSet("routeList",routes);

        curEd.commit();

    }

    public void KO(View button){
        HashSet<String> empty = new HashSet<>();

        HashSet<String> routes = new HashSet<>();


        routes.addAll(saveFile.getStringSet("routeList", empty));
        String itemName = getIntent().getStringExtra(ARG_ITEM_ID);
        routes.add(itemName);

        SharedPreferences.Editor saveEdit = saveFile.edit();
        saveEdit.putStringSet("routeList", routes);

        saveEdit.commit();

        SharedPreferences current = getSharedPreferences("Current",0);
        SharedPreferences.Editor curEd = current.edit();
        curEd.putStringSet("routeList",routes);

        curEd.commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, RouteListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
