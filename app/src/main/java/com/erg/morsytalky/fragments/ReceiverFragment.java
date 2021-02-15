package com.erg.morsytalky.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.erg.morsytalky.R;
import com.erg.morsytalky.controller.CameraEngine;

public class ReceiverFragment extends Fragment {

    private static final String TAG = "ReceiverFragment";

    private View rootView;
    private CameraEngine cameraEngine;
    private TransmitterFragment transmitterFragment;

    public ReceiverFragment() {
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
        Log.d(TAG, "View Created!!");
        TextureView mainTextureView = rootView.findViewById(R.id.main_texture_view);
        SurfaceView squareViewSurface = rootView.findViewById(R.id.square_view_surface);
        cameraEngine = new CameraEngine(requireActivity(), mainTextureView, squareViewSurface);
        transmitterFragment.bindCameraEngine(cameraEngine);
        cameraEngine.onResume();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        cameraEngine.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        cameraEngine.release();
    }
}