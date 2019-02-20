package com.capstone.energytrade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.nio.Buffer;

public class buytobattery_complete extends AppCompatActivity {

    private Button returntomain_btbc;
    private TextView text1_btbc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buytobattery_complete);

        returntomain_btbc = (Button) findViewById(R.id.returntomain_btbc);
        text1_btbc = (TextView) findViewById(R.id.text1_btbc);

        returntomain_btbc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returntomain_btbc = new Intent(buytobattery_complete.this, setup_interface.class);
                startActivity(returntomain_btbc);
            }
        });

    }
}
