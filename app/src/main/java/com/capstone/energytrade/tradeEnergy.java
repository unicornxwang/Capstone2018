package com.capstone.energytrade;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
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
    private TextView price_tradeenergy;
    private EditText quantity_tradeenergy;
    private ListView offerlist_tradeenergy;

    private BluetoothSocket btSocket_tradeenergy = BluetoothApplication.getApplication().getCurrentBluetoothConnection();

    private InputStream inStream = null;

    int readBufferPosition = 0;
    byte[] readBuffer = new byte[1024];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_energy);

        return_tradeenergy = (ImageButton) findViewById(R.id.return_tradeenergy);
        btb_tradeenergy = (Button) findViewById(R.id.btb_tradeenergy);
        update_tradeenergy = (Button) findViewById(R.id.update_tradeenergy);

        quantity_tradeenergy = (EditText) findViewById(R.id.quantity_tradeenergy);
        price_tradeenergy = (TextView) findViewById(R.id.price_tradeenergy);
        offerlist_tradeenergy = (ListView) findViewById(R.id.offerlist_tradeenergy);
        return_tradeenergy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                system_status.btbStatus = false;
                Intent btbc = new Intent(tradeEnergy.this, buytobattery_complete.class);
                startActivity(btbc);

            }
        });

    }

    private void receiveUpdates() {
        if (btSocket_tradeenergy != null) {
            try {
                btSocket_tradeenergy.getOutputStream().write('1');
                btSocket_tradeenergy.getOutputStream().write('\n');

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    inStream = btSocket_tradeenergy.getInputStream();
                                    int bytesAvailable = inStream.available();
                                    if (bytesAvailable > 0) {
                                        byte[] packetBytes = new byte[bytesAvailable];
                                        System.out.println(bytesAvailable);
                                        inStream.read(packetBytes);

                                        for (int i = 0; i < bytesAvailable; i++) {
                                            byte b = packetBytes[i];
                                            if (b != 0) {
                                                readBuffer[readBufferPosition++] = b;
                                                System.out.println((char) b);
                                            }
                                            System.out.println(new String(readBuffer));
                                        }
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);

                                        String[] offerOption = new String(encodedBytes).split("\n");
                                        ArrayList<String> offer_list = new ArrayList<String>();
                                        TRANSACTION_INFO = new HashMap<Integer, Map<String, String>>();
                                        for (int i = 0; i < offerOption.length; i++) {
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

                                                Map<String, String> offerDetails = new HashMap<String, String>();
                                                offerDetails.put("Price", price);
                                                offerDetails.put("Quantity", quantity);
                                                offerDetails.put("Count", count);
                                                offerDetails.put("Path", path);

                                                int householdID = Integer.parseInt(nodeID);
                                                TRANSACTION_INFO.put(householdID, offerDetails);
                                                offer_list.add("Node ID:" + nodeID + '\n' + "Price:" + price + "$/Ah" + " Quantity:" + quantity + "Ah");

                                            }
                                        }
                                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(tradeEnergy.this, android.R.layout.simple_list_item_1, offer_list);
                                        offerlist_tradeenergy.setAdapter(arrayAdapter);
                                        offerlist_tradeenergy.setOnItemClickListener(myOfferListClickListener);

                                    }
                                } catch (IOException ex) {

                                }

                            }
                        });
                    }
                    }, 2000);

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    private OnItemClickListener myOfferListClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String transactionInfo = ((TextView)view).getText().toString();
            System.out.println(transactionInfo);
            String[]extractNodeID = transactionInfo.split("\n");
            int householdID = Integer.parseInt(extractNodeID[0].replaceAll("[^0-9]", ""));
            Map<String, String> offerDetails = TRANSACTION_INFO.get(householdID);
            price_tradeenergy.setText(offerDetails.get("Price"));
            quantity_tradeenergy.setText(offerDetails.get("Quantity"));
            nodeIDSelected = householdID;

        }
    };

    private void confirmTransaction()
    {
        if (btSocket_tradeenergy != null){
            try{
                btSocket_tradeenergy.getOutputStream().write('2');
                btSocket_tradeenergy.getOutputStream().write('/');


                if(nodeIDSelected<10){
                    btSocket_tradeenergy.getOutputStream().write('0');
                    btSocket_tradeenergy.getOutputStream().write(nodeIDSelected.toString().getBytes());
                }
                else {
                    btSocket_tradeenergy.getOutputStream().write(nodeIDSelected.toString().getBytes());
                }

                btSocket_tradeenergy.getOutputStream().write('/');

                btSocket_tradeenergy.getOutputStream().write(TRANSACTION_INFO.get(nodeIDSelected).get("Price").getBytes());
                btSocket_tradeenergy.getOutputStream().write('/');

                btSocket_tradeenergy.getOutputStream().write(quantity_tradeenergy.getText().toString().getBytes());
                btSocket_tradeenergy.getOutputStream().write('/');

                btSocket_tradeenergy.getOutputStream().write(TRANSACTION_INFO.get(nodeIDSelected).get("Count").getBytes());
                btSocket_tradeenergy.getOutputStream().write('/');

                btSocket_tradeenergy.getOutputStream().write(TRANSACTION_INFO.get(nodeIDSelected).get("Path").getBytes());

                btSocket_tradeenergy.getOutputStream().write('\n');

            }catch(IOException e) {
                msg("Error");
            }
        }
    }
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

}