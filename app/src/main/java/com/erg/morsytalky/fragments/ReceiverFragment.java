package com.erg.morsytalky.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.erg.morsytalky.R;
import com.erg.morsytalky.controller.CameraEngine;
import com.erg.morsytalky.views.AutoFitTextureView;

public class ReceiverFragment extends Fragment {

    private static final String TAG = "ReceiverFragment";

    private View rootView;
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
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "View Created");
        AutoFitTextureView mTextureView = rootView.findViewById(R.id.texture);
        cameraEngine = new CameraEngine(requireActivity(), mTextureView);
        initCameraFlow();
    }

    private void initCameraFlow() {
        cameraEngine.onResume();
        transmitterFragment.bindCameraEngine(cameraEngine);
        Log.d(TAG, "initCameraFlow: Camera init on");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        initCameraFlow();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        cameraEngine.onPause();
    }

    /*    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraEngine != null && cameraEngine.isOn()) {
            cameraEngine.stop();
        }
        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.removeCallback(this);
    }*/
}