package com.example.androidlabs;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.androidlabs.ProfileActivity.ACTIVITY_NAME;


public class ChatRoomActivity extends AppCompatActivity {

    Button send;
    Button receive;
    EditText type;
    TextView sText;
    TextView rText;

    ChatAdapter adt;
    Message newMessage;
    ArrayList<Message> msg = new ArrayList<>();
    MyDatabaseOpenHelper dbOpener;
    Cursor results;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        //adapter object
        adt = new ChatAdapter();
        ListView theList = findViewById(R.id.the_list);

        send =  findViewById(R.id.send);
        receive = findViewById(R.id.receive);
        type = findViewById(R.id.type);
        sText = findViewById(R.id.sendtext);
        rText = findViewById(R.id.receivetext);


        //String typed = type.getText().toString();

        //get a database
        dbOpener = new MyDatabaseOpenHelper(this);
        SQLiteDatabase db = dbOpener.getWritableDatabase();

        //query all the results from the database:
        String [] columns = {MyDatabaseOpenHelper.COL_ID, MyDatabaseOpenHelper.COL_MESSAGE, MyDatabaseOpenHelper.COL_TYPE};
        results = db.query(false, MyDatabaseOpenHelper.TABLE_NAME, columns, null, null, null, null, null, null);

        //find the column indices:
        int idColIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_ID);
        int messageColumnIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_MESSAGE);
        int typeColIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_TYPE);

        //iterate over the results, return true if there is a next item:
        while(results.moveToNext())
        {

            String t = results.getString(typeColIndex);
            String m = results.getString(messageColumnIndex);
            long id = results.getLong(idColIndex);

            //add the new newMessage to the list:
            if(t.equals("false")){
                msg.add(new Message(m,false, id));
            }else if(t.equals("true")){
                msg.add(new Message(m,true, id));
            }

        }


        send.setOnClickListener(a->{
//            message = new Message(type.getText().toString(),true,id);
//            msg.add(message);


            //add to the database and get the new ID
            ContentValues newRowValues = new ContentValues();
            //put string message in the MESSAGE column:
            newRowValues.put(MyDatabaseOpenHelper.COL_MESSAGE, type.getText().toString());
            //put boolean type in the TYPE column:
            newRowValues.put(MyDatabaseOpenHelper.COL_TYPE, "true");
            //insert in the database:
            long newId = db.insert(MyDatabaseOpenHelper.TABLE_NAME, null, newRowValues);

            //add the new contact to the list:
            newMessage = new Message(type.getText().toString(),true, newId);

            //add the new newMessage to the list:
            msg.add(newMessage);

            adt.notifyDataSetChanged();
            type.setText("");

        });

        receive.setOnClickListener(a->{
//            message = new Message(type.getText().toString(), false,id);
//            Log.e(ACTIVITY_NAME, message.message);
//            msg.add(message);


            //add to the database and get the new ID
            ContentValues newRowValues = new ContentValues();
            //put string message in the MESSAGE column:
            newRowValues.put(MyDatabaseOpenHelper.COL_MESSAGE, type.getText().toString());
            //put boolean type in the TYPE column:
            newRowValues.put(MyDatabaseOpenHelper.COL_TYPE, "false");
            //insert in the database:
            long newId = db.insert(MyDatabaseOpenHelper.TABLE_NAME, null, newRowValues);

            //add the new contact to the list:
            newMessage = new Message(type.getText().toString(),false, newId);
            //add the new newMessage to the list:
            msg.add(newMessage);

            adt.notifyDataSetChanged();
            type.setText("");

        });

        theList.setAdapter(adt);
        printCursor(results);
    }


    public void printCursor(Cursor c){
        Log.d("PrintCursor", "The database version number: " + dbOpener.VERSION_NUM);
        Log.d("PrintCursor", "The number of columns in the cursor: " + results.getColumnCount());
        Log.d("PrintCursor", "The name of the columns in the cursor: "+ Arrays.toString(results.getColumnNames()));
        Log.d("PrintCursor", "The number of results in the cursor: " + results.getCount());
        Log.d("PrintCursor", "Each row of results in the cursor: " );
        if(results.moveToFirst()) {
            do {
                String str="";
                for (String data : results.getColumnNames()) {

                    str = str.concat(results.getString(results.getColumnIndex(data))).concat(" | ");

                }
                Log.d("PrintCursor", str);
            } while (results.moveToNext());
        }

    }

    protected class ChatAdapter<E> extends BaseAdapter{

        @Override
        public int getCount() {
            return msg.size();
        }

        @Override
        public Message getItem(int position) {
            return msg.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //ViewHolder holder;
            LayoutInflater inflater = getLayoutInflater();
            //convertView = inflater.inflate(R.layout.activity_chat_room,null);

            View newView = null;
            TextView row;

            newMessage = getItem(position);
//            newView = inflater.inflate(R.layout.history_message, parent, false);
//            TextView rowMessage = newView.findViewById(R.id.row_message);
//            TextView rowType = newView.findViewById(R.id.row_type);
//            TextView rowID = newView.findViewById(R.id.row_id);
//
//
//            rowMessage.setText("Message:"+ newMessage.getMessage());
//            rowType.setText("isSend?:"+ String.valueOf(newMessage.getIsSend()));
//            rowID.setText("ID:"+ newMessage.getId());

                if(newMessage.getIsSend()==true){

                    newView = inflater.inflate(R.layout.send_message, parent, false);
                    row = newView.findViewById(R.id.sendtext);
                    //TextView rowType = newView.findViewById(R.id.row_type);
                    //TextView rowID = newView.findViewById(R.id.row_id);
                    String show = newMessage.getMessage();
                    //rowType.setText("isSend?:"+ String.valueOf(newMessage.getIsSend()));
                    //rowID.setText("ID:"+ newMessage.getId());
                    row.setText(show);

                }else {
                    newView = inflater.inflate(R.layout.receive_message, parent, false);
                    row = newView.findViewById(R.id.receivetext);
                    //TextView rowType = newView.findViewById(R.id.row_type);
                    //TextView rowID = newView.findViewById(R.id.row_id);
                    String show = newMessage.getMessage();
                    //rowType.setText("isSend?:"+ String.valueOf(newMessage.getIsSend()));
                    //rowID.setText("ID:"+ newMessage.getId());
                    row.setText(show);

                }


            return newView;
        }
    }


}
