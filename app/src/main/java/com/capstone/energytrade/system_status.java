package com.capstone.energytrade;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class system_status extends AppCompatActivity {

    public static boolean btbStatus= false;
    public static boolean btuStatus= false;
    public static boolean sellStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_status);
        sellStatus = true;
    }
}
