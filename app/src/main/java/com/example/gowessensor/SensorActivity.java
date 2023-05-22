package com.example.gowessensor;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor heartRateSensor;
    private Sensor cadenceSensor;
    private Sensor speedSensor;
    private TextView heartRateTextView;
    private TextView cadenceTextView;
    private TextView speedTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_activity);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //  TextView untuk menampilkan data
        heartRateTextView = findViewById(R.id.heart_rate_textview);
        cadenceTextView = findViewById(R.id.cadence_textview);
        speedTextView = findViewById(R.id.speed_textview);

        // Izin akses ke sensor
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BODY_SENSORS}, 1);
        }

        // Mendapatkan referensi ke sensor heart rate
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        if (heartRateSensor != null) {
            // Daftarkan listener untuk sensor heart rate
            sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // Mendapatkan referensi ke sensor cadence
        cadenceSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (cadenceSensor != null) {
            // Daftarkan listener untuk sensor cadence
            sensorManager.registerListener(this, cadenceSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        // Dapatkan referensi ke sensor speed
        speedSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (speedSensor != null) {
            // Daftarkan listener untuk sensor speed
            sensorManager.registerListener(this, speedSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Periksa jenis sensor
        switch (event.sensor.getType()) {
            case Sensor.TYPE_HEART_RATE:
                // Perbarui data heart rate
                float heartRate = event.values[0];
                heartRateTextView.setText("Heart Rate: " + String.valueOf(heartRate));
                break;
            case Sensor.TYPE_STEP_COUNTER:
                // Perbarui data cadence
                float cadence = event.values[0];
                cadenceTextView.setText("Cadence: " + String.valueOf(cadence));
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                // Perbarui data speed
                float speed = event.values[0];
                speedTextView.setText("Speed: " + String.valueOf(speed));
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister listener ketika aktivitas dihancurkan
        sensorManager.unregisterListener(this);
    }

}
