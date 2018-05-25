package com.goudela.dimitra.sdy61_ge5_106304;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.goudela.dimitra.provider.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the Send button */
    public void enterTextEditor(View view) {
        // Do something in response to button

        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled()) {
            Toast.makeText(this,"Please turn on wifi to continue",Toast.LENGTH_SHORT).show();


        } else {
            Intent intent = new Intent(this, TextEditorActivity.class);
            startActivity(intent);
        }
    }






}
