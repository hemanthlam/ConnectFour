package com.example.hemanthlam.connectfour;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class BoardTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_type);
    }

    public void boardType(View button){
        Intent intent=null;
        Toast.makeText(this, "grid button clicked", Toast.LENGTH_SHORT).show();
        switch (button.getId()){
            case R.id.board_76:
                intent = new Intent(getApplicationContext(), Game1Activity.class);
                break;
            case R.id.board_87:
                intent = new Intent(getApplicationContext(), Game2Activity.class);
                break;
            case R.id.board_108:
                intent = new Intent(getApplicationContext(), Game3Activity.class);
                break;
        }
        startActivity(intent);

    }
}