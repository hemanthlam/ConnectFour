package com.example.hemanthlam.connectfour;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.view.View;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button playbutton = (Button) findViewById(R.id.playbutton);
        playbutton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent startIntent = new Intent(getApplicationContext(), BoardTypeActivity.class);
                startActivity(startIntent);
            }
        });
    }
}