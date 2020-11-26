package com.erg.morsytalky.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;

/**
 * Created by Elio on 18/04/2015.
 */
public class CameraUtils {

    static final String TAG = "CameraUtils_" + CameraUtils.class.getName();

    public static boolean deviceHasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static Camera getCamera() {
        try {
            return Camera.open();
        } catch (Exception e) {
            Log.e(TAG, "There is no camera to return");
            return null;
        }
    }
}
