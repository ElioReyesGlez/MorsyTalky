package com.erg.morsytalky.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.erg.morsytalky.R;
import com.erg.morsytalky.controller.helpers.MessagesHelper;
import com.erg.morsytalky.util.SuperUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale;

/**
 * Created by Elio on 18/04/2015.
 */
public class CameraEngine implements ActivityCompat.OnRequestPermissionsResultCallback,
        View.OnClickListener {

    private static final String TAG = "CameraEngine2";
    private final Activity context;
    private int testingCont = 0;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 0);
        ORIENTATIONS.append(Surface.ROTATION_90, 90);
        ORIENTATIONS.append(Surface.ROTATION_180, 180);
        ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAITING_LOCK = 1;
    private static final int STATE_WAITING_PRE_CAPTURE = 2;
    private static final int STATE_WAITING_NON_PRE_CAPTURE = 3;
    private static final int STATE_PICTURE_TAKEN = 4;
    private static final int MAX_PREVIEW_WIDTH = 1920;
    private static final int MAX_PREVIEW_HEIGHT = 1080;


    public static final String CAMERA_FRONT = "1";
    public static final String CAMERA_BACK = "0";

    private boolean isTorchOn;
    public boolean isCameraConnected;

    private final TextureView.SurfaceTextureListener surfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            setUpCamera(width, height);
            connectCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
            testingCont++;
