package com.capstone.energytrade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class sell_complete extends AppCompatActivity {

    private TextView text1_sellcomplete;
    private Button stop_sellcomplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_complete);

        text1_sellcomplete = (TextView) findViewById(R.id.text1_sellcomplete);
        stop_sellcomplete = (Button) findViewById(R.id.stop_sellcomplete);

        stop_sellcomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
