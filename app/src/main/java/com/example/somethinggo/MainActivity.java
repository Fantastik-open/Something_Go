package com.example.somethinggo;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import android.os.Bundle;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.somethinggo.databinding.ActivityMainBinding;

import java.lang.reflect.Array;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor magnetometer;

    EditText AccelXText;
    EditText AccelYText;
    EditText AccelZText;
    EditText GyroXText;
    EditText GyroYText;
    EditText GyroZText;
    EditText HeadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AccelXText = findViewById(R.id.AccelX);
        AccelYText = findViewById(R.id.AccelY);
        AccelZText = findViewById(R.id.AccelZ);
        GyroXText = findViewById(R.id.GyroX);
        GyroYText = findViewById(R.id.GyroY);
        GyroZText = findViewById(R.id.GyroZ);
        HeadingText = findViewById(R.id.heading);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }


    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        float[] accelOutput;
        float[] magOutput;
        float[] gyroOutput;
        float azimuth;

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    accelOutput = event.values;
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    magOutput = event.values;
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    gyroOutput = event.values; // Use this for movement detection
                    break;
            }

            if (accelOutput != null && magOutput != null) {
                float[] R = new float[9];
                float[] I = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, accelOutput, magOutput);
                if (success) {
                    float[] orientation = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    azimuth = orientation[0]; // compass direction
                    // Use the azimuth for your compass functionality
                }
            }

            try {
                // Update the EditText fields with the new sensor data
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AccelXText.setText(String.valueOf(accelOutput[0]));
                        AccelYText.setText(String.valueOf(accelOutput[1]));
                        AccelZText.setText(String.valueOf(accelOutput[2]));
                        GyroXText.setText(String.valueOf(gyroOutput[0]));
                        GyroYText.setText(String.valueOf(gyroOutput[1]));
                        GyroZText.setText(String.valueOf(gyroOutput[2]));
                        HeadingText.setText(String.valueOf((Math.toDegrees(azimuth) + 360) % 360));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace(); // Log the exception for debugging
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Handle accuracy changes, if necessary
        }

    };

    @Override
    protected void onResume() {
        super.onResume();

        if (accelerometer != null) {
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }

        if (magnetometer != null) {
            sensorManager.registerListener(sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_UI);
        }

        if (gyroscope != null) {
            sensorManager.registerListener(sensorEventListener, gyroscope, SensorManager.SENSOR_DELAY_UI);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }
}

