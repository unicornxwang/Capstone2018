package com.capstone.energytrade;

import android.app.PictureInPictureParams;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.util.Timer;
import java.util.TimerTask;

public class buytobattery_complete extends AppCompatActivity {

    private ProgressBar progressbar_btbc;
    private Button progresscheck_btbc;
    private Button returntomain_btbc;
    private TextView text1_btbc;
    private InputStream inStream_btbc = null;

    private BluetoothSocket btSocket_btbc = BluetoothApplication.getApplication().getCurrentBluetoothConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buytobattery_complete);

        progressbar_btbc = (ProgressBar) findViewById(R.id.progressbar_btbc);
        progresscheck_btbc = (Button) findViewById(R.id.progresscheck_btbc);
        returntomain_btbc = (Button) findViewById(R.id.returntomain_btbc);
        text1_btbc = (TextView) findViewById(R.id.text1_btbc);


        progresscheck_btbc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressCheck();
            }
        });

        returntomain_btbc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returntomain_btbc = new Intent(buytobattery_complete.this, setup_interface.class);
                startActivity(returntomain_btbc);
            }
        });

    }

    private void progressCheck() {
        if(btSocket_btbc!=null) {
            try {
                btSocket_btbc.getOutputStream().write('7');
                btSocket_btbc.getOutputStream().write('\n');
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try
                                {
                                    inStream_btbc = btSocket_btbc.getInputStream();
                                    int inStreamAvailable = inStream_btbc.available();
                                    if (inStreamAvailable > 0) {
                                        byte[] packetBytes_btbc = new byte[inStreamAvailable];
                                        inStream_btbc.read(packetBytes_btbc);
                                        System.out.println(packetBytes_btbc);
                                        String[] socInfo = new String(packetBytes_btbc).split("/");
                                        String stateofcharge_btbc = socInfo[1].trim();
                                        System.out.println(socInfo);
                                        text1_btbc.setText(stateofcharge_btbc + "%");

                                        progressbar_btbc.setProgress(Math.round(Float.parseFloat(stateofcharge_btbc)));
                                    }
                                } catch (IOException E)
                                {
                                    msg("Error");
                                }
                            }
                        });

                    }

                }, 1000);//Update text every second

            }catch (IOException e)
            {
                msg("Error");
            }
        }
//        if (btSocket_btbc != null) {
//            try {
//                btSocket_btbc.getOutputStream().write('4');
//                btSocket_btbc.getOutputStream().write('\n');
//
//                inStream_btbc = btSocket_btbc.getInputStream();
//                int inStreamAvailable_btbc = inStream_btbc.available();
//                if (inStreamAvailable_btbc > 0) {
//                    byte[] packetBytes_btbc = new byte[inStreamAvailable_btbc];
//                    inStream_btbc.read(packetBytes_btbc);
//                    String[] socInfo = new String(packetBytes_btbc).split("/");
//                    String stateofcharge_btbc = socInfo[2];
//                    text1_btbc.setText(stateofcharge_btbc + "%");
//                    progressbar_btbc.setProgress(Integer.parseInt(stateofcharge_btbc));
//                }
//
//
//            }catch (IOException e)
//            {
//                msg("Error");
//            }
//        }

    }

    private void msg (String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}

