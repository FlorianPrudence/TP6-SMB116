package cnam.smb116.tp6;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ProgressBar;


public class MainActivity extends AppCompatActivity {
    private static Thread thread;
    private static ProgressBar bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bar = (ProgressBar)findViewById(R.id.sensorBar);
        //thread = new Thread(this::UpdateSensor);
        //thread.start();
    }
}