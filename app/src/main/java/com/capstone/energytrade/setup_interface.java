package com.capstone.energytrade;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;


public class setup_interface extends AppCompatActivity {

    private ImageButton return_setup;
    private Button btb_setup;
    private Button btu_setup;
    private Button sell_setup;
    private ImageButton setup_setup;

    private TextView storage_setup;

    private InputStream stateofcharge = null;
    private ProgressBar progress_setup;

    private BluetoothSocket btSocket_setup = null;
    private InputStream inStream_setup = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_interface);
        Intent newint = getIntent();
        btSocket_setup = BluetoothApplication.getApplication().getCurrentBluetoothConnection();
        return_setup = (ImageButton) findViewById(R.id.return_setup);
        btb_setup = (Button) findViewById(R.id.btb_setup);
        btu_setup = (Button) findViewById(R.id.btu_setup);
        sell_setup = (Button) findViewById(R.id.sell_setup);
        setup_setup = (ImageButton) findViewById(R.id.setup_setup);
        storage_setup = (TextView) findViewById(R.id.storage_setup);
        progress_setup = (ProgressBar) findViewById(R.id.progress_setup);

        requestStateofCharge();

        return_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returntomain_setup = new Intent(setup_interface.this, btPairing.class);
                startActivity(returntomain_setup);
            }
        });


        btb_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(system_status.btbStatus)
                {
                    Intent btbInterface = new Intent(setup_interface.this, tradeEnergy.class);
                    startActivity(btbInterface);
                }
                else {
                    Intent btbcInterface = new Intent(setup_interface.this, buytobattery_complete.class);
                    startActivity(btbcInterface);
                }

            }
        });

        btu_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(system_status.btuStatus)
                {
                    Intent btuInterface = new Intent(setup_interface.this, buytouse.class);
                    startActivity(btuInterface);
                }
                else {
                    Intent btucInterface = new Intent(setup_interface.this, buytouse_complete.class);
                    startActivity(btucInterface);
                }

            }
        });

        sell_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(system_status.sellStatus)
                {
                    Intent sellInterface = new Intent(setup_interface.this, sell_interface.class);
                    startActivity(sellInterface);

                }
                else
                {
                    Intent sellcomplete = new Intent(setup_interface.this, sell_complete.class);
                    startActivity(sellcomplete);
                }
            }
        });

        setup_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent systemSetUp = new Intent(setup_interface.this, system_setup.class);
                startActivity(systemSetUp);
            }
        });
    }

    private void requestStateofCharge() {
                if(btSocket_setup!=null){
                    try
                    {
                        btSocket_setup.getOutputStream().write('7');
                        btSocket_setup.getOutputStream().write('\n');
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override

                            public void run()
                            {
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        try
                                        {
                                            inStream_setup = btSocket_setup.getInputStream();
                                            int inStreamAvailable = inStream_setup.available();
                                            if (inStreamAvailable > 0)
                                            {
                                                byte[]packetBytes_setup = new byte[inStreamAvailable];
                                                inStream_setup.read(packetBytes_setup);
                                                System.out.println(packetBytes_setup);
                                                String[] socInfo = new String (packetBytes_setup).split("/");
                                                String stateofcharge = socInfo[1].trim();
                                                System.out.println(socInfo);
                                                storage_setup.setText(stateofcharge + "%");

                                                progress_setup.setProgress(Math.round(Float.parseFloat(stateofcharge)));
                                            }
                                        }
                                        catch (IOException E)
                                        {
                                            msg("Error");
                                        }
                                    }
                                });

                            }

                        },1000);//Update text every second

                    }

            catch (IOException E)
            {
                msg("Error");
            }
        }
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

}
