package sg.edu.rp.c346.dontstop;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.today.step.lib.ISportStepInterface;
import com.today.step.lib.TodayStepManager;
import com.today.step.lib.TodayStepService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecordingActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvTime, tvDistance;
    Button btnContinue, btnPause, btnFinish;
    long milliSecondTime, startTime, TimeBuff, update = 0L;
    Handler handler;
    private int second, mintues, hours, milliseconds;

    SensorManager sensorManager;
    Sensor stepSensor;

    private static String TAG = "RecordingActivity";

    private static final int REFRESH_STEP_WHAT = 0;

    //循环取当前时刻的步数中间的间隔时间
    private long TIME_INTERVAL_REFRESH = 500;

    private Handler mDelayHandler = new Handler(new TodayStepCounterCall());
    private int currentStep, initStep;

    private ISportStepInterface iSportStepInterface;

    private JSONObject jsonObject;
    private JSONArray jsonArray;

    private boolean isPause = false;
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

        Intent i = getIntent();
        initStep = i.getIntExtra("initStep", 0);
        Log.d(TAG, "onCreate: initStep" + initStep);


        //初始化计步模块
        TodayStepManager.init(getApplication());

        //开启计步Service，同时绑定Activity进行aidl通信
        Intent intent = new Intent(getApplicationContext(), TodayStepService.class);
        startService(intent);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //Activity和Service通过aidl进行通信
                iSportStepInterface = ISportStepInterface.Stub.asInterface(service);
                try {
                    currentStep = iSportStepInterface.getCurrentTimeSportStep();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mDelayHandler.sendEmptyMessageDelayed(REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onClick(View view) {
        if (view == btnPause) {
            TimeBuff += milliSecondTime;
            handler.removeCallbacks(runnable);
            btnPause.setVisibility(view.INVISIBLE);
//            btnContinue.setVisibility(view.VISIBLE);
            btnFinish.setVisibility(view.VISIBLE);
            isPause = true;
        } else if (view == btnContinue) {
            startTime = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
            btnPause.setVisibility(view.VISIBLE);
            btnContinue.setVisibility(view.INVISIBLE);
            btnFinish.setVisibility(view.INVISIBLE);

        } else if (view == btnFinish) {

        }
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            milliSecondTime = SystemClock.uptimeMillis() - startTime;
            second = (int) (milliSecondTime / 1000);
            mintues = second / 60;
            hours = second / 360;
            second = second % 60;

            milliseconds = (int) update % 100;
            tvTime.setText(String.format("%02d", hours) + ":" + String.format("%02d", mintues) + ":" + String.format("%02d", second));
//            tvDistance.setText;

            handler.postDelayed(this, 0);
        }
    };

    private void updateStepCount() {

//        goalList.get(goalList.size()-1).setCurrentAmount(mStepSum);
//        gca.notifyDataSetChanged();
        double totalDistace = (currentStep - initStep) * 0.67;
        Log.e(TAG, "updateSDistance currentStep: " + currentStep);
        Log.e(TAG, "updateSDistance initStep: " + initStep);
        Log.e(TAG, "updateSDistance : " + totalDistace);
        tvDistance.setText( String.format("%.2f",totalDistace));
//
    }


    class TodayStepCounterCall implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_STEP_WHAT: {
                    //每隔500毫秒获取一次计步数据刷新UI
                    if (null != iSportStepInterface) {
                        int step = 0;
                        try {
                            step = iSportStepInterface.getCurrentTimeSportStep();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        if (currentStep != step) {
                            if (isPause == false){
                                currentStep = step;
                                updateStepCount();
                            }
                        }
                    }
                    mDelayHandler.sendEmptyMessageDelayed(REFRESH_STEP_WHAT, TIME_INTERVAL_REFRESH);

                    break;
                }
            }
            return false;
        }
    }
}
