package com.erg.morsytalky;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.erg.morsytalky.util.CameraEngine;
import com.erg.morsytalky.util.SuperUtils;
import com.erg.morsytalky.views.FocusBoxView;

public class MainActivity extends Activity implements SurfaceHolder.Callback,
        View.OnLongClickListener {

    private static final String TAG = "MainActivity";

    private FocusBoxView focusBoxView;
    private SurfaceView cameraFrame;
    private CameraEngine cameraEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "Surface created - Opening Camera");

        if (cameraEngine != null && !cameraEngine.isOn()) {
            cameraEngine.start();
        }
        if (cameraEngine != null && cameraEngine.isOn()) {
            Log.d(TAG, "Camera Engine isOn");
            return;
        }
        cameraEngine = CameraEngine.New(holder);
        cameraEngine.start();
        Log.d(TAG, "Camera Engine opened");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        cameraEngine.stop();
    }


    @Override
    protected void onResume() {
        super.onResume();
        cameraFrame = findViewById(R.id.camera_frame);
        focusBoxView = findViewById(R.id.focus_box);

        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.addCallback(this);
//        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        focusBoxView.setOnLongClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (cameraEngine != null && cameraEngine.isOn()) {
            cameraEngine.stop();
        }
        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.removeCallback(this);
    }

    @Override
    public boolean onLongClick(View v) {
        Log.d(TAG, "onLongClick");
        if (v.getId() == R.id.focus_box) {
            if (cameraEngine != null && cameraEngine.isOn()) {
                SuperUtils.vibrate(this);
                cameraEngine.requestFocus();
            }
        }
        return true;
    }
}