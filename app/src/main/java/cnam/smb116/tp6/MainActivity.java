package cnam.smb116.tp6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import cnam.smb116.tp6.Sensor.HTTPHumiditySensor;


public class MainActivity extends AppCompatActivity {
    private SensorUpdater updater;
    private ProgressBar bar;
    private TextView humidityText;
    private BroadcastReceiver receiver;
    private Button startBtn, stopBtn;
    private EditText editText;
    private LinearLayout linearLayout;
    private float sensorData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = (Button)findViewById(R.id.buttonStart);
        stopBtn = (Button)findViewById(R.id.buttonStop);
        bar = (ProgressBar)findViewById(R.id.sensorBar);
        humidityText = (TextView)findViewById(R.id.humidityText);
        editText = (EditText)findViewById(R.id.urlEditText);
        linearLayout = (LinearLayout)findViewById(R.id.editUrlLayout);

        bar.setProgress(0);
        bar.setMax(100);

        // Création du receiver qui mettra à jour l'IHM avec les informations reçues du capteur
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sensorData = intent.getFloatExtra(SensorUpdater.SENSOR_DATA, 0);
                humidityText.setText("Hudimité: " + sensorData + " %");
                bar.setProgress(Math.round(sensorData));
            }
        };

        // Récupération de l'URL personnalisé enregistré dans les SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String sensorURL = prefs.getString("sensorURL", null);
        if(sensorURL == null || sensorURL.length() == 0)
            updater = new SensorUpdater(this);
        else
            updater = new SensorUpdater(this, sensorURL);
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

    // Démarre le processus d'acquisition des données depuis le sensor
    public void startUpdater(View view) {
        updater.start();
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
    }

    // Stop le processus d'acquisition des données depuis le sensor
    public void stopUpdater(View view) {
        updater.stop();
        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        humidityText.setText("Pour lancer l'acquisition, cliquez sur Start");
        bar.setProgress(0);
    }

    // Affiche ou masque les composants de l'IHM permettant de modifier l'URL du sensor
    public void toggleEditUrl(View view) {
        updater.stop();
        if(linearLayout.getVisibility() == View.VISIBLE) {
            linearLayout.setVisibility(View.INVISIBLE);
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
            return;
        }
        if(linearLayout.getVisibility() == View.INVISIBLE) {
            startBtn.setEnabled(false);
            stopBtn.setEnabled(false);
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    // Récupère l'URL saisi dans l'IHM et l'enregistre dans les SharedPreferences
    public void doEditUrl(View view) {
        String url = editText.getText().toString();
        if(url.length() == 0)
            updater = new SensorUpdater(this);
        else {
            updater = new SensorUpdater(this, editText.getText().toString());
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor sEdit = prefs.edit();
            sEdit.putString("sensorURL", url);
            sEdit.apply();
        }
        toggleEditUrl(view);
    }
}