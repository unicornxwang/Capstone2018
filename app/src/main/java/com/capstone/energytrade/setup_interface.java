package com.capstone.energytrade;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class setup_interface extends AppCompatActivity {

    private Button batteryLevel;
    private Button buyInterface;
    private Button sellInterface;
    private ImageButton setUp;

    private TextView storage;
    private TextView myBalance2;

    private InputStream stateofcharge = null;

    private ProgressDialog progress;

    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;

    String address = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_interface);
        Intent newint = getIntent();
        address = newint.getStringExtra(btPairing.EXTRA_ADDRESS);
        new setup_interface.ConnectBT().execute();

//        batteryLevel = (Button) findViewById(R.id.batteryLevel);
//        buyInterface = (Button) findViewById(R.id.buyInterface);
//        sellInterface = (Button) findViewById(R.id.sellInterface);
//        setUp = (ImageButton) findViewById(R.id.setUp);

        batteryLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStateofCharge();
            }
        });

        buyInterface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent buyInterface = new Intent(setup_interface.this, tradeEnergy.class);
                startActivity(buyInterface);
            }
        });

        sellInterface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sellInterface = new Intent(setup_interface.this, sell_interface.class);
                startActivity(sellInterface);
            }
        });

        setUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent systemSetUp = new Intent(setup_interface.this, system_setup.class);
            }
        });
    }

    private void requestStateofCharge() {
                if(btSocket!=null){
            try
            {
                btSocket.getOutputStream().write('4');
                btSocket.getOutputStream().write('\n');
//                stateofcharge = btSocket.getInputStream();
//                storage.setText((CharSequence) stateofcharge);
            }
            catch (IOException E)
            {
                msg("Error");
            }
        }
    }

    //-------------------------------------------------------//

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setup_interface, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(setup_interface.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection

                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
