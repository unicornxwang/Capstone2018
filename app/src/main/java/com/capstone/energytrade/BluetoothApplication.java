package com.capstone.energytrade;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;


public class BluetoothApplication extends Application {

    BluetoothAdapter myBluetooth = null;
    private boolean isBtConnected = false;
    private String address_setup = null;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private boolean ConnectSuccess = true;
    private static BluetoothApplication sInstance;

    public static BluetoothApplication getApplication() {
        return sInstance;
    }

    BluetoothSocket btSocket = null;

    public void onCreate() {
        super.onCreate();

        sInstance = this;
    }

    public void setupBluetoothConnection(String address_setup)
    {
        // Either setup your connection here, or pass it in

            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address_setup);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection

                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
                msg("Bluetooth Connection Failed.");
//                Intent failtoconnect = new Intent (this, btPairing.class);
//                startActivity(failtoconnect);
            }
    }

    public BluetoothSocket getCurrentBluetoothConnection()
    {
        return btSocket;
    }
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
