package com.erg.morsytalky.util;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by Elio on 18/04/2015.
 */
public class CameraEngine {

    static final String TAG = "CameraEngine_" + CameraUtils.class.getName();

    private boolean on;
    private Camera camera;
    private SurfaceHolder surfaceHolder;

    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            Log.d(TAG, "onAutoFocus SUCCESS: " + success );
        }
    };

    public boolean isOn() {
        return on;
    }

    private CameraEngine(SurfaceHolder surfaceHolder){
        this.surfaceHolder = surfaceHolder;
    }

    static public CameraEngine New(SurfaceHolder surfaceHolder){
        Log.d(TAG, "Creating camera engine");
        return  new CameraEngine(surfaceHolder);
    }

    public void requestFocus() {
        if (camera == null)
            return;

        if (isOn()) {
            camera.autoFocus(autoFocusCallback);
        }
    }

    public void start() {

        Log.d(TAG, " CameraEngine - start()");
        this.camera = CameraUtils.getCamera();

        if (this.camera == null)
            return;

        Log.d(TAG, "Get camera hardware");

        try {

            this.camera.setPreviewDisplay(this.surfaceHolder);
            this.camera.setDisplayOrientation(90);
            this.camera.startPreview();

            on = true;

            Log.d(TAG, "CameraEngine preview started");

        } catch (IOException e) {
            Log.e(TAG, "Error in setPreviewDisplay");
        }
    }

    public void stop(){

        if(camera != null){
            //this.autoFocusEngine.stop();
            camera.release();
            camera = null;
        }

        on = false;

        Log.d(TAG, "CameraEngine stopped");
    }

    public void takePhoto(Camera.ShutterCallback shutterCallback,
                          Camera.PictureCallback rawPictureCallback,
                          Camera.PictureCallback jpegPictureCallback){
        if(isOn()){
            camera.takePicture(shutterCallback, rawPictureCallback, jpegPictureCallback);
        }
    }


    public void turnOnFlash() {
        final Camera.Parameters p = camera.getParameters();
        if (camera == null || p == null) {
            return;
        }
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(p);
        camera.startPreview();
        Log.d(TAG, "Camera led ON");
    }

    public void turnOffFlash() {
        final Camera.Parameters p = camera.getParameters();
        if (camera == null || p == null) {
            return;
        }
        p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(p);
        camera.startPreview();
        Log.d(TAG, "Camera led OFF");
    }

}
