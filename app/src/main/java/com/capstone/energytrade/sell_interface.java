package com.capstone.energytrade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class sell_interface extends AppCompatActivity {

    private ImageButton return_sell;
    private Button sell_sell;

    private EditText price_sell;
    private EditText quantity_sell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_interface);
        return_sell = (ImageButton) findViewById(R.id.return_sell);
        sell_sell = (Button) findViewById(R.id.sell_sell);

        return_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sell_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextpage_sell = new Intent(sell_interface.this, sell_complete.class);
                startActivity(nextpage_sell);
            }
        });

    }
}
