package com.example.jsampaio.veloguiaversao2;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private Button mBtnComecar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mBtnComecar = (Button)findViewById(R.id.btnComecar);

        ativaGPS();
        mBtnComecar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v){

                chamarOutraActivity();
            }
        });
    }

    protected  void ativaGPS()
    {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean GPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!GPSEnabled){
            Toast.makeText(this, "É necessário ativar o GPS do celular",
                    Toast.LENGTH_SHORT).show();

            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

    }



    public void chamarOutraActivity() {

        Intent chamarTela = new Intent(this, LigarBluetooth.class);
        startActivity(chamarTela);
    }
}
