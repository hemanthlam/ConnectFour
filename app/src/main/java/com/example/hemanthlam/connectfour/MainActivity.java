package com.example.hemanthlam.connectfour;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

   /*     Button playbutton = (Button) findViewById(R.id.XX);
        XX.setOnClickListener(new View.OnClickListener()){

            @Override
            public void onClick(View v){
                Intent startIntent = new Intent(getApplicationContext(), XX.class);
                startActivity(startIntent);
            }
        }*/
    }
}
