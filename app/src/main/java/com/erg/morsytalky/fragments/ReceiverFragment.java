package com.erg.morsytalky.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.erg.morsytalky.R;
import com.erg.morsytalky.controller.CameraEngine;
import com.erg.morsytalky.util.SuperUtils;

public class ReceiverFragment extends Fragment implements SurfaceHolder.Callback {

    private static final String TAG = "ReceiverFragment";

    private View rootView;

    private SurfaceView cameraFrame;
    private CameraEngine cameraEngine;
    private TransmitterFragment transmitterFragment;


    public ReceiverFragment() {
        // Required empty public constructor
    }

    public ReceiverFragment(TransmitterFragment transmitterFragment) {
        this.transmitterFragment = transmitterFragment;
    }


    public static ReceiverFragment newInstance(TransmitterFragment transmitterFragment) {
        return new ReceiverFragment(transmitterFragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        rootView = inflater.inflate(R.layout.fragment_receiver, container, false);
        initCameraFlow();
        return rootView;
    }

    private void initCameraFlow() {
        cameraFrame = rootView.findViewById(R.id.camera_frame);
        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.addCallback(this);
        cameraFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuperUtils.vibrateMin(requireActivity());
                cameraEngine.requestFocus();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
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
        transmitterFragment.bindCameraEngine(cameraEngine);
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
    public void onResume() {
        super.onResume();
        initCameraFlow();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraEngine != null && cameraEngine.isOn()) {
            cameraEngine.stop();
        }
        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.removeCallback(this);
    }
}