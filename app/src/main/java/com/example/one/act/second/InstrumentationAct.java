package com.example.one.act.second;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Instrumentation;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.widget.Button;

import com.blankj.utilcode.util.LogUtils;
import com.example.one.R;

public class InstrumentationAct extends AppCompatActivity {

    Button btSimulation,btSimulationOk;
    int[] location = new int[2];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrumentation);

        btSimulation = (Button) findViewById(R.id.btSimulation);
        btSimulationOk =  (Button) findViewById(R.id.btSimulationOK);

        btSimulation.setOnClickListener(v -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(5000);
                    tap(location[0],location[1]);
                }
            }).start();
        });

        btSimulationOk.setOnClickListener(v -> {
            LogUtils.d("被点击了");
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        btSimulationOk.getLocationOnScreen(location);
        LogUtils.d("x坐标:"+location[0]+"\ny坐标："+location[1]);
    }

    public void tap(int x, int y){
        try{
            Instrumentation inst = new Instrumentation();
            inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0));    //x,y 即是事件的坐标
            inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                    SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0));
        }catch (Exception e){
            LogUtils.d("点击出错");
            LogUtils.d(e);
        }
    }
}