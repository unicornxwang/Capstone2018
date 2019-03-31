package com.capstone.energytrade;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class buytouse extends AppCompatActivity {


    private ImageButton return_btu;
    private TextView text1_btu;
    private Button confirm_btu;
    private InputStream inStream_btu = null;
    private BluetoothSocket btSocket_btu = BluetoothApplication.getApplication().getCurrentBluetoothConnection();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buytouse);
        return_btu = (ImageButton) findViewById(R.id.return_btu);
        confirm_btu = (Button) findViewById(R.id.confirm_btu);
        text1_btu = (TextView) findViewById(R.id.text1_btu);


        return_btu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        confirm_btu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btSocket_btu != null){
                    try{
                        btSocket_btu.getOutputStream().write('4');
                        btSocket_btu.getOutputStream().write('\n');

                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        try
                                        {
                                            inStream_btu = btSocket_btu.getInputStream();
                                            int bytesAvailable_btu = inStream_btu.available();
                                            if(bytesAvailable_btu > 0){
                                               byte[] packetBytes_btu = new byte[bytesAvailable_btu];
                                               inStream_btu.read(packetBytes_btu);
                                               String[] instruction = new String(packetBytes_btu).split("\n");
                                               if(instruction[0].equals("3"))
                                               {
                                                   system_status.btuStatus = false;
                                                   Intent nextpage_btu = new Intent(buytouse.this, buytouse_complete.class);
                                                   startActivity(nextpage_btu);
                                                }
                                            }
                                            else{
                                                msg("No energy is available to buy.");
                                            }

                                        }
                                        catch (IOException e){}

                                    }
                                });


                            }
                        },2000);



                    }
                    catch (IOException e){
                        msg("Error");
                    }

                }

            }
        });

    }
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
