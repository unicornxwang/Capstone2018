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
import android.widget.ImageButton;
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
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
public class tradeEnergy extends AppCompatActivity {

    private Map<Integer, Map<String,String>> TRANSACTION_INFO;
    private Integer nodeIDSelected;




    private ImageButton return_tradeenergy;
    private Button update_tradeenergy;
    private Button btb_tradeenergy;

    private TextView text1_tradeenergy;
    private TextView text2_tradeenergy;
    private TextView text3_tradeenergy;

    private EditText quantity_tradeenergy;
    private EditText price_tradeenergy;

    private ListView offerlist_tradeenergy;


    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;

    String address_tradeenergy = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog progress;
    private InputStream inStream = null;

    int readBufferPosition = 0;
    byte[] readBuffer = new byte[1024];

    public static final String btbOfferPrice = "btbOfferPrice";
    public static final String btbOfferQuantity = "btbOfferQuantity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_energy);
        Intent newint = getIntent();
        address_tradeenergy = newint.getStringExtra(btPairing.EXTRA_ADDRESS);
        new ConnectBT().execute();

        return_tradeenergy = (ImageButton) findViewById(R.id.return_tradeenergy);
        btb_tradeenergy = (Button) findViewById(R.id.btb_tradeenergy);
        update_tradeenergy = (Button) findViewById(R.id.update_tradeenergy);

        quantity_tradeenergy = (EditText) findViewById(R.id.quantity_tradeenergy);
        price_tradeenergy = (EditText) findViewById(R.id.price_tradeenergy);

        offerlist_tradeenergy = (ListView) findViewById(R.id.offerlist_tradeenergy);




//        buytouse.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent btuInterface = new Intent(tradeEnergy.this, buytouse.class);
//
//                startActivity(btuInterface);
//            }
//        });
//        BatteryLevel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                requestStateofCharge();
//            }
//        });

        return_tradeenergy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returntosetup_tradeenergy = new Intent(tradeEnergy.this, setup_interface.class);
                startActivity(returntosetup_tradeenergy);
            }
        });

        update_tradeenergy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiveUpdates();
            }
        });


        btb_tradeenergy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmTransaction();
            }
        });


//        OfferButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updateOffers();
//
//            }
//        });

    }

//    private void requestStateofCharge() {
//        if(btSocket!=null){
//            try{
//                btSocket.getOutputStream().write('4');
//            }catch (IOException E){
//                msg("Error");
//            }
//        }
//    }

    //Multiple offer//
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
                        if( b != 0) {
                            readBuffer[readBufferPosition++] = b;
                            //char btoString = (char) b;
                            System.out.println((char) b);
                        }
                        //System.out.println(new String(readBuffer));
                    }
                    byte[] encodedBytes = new byte[readBufferPosition];
                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);

                    String[] offerOption = new String(encodedBytes).split("\n");
                    ArrayList<String> offer_list = new ArrayList<String>();
                    TRANSACTION_INFO = new HashMap<Integer, Map<String,String>>();
                    for(int i=0; i < offerOption.length;i++) {
                        String[] offerInformation = offerOption[i].split("/");

                       if (offerInformation.length >= 6) {
                           String signal = offerInformation[0];
                           System.out.println(signal);
                           String nodeID = offerInformation[1];
                           System.out.println(nodeID);
                           String price = offerInformation[2];
                           System.out.println(price);
                           String quantity = offerInformation[3];
                           System.out.println(quantity);
                           String count = offerInformation[4];
                           System.out.println(count);
                           String path = offerInformation[5];
                           System.out.println(path);

                           Map<String,String> offerDetails = new HashMap<String,String>();
                           offerDetails.put("Price",price);
                           offerDetails.put("Quantity", quantity);
                           offerDetails.put("Count", count);
                           offerDetails.put("Path", path);

                           int householdID = Integer.parseInt(nodeID);
                           TRANSACTION_INFO.put(householdID,offerDetails);
                           offer_list.add(nodeID + '\n' + "Price:" + price + " Quantity:" + quantity);

                       }


                    }
                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, offer_list);
                    offerlist_tradeenergy.setAdapter(arrayAdapter);
                    offerlist_tradeenergy.setOnItemClickListener(myOfferListClickListener);

                }
            } catch (IOException ex) {

            }
        }
    }


    private OnItemClickListener myOfferListClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String transactionInfo = ((TextView)view).getText().toString();
            System.out.println(transactionInfo);
            String[]extractNodeID = transactionInfo.split("\n");
            int householdID = Integer.parseInt(extractNodeID[0]);

            Map<String, String> offerDetails = TRANSACTION_INFO.get(householdID);
            System.out.println(offerDetails.get("Price"));
            price_tradeenergy.setText(offerDetails.get("Price"));
            quantity_tradeenergy.setText(offerDetails.get("Quantity"));
            nodeIDSelected = householdID;

        }
    };

//    private void updateOffers() {
//        if (btSocket != null) {
//            try {
//                btSocket.getOutputStream().write('0');
//                btSocket.getOutputStream().write('/');
//                Float getOfferPrice = Float.valueOf(Household4Price.getText().toString());
//                ByteBuffer a = ByteBuffer.allocate(1);
//                a.putFloat(getOfferPrice);
//                btSocket.getOutputStream().write(a.array());
//                btSocket.getOutputStream().write('/');
//                Float getkWhAvailable = Float.valueOf(Household4kWh.getText().toString());
//                ByteBuffer b = ByteBuffer.allocate(1);
//                b.putFloat(getkWhAvailable);
//                btSocket.getOutputStream().write(b.array());
//                btSocket.getOutputStream().write('/');
//
//            } catch (IOException e) {
//                msg("Error");
//            }
//        }
//    }

    private void confirmTransaction()
    {
        if (btSocket != null){
            try{
                btSocket.getOutputStream().write('2');
                btSocket.getOutputStream().write('/');


                if(nodeIDSelected<10){
                    btSocket.getOutputStream().write('0');
                    btSocket.getOutputStream().write(nodeIDSelected.toString().getBytes());
                }
                else {
                    btSocket.getOutputStream().write(nodeIDSelected.toString().getBytes());
                }


                btSocket.getOutputStream().write('/');

                btSocket.getOutputStream().write(TRANSACTION_INFO.get(nodeIDSelected).get("Price").getBytes());
                btSocket.getOutputStream().write('/');

                btSocket.getOutputStream().write(quantity_tradeenergy.getText().toString().getBytes());
//                btSocket.getOutputStream().write(TRANSACTION_INFO.get(nodeIDSelected).get("Quantity").getBytes());
                btSocket.getOutputStream().write('/');

                btSocket.getOutputStream().write(TRANSACTION_INFO.get(nodeIDSelected).get("Count").getBytes());
                btSocket.getOutputStream().write('/');

                btSocket.getOutputStream().write(TRANSACTION_INFO.get(nodeIDSelected).get("Path").getBytes());
//                btSocket.getOutputStream().write('/');

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
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address_tradeenergy);//connects to the device's address and checks if it's available
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