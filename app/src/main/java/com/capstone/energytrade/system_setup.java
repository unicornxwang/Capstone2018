package com.capstone.energytrade;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class system_setup extends AppCompatActivity {

    private Button update_system;
    private ImageButton return_system;
    private EditText myport_system;
    private EditText batterysize_system;
    private EditText port1_system;
    private EditText port2_system;
    private EditText port3_system;
    private TextView text1_system;
    private TextView text2_system;
    private TextView text3_system;
    private TextView text4_system;
    private TextView text5_system;
    private TextView text6_system;
    private BluetoothSocket btSocket_system = BluetoothApplication.getApplication().getCurrentBluetoothConnection();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_setup);

        update_system = (Button) findViewById(R.id.update_system);
        return_system = (ImageButton) findViewById(R.id.return_system);
        batterysize_system = (EditText)findViewById(R.id.batterysize_system);
        myport_system = (EditText) findViewById(R.id.myport_system);
        port1_system = (EditText)findViewById(R.id.port1_system);
        port2_system = (EditText)findViewById(R.id.port2_system);
        port3_system = (EditText)findViewById(R.id.port3_system);


        return_system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent returntomain_system = new Intent(system_setup.this, setup_interface.class);
//                startActivity(returntomain_system);
                finish();
            }
        });

        update_system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                systemUpdate();
            }
        });

    }

    private void systemUpdate() {
        if(btSocket_system != null){
            try{
                btSocket_system.getOutputStream().write('3');
                btSocket_system.getOutputStream().write('/');

                btSocket_system.getOutputStream().write(myport_system.getText().toString().getBytes());
                btSocket_system.getOutputStream().write('/');

                btSocket_system.getOutputStream().write(batterysize_system.getText().toString().getBytes());

                btSocket_system.getOutputStream().write('/');

                if(port1_system.getText().toString().trim() == "")
                {
                    btSocket_system.getOutputStream().write('0');
                } else {
                    btSocket_system.getOutputStream().write(port1_system.getText().toString().trim().getBytes());
                }
                btSocket_system.getOutputStream().write('/');

                if(port2_system.getText().toString().trim() == "")
                {
                    btSocket_system.getOutputStream().write('0');
                } else {
                    btSocket_system.getOutputStream().write(port2_system.getText().toString().trim().getBytes());
                }
                btSocket_system.getOutputStream().write('/');

                if(port3_system.getText().toString().trim() == "")
                {
                    btSocket_system.getOutputStream().write('0');
                } else {
                    btSocket_system.getOutputStream().write(port3_system.getText().toString().trim().getBytes());
                }
                btSocket_system.getOutputStream().write('\n');

            }catch (IOException e)
            {
                msg("Error");
            }
        }
    }
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
