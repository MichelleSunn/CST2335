package com.example.androidlabs;

import android.app.Activity;
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
import java.util.List;

import static com.example.androidlabs.ProfileActivity.ACTIVITY_NAME;


public class ChatRoomActivity extends AppCompatActivity {

    Button send;
    Button receive;
    EditText type;
    TextView sText;
    TextView rText;

    ChatAdapter adt;
    ArrayList<Message> msg = new ArrayList<>();
    Message message;

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


        send.setOnClickListener(a->{
            message = new Message(type.getText().toString(),1);
            //sText.setText(typed);
            msg.add(message);


            adt.notifyDataSetChanged();
            type.setText("");

        });

        receive.setOnClickListener(a->{
            message = new Message(type.getText().toString(),2);
            //rText.setText(typed);
            Log.e(ACTIVITY_NAME, message.content);
            msg.add(message);

            adt.notifyDataSetChanged();
            type.setText("");

        });
        theList.setAdapter(adt);
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

            message = getItem(position);

            if(message.type == message.TYPE_SEND){
                
                newView = inflater.inflate(R.layout.send_message, parent, false);
                row = newView.findViewById(R.id.sendtext);
                String show = getItem(position).toString();
                row.setText(show);

                //holder.sendT.setText(message.content);


            }else if(message.type == message.TYPE_RECEIVE){
                newView = inflater.inflate(R.layout.receive_message, parent, false);
                row = newView.findViewById(R.id.receivetext);
                String show = getItem(position).toString();
                row.setText(show);
                //holder.receiveT.setText(message.content);
            }

            return newView;
        }
    }

    class Message{
        static final int TYPE_SEND = 1;
        static final int TYPE_RECEIVE = 2;
        String content;
        int type;

        Message(String content, int type){
            this.content = content;
            this.type = type;
        }

        @Override
        public String toString(){
            return content;
        }
    }
}
