package com.example.vault.panicswitch;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import java.util.List;

public class AccelerometerManager {
    private static Context aContext = null;

    public static int interval = 200;

    public static AccelerometerListener listener = null;
    private static boolean running = false;
    private static Sensor sensor = null;
    private static SensorEventListener sensorEventListener = new SensorEventListener() {
        private float force = 0.0f;
        private long lastShake = 0;
        private long lastUpdate = 0;
        private float lastX = 0.0f;
        private float lastY = 0.0f;
        private float lastZ = 0.0f;
        private long now = 0;
        private long timeDiff = 0;
        private float x = 0.0f;
        private float y = 0.0f;
        private float z = 0.0f;

        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        public void onSensorChanged(SensorEvent sensorEvent) {
            this.now = sensorEvent.timestamp;
            this.x = sensorEvent.values[0];
            this.y = sensorEvent.values[1];
            this.z = sensorEvent.values[2];
            long j = this.lastUpdate;
            if (j == 0) {
                long j2 = this.now;
                this.lastUpdate = j2;
                this.lastShake = j2;
                this.lastX = this.x;
                this.lastY = this.y;
                this.lastZ = this.z;
            } else {
                this.timeDiff = this.now - j;
                if (this.timeDiff > 0) {
                    this.force = Math.abs(((((this.x + this.y) + this.z) - this.lastX) - this.lastY) - this.lastZ);
                    if (Float.compare(this.force, AccelerometerManager.threshold) > 0) {
                        if (this.now - this.lastShake >= ((long) AccelerometerManager.interval)) {
                            AccelerometerManager.listener.onShake(this.force);
                        }
                        this.lastShake = this.now;
                    }
                    this.lastX = this.x;
                    this.lastY = this.y;
                    this.lastZ = this.z;
                    this.lastUpdate = this.now;
                }
            }
            AccelerometerManager.listener.onAccelerationChanged(this.x, this.y, this.z);
        }
    };
    private static SensorManager sensorManager = null;
    private static Boolean supported = null;

    public static float threshold = 15.0f;

    public static boolean isListening() {
        return running;
    }

    public static void stopListening() {
        running = false;
        try {
            if (sensorManager != null && sensorEventListener != null) {
                sensorManager.unregisterListener(sensorEventListener);
            }
        } catch (Exception unused) {
        }
    }

    public static boolean isSupported(Context context) {
        aContext = context;
        if (supported == null) {
            Context context2 = aContext;
            if (context2 != null) {
                sensorManager = (SensorManager) context2.getSystemService(Context.SENSOR_SERVICE);
                boolean z = true;
                if (sensorManager.getSensorList(1).size() <= 0) {
                    z = false;
                }
                supported = new Boolean(z);
            } else {
                supported = Boolean.FALSE;
            }
        }
        return supported.booleanValue();
    }

    public static void configure(int i, int i2) {
        threshold = (float) i;
        interval = i2;
    }

    public static void startListening(AccelerometerListener accelerometerListener) {
        sensorManager = (SensorManager) aContext.getSystemService(Context.SENSOR_SERVICE);
        List sensorList = sensorManager.getSensorList(1);
        if (sensorList.size() > 0) {
            sensor = (Sensor) sensorList.get(0);
            running = sensorManager.registerListener(sensorEventListener, sensor, 1);
            listener = accelerometerListener;
        }
    }

    public static void startListening(AccelerometerListener accelerometerListener, int i, int i2) {
        configure(i, i2);
        startListening(accelerometerListener);
    }
}
