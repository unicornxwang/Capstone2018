package com.capstone.energytrade;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class buytouse_complete extends AppCompatActivity {

    private TextView text1_btuc;
    private Button cancel_btuc;
    private BluetoothSocket btSocket_btuc = BluetoothApplication.getApplication().getCurrentBluetoothConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buytouse_complete);

        text1_btuc = (TextView) findViewById(R.id.text1_btuc);
        cancel_btuc = (Button) findViewById(R.id.cancel_btuc);

        cancel_btuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btSocket_btuc != null){
                    try{
                        btSocket_btuc.getOutputStream().write('5');
                        btSocket_btuc.getOutputStream().write('\n');
                    } catch (IOException e){
                        msg("Error");
                    }

                }

                finish();
            }
        });
    }
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
