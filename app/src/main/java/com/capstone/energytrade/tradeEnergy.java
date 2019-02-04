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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class tradeEnergy extends AppCompatActivity {

    private ProgressDialog progress;

    private Button BatteryLevel;
    private Button BuyButton;
    private Button Update;
    private Button OfferButton;
    private Button Confirm;


    private TextView MyBalance;

    private TextView Storage;
    private TextView Wallet;
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
    //private AdapterView.OnItemClickListener myOfferListClickListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_energy);
        Intent newint = getIntent();
        address = newint.getStringExtra(btPairing.EXTRA_ADDRESS);
        new ConnectBT().execute();


        offerList = (ListView) findViewById(R.id.offerList);
        MyBalance = (TextView) findViewById(R.id.MyBalance);



        BatteryLevel = (Button) findViewById(R.id.BatteryLevel);
        Update = (Button) findViewById(R.id.update);
        BuyButton = (Button) findViewById(R.id.BuyButton);
        BuyButton.setText(R.string.BuyButton);
        OfferButton = (Button) findViewById(R.id.OfferButton);
        OfferButton.setText(R.string.OfferButton);

        Storage = (TextView) findViewById(R.id.Storage);
        Wallet = (TextView) findViewById(R.id.Wallet);

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
                confirmTransaction();


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


    //Multiple offer
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

            try
            {
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

                    String[] offerOption = encodedBytes.toString().split("\n");
                    int offerOptionindex = 1;
                    for (String offerDetails : offerOption)
                    {
                        Scanner s = new Scanner(new String(String.valueOf(offerOption[offerOptionindex]))).useDelimiter("/");

                        String signal = s.next();
                        System.out.println(signal);
                        String nodeID = s.next();
                        System.out.println(nodeID);
                        String price = s.next();
                        System.out.println(price);
                        //Household1Price.setText(price);
                        String quantity = s.next();
                        System.out.println(quantity);
                        //Household1kWh.setText(quantity);
                        String count = s.next();
                        System.out.println(count);
                        String path = s.next();
                        System.out.println(path);
                        s.close();
                        ArrayList offer_list = new ArrayList();
                        offer_list.add(nodeID + '\n' + "Price:" + price + "Quantity:" + quantity);
                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, offer_list);
                        offerList.setAdapter(arrayAdapter);
                        offerList.setOnItemClickListener(myOfferListClickListener);
                        offerOptionindex++;
                    }
                  /* for(int i=0, l=offerOption.length; i<l;i++) {

                        Scanner s = new Scanner(new String(offerOption[i])).useDelimiter("/");

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
                        String[] offer = new String[]{nodeID,"Price:", price,"Quantity", quantity};
                        final List<String> offer_list = new ArrayList<String>(Arrays.asList(offer));
                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, offer_list);
                        offerList.setAdapter(arrayAdapter);
                        offerList.setOnItemClickListener(myOfferListClickListener);
                        s.close();
                    }*/

                }
            } catch (IOException ex) {

            }
        }
    }

    //Single offer
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
                    String quantity = s.next();
                    System.out.println(quantity);
                    String count = s.next();
                    System.out.println(count);
                    String path = s.next();
                    System.out.println(path);
                    s.close();

                    ArrayList offer_list = new ArrayList();
                    offer_list.add("Node ID:" + nodeID + '\n' + "Price:" + price + ' ' + "Quantity:" + quantity);
                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, offer_list);
                    offerList.setAdapter(arrayAdapter);
                    offerList.setOnItemClickListener(myOfferListClickListener);
                    //System.out.println(encodedBytes[readBufferPosition - 5]);
                    //Household1Price.setText(String.valueOf(encodedBytes[readBufferPosition-5]));

                }
            } catch (IOException ex) {

            }

        }

    }*/

    private OnItemClickListener myOfferListClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String transactionInfo = ((TextView)view).getText().toString();
            //String price = transactionInfo.indexOf(price)
            //String price = transactionInfo.substring(19,22);
            //String kWhAvailable = transactionInfo.substring(33,35);
            


        }
    };

    private void updateOffers() {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(0);
                btSocket.getOutputStream().write('/');
                //Float getOfferPrice = Float.valueOf(Household4Price.getText().toString());
                //ByteBuffer a = ByteBuffer.allocate(1);
                //a.putFloat(getOfferPrice);
                //btSocket.getOutputStream().write(a.array());
                btSocket.getOutputStream().write('/');
                //Float getkWhAvailable = Float.valueOf(Household4kWh.getText().toString());
                //ByteBuffer b = ByteBuffer.allocate(1);
                //b.putFloat(getkWhAvailable);
                //btSocket.getOutputStream().write(b.array());
                btSocket.getOutputStream().write('/');

            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void confirmTransaction()
    {
        if (btSocket != null){
            try{
                btSocket.getOutputStream().write(2);
                btSocket.getOutputStream().write('/');
                //nodeID
                btSocket.getOutputStream().write('/');
                //price from selected offer
                btSocket.getOutputStream().write('/');
                Float getkWhNeeded = Float.valueOf(kWh.getText().toString());
                ByteBuffer c = ByteBuffer.allocate(1);
                c.putFloat(getkWhNeeded);
                btSocket.getOutputStream().write(c.array());
                btSocket.getOutputStream().write('/');
                //pathcount
                btSocket.getOutputStream().write('/');
                //path
                btSocket.getOutputStream().write('/');
                btSocket.getOutputStream().write('\n');
            }catch(IOException e) {
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