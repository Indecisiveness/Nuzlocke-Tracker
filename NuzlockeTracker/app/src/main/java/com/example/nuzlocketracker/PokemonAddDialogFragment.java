package com.example.nuzlocketracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class PokemonAddDialogFragment extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final EditText etSearch = new EditText(getContext());
        builder.setView(etSearch)
                .setMessage("Enter a Pokemon name")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Main2Activity myAct = (Main2Activity) getActivity();
                        myAct.findName(etSearch.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PokemonAddDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
