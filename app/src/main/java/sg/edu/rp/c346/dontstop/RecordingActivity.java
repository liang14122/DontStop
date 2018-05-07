package sg.edu.rp.c346.dontstop;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RecordingActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener{

    TextView tvTime, tvDistance;
    Button btnContinue, btnPause, btnFinish;
    long milliSecondTime, startTime, TimeBuff, update=0L;
    Handler handler;
    private int second, mintues, hours, milliseconds;
    int initialValue;
    int currentStep = 0;

    SensorManager sensorManager;
    Sensor stepSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        tvTime = findViewById(R.id.tvTime);
        tvDistance = findViewById(R.id.tvDistance);
        btnPause = findViewById(R.id.btnPause);
        btnContinue = findViewById(R.id.btnContinue);
        btnFinish = findViewById(R.id.btnFinish);
        handler = new Handler();

        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);

        btnPause.setOnClickListener(this);
        btnFinish.setOnClickListener(this);
        btnContinue.setOnClickListener(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }

    @Override
    public void onClick(View view) {
        if (view == btnPause)
        {
            TimeBuff += milliSecondTime;
            handler.removeCallbacks(runnable);
            btnPause.setVisibility(view.INVISIBLE);
            btnContinue.setVisibility(view.VISIBLE);
            btnFinish.setVisibility(view.VISIBLE);
        }
        else if (view == btnContinue)
        {
            startTime = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
            btnPause.setVisibility(view.VISIBLE);
            btnContinue.setVisibility(view.INVISIBLE);
            btnFinish.setVisibility(view.INVISIBLE);

        }
        else if (view == btnFinish)
        {

        }
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            milliSecondTime = SystemClock.uptimeMillis() - startTime;
            second = (int)(milliSecondTime/1000);
            mintues = second/60;
            hours = second/360;
            second = second % 60;

            milliseconds = (int)update%100;
            tvTime.setText(String.format("%02d", hours) + ":" + String.format("%02d", mintues) + ":" + String.format("%02d", second));
//            tvDistance.setText;

            handler.postDelayed(this,0);
        }
    };
    private long steps = 0;
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        initialValue = (int)sensorEvent.values[0];
        Sensor sensor = sensorEvent.sensor;
        float[] values = sensorEvent.values;
        int value = -1;
        if (values.length > 0){
            value = (int) values[0];
        }

        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
            steps ++;
        }
    }

    public float getDistance(long steps){
        float distance = (float)(steps*78)/(float) 100000;
        return  distance;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sensorManager.unregisterListener(this, stepSensor);
    }
}
