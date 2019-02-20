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

    private ImageButton return_setup;
    private Button batterylevel_setup;
    private Button btb_setup;
    private Button btu_setup;
    private Button sell_setup;
    private ImageButton setup_setup;

    private TextView storage_setup;
    private TextView mybalance_setup;

    private InputStream stateofcharge = null;

    private ProgressDialog progress;

    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;

    String address = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //public static final String EXTRA_ADDRESS_btb = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_interface);
        Intent newint = getIntent();
        address = newint.getStringExtra(btPairing.EXTRA_ADDRESS);
        new setup_interface.ConnectBT().execute();

        return_setup = (ImageButton) findViewById(R.id.return_setup);
        batterylevel_setup = (Button) findViewById(R.id.batterylevel_setup);
        btb_setup = (Button) findViewById(R.id.btb_setup);
        btu_setup = (Button) findViewById(R.id.btu_setup);
        sell_setup = (Button) findViewById(R.id.sell_setup);
        setup_setup = (ImageButton) findViewById(R.id.setup_setup);



        return_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returntomain_setup = new Intent(setup_interface.this, btPairing.class);
                startActivity(returntomain_setup);
            }
        });
        batterylevel_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStateofCharge();
            }
        });

        btb_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent btbInterface = new Intent(setup_interface.this, tradeEnergy.class);
//                btbInterface.putExtra(EXTRA_ADDRESS_btb, address);
                startActivity(btbInterface);
            }
        });

        btu_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent btuInterface = new Intent(setup_interface.this, buytouse.class);
                startActivity(btuInterface);
            }
        });

        sell_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sellInterface = new Intent(setup_interface.this, sell_interface.class);
                startActivity(sellInterface);
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
