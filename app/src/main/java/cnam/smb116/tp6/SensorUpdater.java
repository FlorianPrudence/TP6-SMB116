package cnam.smb116.tp6;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicBoolean;

import cnam.smb116.tp6.Sensor.HTTPHumiditySensor;

public class SensorUpdater implements Runnable {
    private Thread worker;
    private Context context;
    private HTTPHumiditySensor httpHumiditySensor;
    public static final String HUMIDITY_SENSOR_RECEIVED = "humidity_sensor_received";
    public static final String SENSOR_DATA = "sensorData";
    private final AtomicBoolean running = new AtomicBoolean(false);
    // Constructeur utilisé lorsqu'aucune URL n'a été récupérée dans les SharedPreferences
    public SensorUpdater(Context context) {
        this.context = context;
        this.httpHumiditySensor = new HTTPHumiditySensor();
    }

    // Constructeur utilisé lorsqu'une URL a été récupérée depuis l'IHM ou dans les SharedPreferences
    public SensorUpdater(Context context, String url) {
        this.context = context;
        this.httpHumiditySensor = new HTTPHumiditySensor(url);
    }

    public void start() {
        if(!running.get()) {
            worker = new Thread(this);
            worker.start();
        }
    }

    public void stop() {
        running.set(false);
    }

    // Le comportement du Thread : une boucle récupère les données du capteur puis attends avant de recommencer un cycle
    // Les données récupérées sont transmises par un intent envoyés via la méthode sendBroadcast
    public void run() {
        running.set(true);
        while(running.get()) {
            try {
                float sensorData = httpHumiditySensor.value();
                Intent intent = new Intent();
                intent.setAction(HUMIDITY_SENSOR_RECEIVED);
                intent.putExtra(SENSOR_DATA, sensorData);
                context.sendBroadcast(intent);
                Thread.sleep(httpHumiditySensor.minimalPeriod());
            } catch (Exception e) {
            }
        }
    }
}
