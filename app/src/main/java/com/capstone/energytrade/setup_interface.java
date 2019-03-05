package com.capstone.energytrade;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
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


public class setup_interface extends AppCompatActivity {

    private ImageButton return_setup;
    private Button batterylevel_setup;
    private Button btb_setup;
    private Button btu_setup;
    private Button sell_setup;
    private ImageButton setup_setup;

    private TextView storage_setup;

    private InputStream stateofcharge = null;

    private ProgressDialog progress;

    BluetoothAdapter myBluetooth = null;
    private BluetoothSocket btSocket_setup = null;

    private String address_setup = null;
    public static final String EXTRA_ADDRESS_btb = "Device Address btb";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_interface);
        Intent newint = getIntent();
        address_setup = newint.getStringExtra(btPairing.EXTRA_ADDRESS);
        btSocket_setup = BluetoothApplication.getApplication().getCurrentBluetoothConnection();

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
                btbInterface.putExtra(EXTRA_ADDRESS_btb, address_setup);
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
                if(btSocket_setup!=null){
            try
            {
                btSocket_setup.getOutputStream().write('4');
                btSocket_setup.getOutputStream().write('\n');
                stateofcharge = btSocket_setup.getInputStream();
                storage_setup.setText((CharSequence) stateofcharge);
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
