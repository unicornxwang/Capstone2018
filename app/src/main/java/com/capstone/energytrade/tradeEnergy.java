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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class tradeEnergy extends AppCompatActivity {

    private ProgressDialog progress;

    private Button BatteryLevel;
    private Button Household1;
    private Button Household2;
    private Button Household3;
    private Button Household4;
    private Button BuyButton;
    private Button Update;
    private Button OfferButton;
    private Button Confirm;


    private TextView MyBalance;
    private TextView Household1Price;
    private TextView Household1kWh;
    private TextView Household2Price;
    private TextView Household2kWh;
    private TextView Household3Price;
    private TextView Household3kWh;

    private EditText Household4Price;
    private EditText Household4kWh;

    private TextView Storage;
    private TextView Wallet;
    private TextView PriceUnit;
    private TextView kWhUnit;
    private EditText kWh;
    private TextView Price;

    private ListView offerList;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;

    String address = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private InputStream inStream = null;
    int readBufferPosition = 0;
    byte[] readBuffer = new byte[1024];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_energy);
        Intent newint = getIntent();
        address = newint.getStringExtra(btPairing.EXTRA_ADDRESS);
        new ConnectBT().execute();


        offerList = (ListView) findViewById(R.id.offerList);
        MyBalance = (TextView) findViewById(R.id.MyBalance);

        Household1 = (Button) findViewById(R.id.Household1);
        Household1.setText(R.string.Household1);
        Household1Price = (TextView) findViewById(R.id.Household1Price);
        Household1kWh = (TextView) findViewById(R.id.Household1kWh);

        Household2 = (Button) findViewById(R.id.Household2);
        Household2.setText(R.string.Household2);
        Household2Price = (TextView) findViewById(R.id.Household2Price);
        Household2kWh = (TextView) findViewById(R.id.Household2kWh);

        Household3 = (Button) findViewById(R.id.Household3);
        Household3.setText(R.string.Household3);
        Household3Price = (TextView) findViewById(R.id.Household3Price);
        Household3kWh = (TextView) findViewById(R.id.Household3kWh);

        Household4 = (Button) findViewById((R.id.Household4));
        Household4.setText(R.string.Household4);
        Household4Price = (EditText) findViewById(R.id.Household4Price);
        Household4kWh = (EditText) findViewById(R.id.Household4kWh);

        BatteryLevel = (Button) findViewById(R.id.BatteryLevel);
        Update = (Button) findViewById(R.id.update);
        BuyButton = (Button) findViewById(R.id.BuyButton);
        BuyButton.setText(R.string.BuyButton);
        OfferButton = (Button) findViewById(R.id.OfferButton);
        OfferButton.setText(R.string.OfferButton);

        Storage = (TextView) findViewById(R.id.Storage);
        Wallet = (TextView) findViewById(R.id.Wallet);
        PriceUnit = (TextView) findViewById(R.id.PriceUnit);
        kWhUnit = (TextView) findViewById(R.id.kWhUnit);
        kWh = (EditText) findViewById(R.id.kWh);
        Price = (TextView) findViewById(R.id.Price);

        Confirm = (Button) findViewById(R.id.Confirm);

        BatteryLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStateofCharge();
            }
        });

        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiveUpdates();
            }
        });

        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double Result = 0.0;
                if (kWh.getText().toString().equals("")) {
                    Price.setText(R.string.ErrorMessage);
                } else {
                    double Kilowatthour = Double.parseDouble(kWh.getText().toString());
                    Result = Kilowatthour;
                    Price.setText("Total: " + String.format("%.2f", Result) + "UGX");
                }


            }
        });

        BuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //loseBalance();

            }
        });


        OfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOffers();

            }
        });

    }

    private void requestStateofCharge() {
        if(btSocket!=null){
            try{
                btSocket.getOutputStream().write(4);
            }catch (IOException E){
                msg("Error");
            }
        }
    }

    private void loseBalance() {
        double FinalBalance = 0.0;
        String NumberOnly = (Price.getText().toString()).replaceAll("[^0-9]", "");
        double Transaction = Double.parseDouble(NumberOnly);
        FinalBalance = -Transaction / 100;
        Wallet.setText(String.format("%.2f", FinalBalance) + "UGX");
    }

    private void gainBalance() {
        double FinalBalance = 0.0;
        String NumberOnly = (Price.getText().toString()).replaceAll("[^0-9]", "");
        double Transaction = Double.parseDouble(NumberOnly);
        FinalBalance = Transaction / 100;
        Wallet.setText(String.format("%.2f", FinalBalance) + "UGX");
    }


    private void receiveUpdates()
    {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write('1');
                btSocket.getOutputStream().write('\n');
                inStream = btSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                int bytesAvailable = inStream.available();
                if (bytesAvailable > 0) {
                    byte[] packetBytes = new byte[bytesAvailable];
                    System.out.println(bytesAvailable);
                    inStream.read(packetBytes);
                    for (int i = 0; i < bytesAvailable; i++) {
                        byte b = packetBytes[i];
                        readBuffer[readBufferPosition++] = b;
                        //System.out.println(new String(readBuffer));
                    }
                    byte[] encodedBytes = new byte[readBufferPosition];
                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);


                    Scanner s = new Scanner(new String (encodedBytes)).useDelimiter("/");

                    String signal = s.next();
                    System.out.println(signal);
                    String nodeID = s.next();
                    System.out.println(nodeID);
                    String price = s.next();
                    System.out.println(price);
                    Household1Price.setText(price);
                    String quantity = s.next();
                    System.out.println(quantity);
                    Household1kWh.setText(quantity);
                    String count = s.next();
                    System.out.println(count);
                    String path = s.next();
                    System.out.println(path);
                    ArrayList listofOffers = new ArrayList();

                    s.close();


                    //System.out.println(encodedBytes[readBufferPosition - 5]);
                    //Household1Price.setText(String.valueOf(encodedBytes[readBufferPosition-5]));

                }
            } catch (IOException ex) {

            }
        }
    }
    /*private void receiveUpdates() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write('1');
                btSocket.getOutputStream().write('\n');
                inStream = btSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                int bytesAvailable = inStream.available();
                if (bytesAvailable > 0) {
                    byte[] packetBytes = new byte[bytesAvailable];
                    System.out.println(bytesAvailable);
                    inStream.read(packetBytes);
                    for (int i = 0; i < bytesAvailable; i++) {
                        byte b = packetBytes[i];
                        readBuffer[readBufferPosition++] = b;
                        //System.out.println(new String(readBuffer));
                    }
                    byte[] encodedBytes = new byte[readBufferPosition];
                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);


                    Scanner s = new Scanner(new String (encodedBytes)).useDelimiter("/");

                    String signal = s.next();
                    System.out.println(signal);
                    String nodeID = s.next();
                    System.out.println(nodeID);
                    String price = s.next();
                    System.out.println(price);
                    Household1Price.setText(price);
                    String quantity = s.next();
                    System.out.println(quantity);
                    Household1kWh.setText(quantity);
                    String count = s.next();
                    System.out.println(count);
                    String path = s.next();
                    System.out.println(path);
                    ArrayList listofOffers = new ArrayList();

                    s.close();


                    //System.out.println(encodedBytes[readBufferPosition - 5]);
                    //Household1Price.setText(String.valueOf(encodedBytes[readBufferPosition-5]));

                }
            } catch (IOException ex) {

            }
        }
    }*/

    private void updateOffers() {
        if (btSocket != null) {
            try {
                //btSocket.getOutputStream().write(0);
                Float getOfferPrice = Float.valueOf(Household4Price.getText().toString());
                ByteBuffer a = ByteBuffer.allocate(4);
                a.putFloat(getOfferPrice);
                btSocket.getOutputStream().write(a.array());
                Float getkWhAvailable = Float.valueOf(Household4kWh.getText().toString());
                ByteBuffer b = ByteBuffer.allocate(4);
                b.putFloat(getkWhAvailable);
                btSocket.getOutputStream().write(b.array());

            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trade_energy, menu);
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
            progress = ProgressDialog.show(tradeEnergy.this, "Connecting...", "Please wait!!!");  //show a progress dialog
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