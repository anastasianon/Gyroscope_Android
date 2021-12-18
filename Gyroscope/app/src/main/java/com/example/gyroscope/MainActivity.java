package com.example.gyroscope;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private Sensor sensor;
    private SensorManager sensorManager;
    private SensorEventListener sensorListener;

    private TextView xOutput;
    private TextView yOutput;
    private TextView zOutput;

    private TextView zImageOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        }
        else {
            System.out.println("Sensor is not found");
            finish();
            return;
        }

        xOutput = findViewById(R.id.xCoordOutput);
        yOutput = findViewById(R.id.yCoordOutput);
        zOutput = findViewById(R.id.zCoordOutput);

        zImageOutput = findViewById(R.id.image);

        sensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float[] rotationMatrix = new float[16];

                SensorManager.getRotationMatrixFromVector(
                        rotationMatrix,
                        sensorEvent.values
                );

                float[] remappedMatrix = new float[16];

                SensorManager.remapCoordinateSystem(
                        rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Z,
                        remappedMatrix
                );

                float[] orientations = new float[3];
                String[] formatted = new String[3];

                SensorManager.getOrientation(remappedMatrix, orientations);

                for (int i = 0; i < 3; i++) {
                    orientations[i] = (float)(Math.toDegrees(orientations[i]));
                    formatted[i] = String.valueOf((int)orientations[i]);
                }

                zOutput.setText(getResources().getString(R.string.zOutputText, formatted[0]));
                xOutput.setText(getResources().getString(R.string.xOutputText, formatted[1]));
                yOutput.setText(getResources().getString(R.string.yOutputText, formatted[2]));

                zImageOutput.setRotation(orientations[0]);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorListener);
    }
}