//            Log.d(TAG, "onSurfaceTextureUpdated: " + testingCont);
        }

    };

    private TextureView mainTextureView;
    private SurfaceView squareViewSurface;
    private String cameraId;
    private Size previewSize;
    private CaptureRequest.Builder captureRequestBuilder;
    private CameraDevice cameraDevice;
    private CameraDevice.StateCallback cameraDeviceStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            startPreview();
            Log.d(TAG, "onOpened: CAMERA CONNECTED!!");
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            camera.close();
            cameraDevice = null;
        }
    };
    private final CameraManager cameraManager;
    private final boolean isFlashSupported = true;
    private HandlerThread backgroundHandlerThread;
    private Handler backgroundHandler;

    public CameraEngine(Activity context, TextureView mainTextureView,
                        SurfaceView squareViewSurface) {
        this.context = context;
        this.mainTextureView = mainTextureView;
        this.squareViewSurface = squareViewSurface;
        this.cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        setUpSurfaceView(squareViewSurface);
    }

    private void setUpSurfaceView(SurfaceView squareViewSurface) {
        squareViewSurface.setZOrderOnTop(true);
        SurfaceHolder mHolder = squareViewSurface.getHolder();
        mHolder.setFormat(PixelFormat.TRANSPARENT);
        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Canvas canvas = holder.lockCanvas();
                if (canvas == null) {
                    Log.e(TAG, "Cannot draw onto the canvas as it's null");
                } else {
                    Paint paint = new Paint();
                    int color = ContextCompat.getColor(context, R.color.custom_white_color);
                    paint.setColor(color);
                    paint.setStrokeWidth(4);
                    paint.setStyle(Paint.Style.STROKE);
                    RectF rectF = new RectF(100, 100, 200, 200);
                    canvas.drawRoundRect(rectF,7,7, paint);


                    holder.unlockCanvasAndPost(canvas);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    private void setUpCamera(int width, int height) {
        try {
            for (String strId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics =
                        cameraManager.getCameraCharacteristics(strId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                        CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
                StreamConfigurationMap configMap = cameraCharacteristics
                        .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                int deviceOrientation = context.getWindowManager()
                        .getDefaultDisplay().getRotation();
                int totalRotation = sensorToDeviceRotation(cameraCharacteristics, deviceOrientation);
                boolean swapRotationFlag = totalRotation == 90 || totalRotation == 270;
                int rotatedWith = width;
                int rotatedHeight = height;
                if (swapRotationFlag) {
                    rotatedWith = width;
                    rotatedHeight = height;
                }
                previewSize = chooseOptimalSize(configMap.getOutputSizes(SurfaceTexture.class),
                        rotatedWith, rotatedHeight);
                cameraId = strId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void connectCamera() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    cameraManager.openCamera(cameraId, cameraDeviceStateCallback, backgroundHandler);
                    isCameraConnected = true;
                } else {
                    if (shouldShowRequestPermissionRationale(context, Manifest.permission.CAMERA)) {
                        MessagesHelper.showInfoMessageWarning(context,
                                context.getString(R.string.permission_needed));
                        requestPermissions(context, new String[]{Manifest.permission.CAMERA},
                                REQUEST_CAMERA_PERMISSION);
                    }
                }
            } else {
                cameraManager.openCamera(cameraId, cameraDeviceStateCallback, backgroundHandler);
                isCameraConnected = true;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
            isCameraConnected = false;
        }
    }

    private void startPreview() {
        SurfaceTexture surfaceTexture = mainTextureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        Surface previewSurface = new Surface(surfaceTexture);
        try {
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(previewSurface);
            cameraDevice.createCaptureSession(Collections.singletonList(previewSurface),
                    new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            try {
                                session.setRepeatingRequest(captureRequestBuilder.build(),
                                        null, backgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            MessagesHelper.showInfoMessageError(context,
                                    context.getString(R.string.camera_error));
                        }
                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startBackgroundThread() {
        backgroundHandlerThread = new HandlerThread("MorsyTalkyThread");
        backgroundHandlerThread.start();
        backgroundHandler = new Handler(backgroundHandlerThread.getLooper());
    }

    private void stopBackgroundThread() {
        if (backgroundHandlerThread != null) {
            backgroundHandlerThread.quitSafely();
            try {
                backgroundHandlerThread.join();
                backgroundHandlerThread = null;
                backgroundHandler = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static int sensorToDeviceRotation(CameraCharacteristics cameraCharacteristics,
                                              int deviceOrientation) {
        int sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        deviceOrientation = ORIENTATIONS.get(deviceOrientation);
        return (sensorOrientation + deviceOrientation + 360) % 360;
    }

    private static Size chooseOptimalSize(Size[] options, int width, int height) {
        List<Size> bigEnough = new ArrayList<Size>();
        for (Size choice : options) {
            if (choice.getHeight() == choice.getWidth() * height / width
                    && choice.getWidth() >= width && choice.getHeight() >= height) {
                bigEnough.add(choice);
            }
        }

        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new SuperUtils.CompareSizeByArea());
        } else {
            return options[0];
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                MessagesHelper.showInfoMessageWarning(context,
                        context.getString(R.string.permission_needed));
            }
        } else {
            onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        if (isFlashSupported) {
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        }
    }

    public void switchFlashOn(ImageView flashButton) {
        try {
            if (cameraId.equals(CAMERA_BACK)) {
                if (isFlashSupported) {
                    if (!isTorchOn) {
                        cameraManager.setTorchMode(cameraId, true);
                        flashButton.setImageResource(R.drawable.ic_flash_on);
                        isTorchOn = true;
                    }
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.e(TAG, "switchFlash: " + e.getMessage());
        }
    }

    public void switchFlashOff(ImageView flashButton) {
        try {
            if (cameraId.equals(CAMERA_BACK)) {
                if (isFlashSupported) {
                    if (isTorchOn) {
                        cameraManager.setTorchMode(cameraId, false);
                        flashButton.setImageResource(R.drawable.ic_flash_off);
                        isTorchOn = false;
                    }
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.e(TAG, "switchFlash: " + e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.main_texture_view) {
            //ToDo
        }
    }

    public void onResume() {
        Log.d(TAG, "Camera Engine Resumed");
        startBackgroundThread();
        if (mainTextureView.isAvailable()) {
            setUpCamera(mainTextureView.getWidth(), mainTextureView.getHeight());
            connectCamera();
        } else {
            mainTextureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    public void release() {
        Log.d(TAG, "Camera Engine Paused");
        stopBackgroundThread();
        closeCamera();
    }
}
