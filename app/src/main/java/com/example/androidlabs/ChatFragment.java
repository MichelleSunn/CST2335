package com.example.androidlabs;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChatFragment extends Fragment {

    private boolean isTablet;
    private Bundle dataFromActivity;
    private long id;

    EditText msg;
    TextView databaseiID;
    Button deleteB;


    public static final int PUSHED_DELETE = 35;
    public void setTablet(boolean tablet) { isTablet = tablet; }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {

        dataFromActivity = getArguments();

        View result =  inflater.inflate(R.layout.activity_chat_fragment, container, false);
        msg = result.findViewById(R.id.msg);
        databaseiID = result.findViewById(R.id.id);
        id = dataFromActivity.getLong("id");
        deleteB = result.findViewById(R.id.delete);

        msg.setText(dataFromActivity.getString("Message"));
        databaseiID.setText("ID="+id);


        deleteB = (Button)result.findViewById(R.id.delete);
        deleteB.setOnClickListener( clk -> {


            if(isTablet) { //both the list and details are on the screen:
                ChatRoomActivity parent = (ChatRoomActivity) getActivity();
                parent.deleteMessageId((int)id); //this deletes the item and updates the list

                //now remove the fragment since you deleted it from the database:
                // this is the object to be removed, so remove(this):
                parent.getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
            //for Phone:
            else //You are only looking at the details, you need to go back to the previous list page
            {
                EmptyActivity parent = (EmptyActivity) getActivity();
                Intent backToFragmentExample = new Intent();
                backToFragmentExample.putExtra("id", dataFromActivity.getLong("id" ));

                parent.setResult(Activity.RESULT_OK, backToFragmentExample); //send data back to FragmentExample in onActivityResult()
                parent.finish(); //go back
            }
            msg.setText("");
            databaseiID.setText("");
        });
        return result;

    }
}
