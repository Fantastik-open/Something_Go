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

    EditText AccelXText = findViewById(R.id.AccelX);
    EditText AccelYText = findViewById(R.id.AccelY);
    EditText AccelZText = findViewById(R.id.AccelZ);
    EditText GyroXText = findViewById(R.id.GyroX);
    EditText GyroYText = findViewById(R.id.GyroY);
    EditText GyroZText = findViewById(R.id.GyroZ);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

            AccelXText.setText(String.valueOf(accelOutput[0]));
            AccelYText.setText(String.valueOf(accelOutput[1]));
            AccelZText.setText(String.valueOf(accelOutput[2]));
            GyroXText.setText(String.valueOf(gyroOutput[0]));
            GyroYText.setText(String.valueOf(gyroOutput[1]));
            GyroZText.setText(String.valueOf(gyroOutput[2]));

            if (accelOutput != null && magOutput != null) {
                float[] R = new float[9];
                float[] I = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, accelOutput, magOutput);
                if (success) {
                    float[] orientation = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    float azimuth = orientation[0]; // compass direction
                    // Use the azimuth for your compass functionality
                }
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
            sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (magnetometer != null) {
            sensorManager.registerListener(sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (gyroscope != null) {
            sensorManager.registerListener(sensorEventListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }
}

