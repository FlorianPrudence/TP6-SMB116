package cnam.smb116.tp6;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.ProgressBar;

import java.util.concurrent.atomic.AtomicBoolean;

import cnam.smb116.tp6.Sensor.HTTPHumiditySensor;

public class SensorUpdater implements Runnable {
    private Thread worker;
    private Context context;
    private HTTPHumiditySensor httpHumiditySensor;
    public static final String HUMIDITY_SENSOR_RECEIVED = "humidity_sensor_received";
    public static final String SENSOR_DATA = "sensorData";
    private final AtomicBoolean running = new AtomicBoolean(false);
    public SensorUpdater(Context context) {
        this.context = context;
        this.httpHumiditySensor = new HTTPHumiditySensor();
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
