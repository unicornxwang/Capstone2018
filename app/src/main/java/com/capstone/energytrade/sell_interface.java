package com.capstone.energytrade;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class sell_interface extends AppCompatActivity {

    private ImageButton return_sell;
    private Button sell_sell;

    private EditText price_sell;
    private EditText quantity_sell;

    private BluetoothSocket btSocket_sell = BluetoothApplication.getApplication().getCurrentBluetoothConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_interface);

        return_sell = (ImageButton) findViewById(R.id.return_sell);
        sell_sell = (Button) findViewById(R.id.sell_sell);
        price_sell = (EditText) findViewById(R.id.price_sell);
        quantity_sell = (EditText) findViewById(R.id.quantity_sell);

        return_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sell_sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btSocket_sell!=null){
                    try{
                        btSocket_sell.getOutputStream().write('0');
                        btSocket_sell.getOutputStream().write('/');
                        btSocket_sell.getOutputStream().write(price_sell.getText().toString().getBytes());
                        btSocket_sell.getOutputStream().write('/');
                        btSocket_sell.getOutputStream().write(quantity_sell.getText().toString().getBytes());
                        btSocket_sell.getOutputStream().write('\n');
                    } catch (IOException e) {
                        msg("Error");
                    }
                }

                system_status.sellStatus = false;
                Intent nextpage_sell = new Intent(sell_interface.this, sell_complete.class);
                startActivity(nextpage_sell);
            }
        });

    }
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
