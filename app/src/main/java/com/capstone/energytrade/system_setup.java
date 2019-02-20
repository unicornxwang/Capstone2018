package com.capstone.energytrade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;

public class system_setup extends AppCompatActivity {

    private Button update_system;
    private ImageButton return_system;
    private EditText port1_system;
    private EditText port2_system;
    private EditText port3_system;
    private TextView text1_system;
    private TextView text2_system;
    private TextView text3_system;
    private TextView text4_system;
    private TextView text5_system;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_setup);

        update_system = (Button) findViewById(R.id.update_system);
        return_system = (ImageButton) findViewById(R.id.return_system);
        port1_system = (EditText)findViewById(R.id.port1_system);
        port2_system = (EditText)findViewById(R.id.port2_system);
        port3_system = (EditText)findViewById(R.id.port3_system);

        return_system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returntomain_system = new Intent(system_setup.this, setup_interface.class);
                startActivity(returntomain_system);
            }
        });



    }

}
