package com.example.androidlabs;

import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.app.PendingIntent.getActivity;

public class TestToolbar extends AppCompatActivity {


    String m = "This is the initial message";
    Toolbar tBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_toolbar);
        tBar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(tBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.item1:
                Toast.makeText(this,m,Toast.LENGTH_LONG ).show();
                break;
            case R.id.item2:
                alert();
                break;
            case R.id.item3:
                Snackbar.make(tBar, "Go Back?", Snackbar.LENGTH_LONG).setAction("stop", a->finish()).show();
                break;
            case R.id.item4:
                Toast.makeText(this,"You clicked on the overflow menu",Toast.LENGTH_LONG ).show();
                break;
        }
        return true;

    }

    public void alert(){

        View dia = getLayoutInflater().inflate(R.layout.dialog_box, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        EditText et = (EditText) dia.findViewById(R.id.diaEdit);
        builder.setView(dia);
        builder
                .setPositiveButton("change", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // What to do on Accept

                        m = et.getText().toString();
                        et.setText("");

                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // What to do on Cancel
                        et.setText("");
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }
}
