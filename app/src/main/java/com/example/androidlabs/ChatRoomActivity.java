package com.example.androidlabs;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    SQLiteDatabase db;
    Cursor results;

    private static int ACTIVITY_CHATFRAGMENT = 40;
    private static int ACTIVITY_PHONEFRAGMENT = 33;
    public static final int EMPTY_ACTIVITY = 345;
    //protected SQLiteDatabase db = null;

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
        db = dbOpener.getWritableDatabase();

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

        boolean isTablet = findViewById(R.id.fragmentLocation) != null;
        //theAdapter = new ArrayAdapter<Message>(this, android.R.layout.simple_list_item_1, msg);
        theList.setAdapter( adt );
        theList.setOnItemClickListener((parent, view, position, id)-> {
            //positionClicked = position;
            //databaseID = id;
            Bundle dataToPass = new Bundle();

            dataToPass.putString("Message", msg.get(position).getMessage());
            dataToPass.putLong("id", id);
            if(isTablet)
            {
                ChatFragment dFragment = new ChatFragment(); //add a DetailFragment
                //dFragment.msg.setText("");
                //dFragment.databaseiID.setText("");
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                dFragment.setTablet(true);  //tell the fragment if it's running on a tablet or not
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                        .addToBackStack("AnyName") //make the back button undo the transaction
                        .commit(); //actually load the fragment.
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(ChatRoomActivity.this, EmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivityForResult(nextActivity,EMPTY_ACTIVITY); //make the transition
            }


        });
        adt.notifyDataSetChanged();
        printCursor(results);
    }

    //This function only gets called on the phone. The tablet never goes to a new activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == EMPTY_ACTIVITY)
        {
            if(resultCode == RESULT_OK) //if you hit the delete button instead of back button
            {

                long id = data.getLongExtra("id", 0);
                Log.i("Delete this message1:" , " id="+id);

                deleteMessageId((int)id);
            }
        }
    }

    public void deleteMessageId(int id)
    {

        Log.i("Delete this message:" , " id="+id);
        msg.remove(id);
        String str="";
        Cursor c;
        String [] cols = {MyDatabaseOpenHelper.COL_ID, MyDatabaseOpenHelper.COL_MESSAGE, MyDatabaseOpenHelper.COL_TYPE};
        c = db.query(false, MyDatabaseOpenHelper.TABLE_NAME, cols, null, null, null, null, null, null);
        if(c.moveToFirst()) {

                for (int i =0; i<id; i++) {
                    c.moveToNext();

                }
            str = c.getString(c.getColumnIndex("_id"));
        }
        int x = db.delete("Message", "_id=?", new String[] {str});
        Log.i("ViewContact", "Deleted " + x + " rows");
        //db.delete(dbOpener.TABLE_NAME, dbOpener.COL_ID+"=?", new String[] {String.valueOf(id)});
        adt.notifyDataSetChanged();
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
