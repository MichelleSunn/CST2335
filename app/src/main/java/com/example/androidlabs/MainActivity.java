package com.example.androidlabs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;
    EditText email;
    Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (EditText) findViewById(R.id.EditEmail);
        sp = getSharedPreferences("profile", Context.MODE_PRIVATE);
        String saved = sp.getString("email","");

        email.setText(saved);
        email.setText(getSharedPreferences("profile", Context.MODE_PRIVATE).getString("email", ""));
        Intent nextPage = new Intent(this, ProfileActivity.class);

        login = (Button)findViewById(R.id.Button);
        login.setOnClickListener(a->{
            startActivity(nextPage);
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sp.edit();

        String typed = email.getText().toString();
        editor.putString("email",typed);

        editor.commit();
    }
}
