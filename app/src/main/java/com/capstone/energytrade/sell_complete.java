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

public class sell_complete extends AppCompatActivity {

    private TextView text1_sellcomplete;
    private Button main_sellcomplete;
    private Button stop_sellcomplete;
    private BluetoothSocket btSocket_sellcomplete = BluetoothApplication.getApplication().getCurrentBluetoothConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_complete);

        text1_sellcomplete = (TextView) findViewById(R.id.text1_sellcomplete);
        stop_sellcomplete = (Button) findViewById(R.id.stop_sellcomplete);
        main_sellcomplete = (Button) findViewById(R.id.main_sellcomplete);

        main_sellcomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rtm_sellcomplete = new Intent(sell_complete.this, setup_interface.class);
                startActivity(rtm_sellcomplete);
            }
        });

        stop_sellcomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btSocket_sellcomplete != null){
                    try
                    {
                        btSocket_sellcomplete.getOutputStream().write('6');
                        btSocket_sellcomplete.getOutputStream().write('\n');
                    } catch (IOException e)
                    {
                        msg("Error");
                    }
                }
                system_status.sellStatus = true;
                finish();
            }
        });

    }
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
