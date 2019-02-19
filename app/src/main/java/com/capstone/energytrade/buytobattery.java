package com.capstone.energytrade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;

public class buytobattery extends AppCompatActivity {

    private EditText price_btb;
    private EditText quantity_btb;
    private Button confirm_btb;

    String offerPrice = null;
    String offerQuantity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buytobattery);

        Intent btbint = getIntent();
        offerPrice = btbint.getStringExtra(tradeEnergy.btbOfferPrice);
        price_btb.setText(offerPrice);
        offerQuantity = btbint.getStringExtra(tradeEnergy.btbOfferQuantity);
        quantity_btb.setText(offerQuantity);

        confirm_btb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent btb_complete = new Intent(buytobattery.this, buytobattery_complete.class);
                startActivity(btb_complete);
            }
        });


    }
}
