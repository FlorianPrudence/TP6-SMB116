package cnam.smb116.tp6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import cnam.smb116.tp6.Sensor.HTTPHumiditySensor;


public class MainActivity extends AppCompatActivity {
    private SensorUpdater updater;
    private ProgressBar bar;
    private TextView humidityText;
    private BroadcastReceiver receiver;
    private Button startBtn, stopBtn;
    private float sensorData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = (Button)findViewById(R.id.buttonStart);
        stopBtn = (Button)findViewById(R.id.buttonStop);
        bar = (ProgressBar)findViewById(R.id.sensorBar);
        humidityText = (TextView)findViewById(R.id.humidityText);
        bar.setProgress(0);
        bar.setMax(100);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sensorData = intent.getFloatExtra(SensorUpdater.SENSOR_DATA, 0);
                humidityText.setText("Hudimité: " + sensorData + " %");
                bar.setProgress(Math.round(sensorData));
            }
        };
        updater = new SensorUpdater(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SensorUpdater.HUMIDITY_SENSOR_RECEIVED);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    public void startUpdater(View view) {
        updater.start();
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
    }

    public void stopUpdater(View view) {
        updater.stop();
        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        humidityText.setText("Pour lancer l'acquisition, cliquez sur Start");
        bar.setProgress(0);
    }
}