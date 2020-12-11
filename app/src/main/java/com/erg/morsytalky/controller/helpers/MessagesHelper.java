package com.erg.morsytalky.controller.helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.erg.morsytalky.R;
import com.google.android.material.snackbar.Snackbar;

public class MessagesHelper {

    public static void showInfoMessage(Activity context, String msg) {
        if (!context.isFinishing()) {
            Snackbar snackBar = Snackbar.make(context.findViewById(R.id.main_root)
                    , msg, Snackbar.LENGTH_SHORT);
            snackBar.setBackgroundTint(context.getColor(R.color.colorPrimary));
            if (!snackBar.isShown())
                snackBar.show();
        }
    }

    public static void showInfoMessageFragment(View rootView, Activity context, String msg) {
        if (!context.isFinishing()) {
            Snackbar snackBar = Snackbar.make(rootView.findViewById(R.id.main_root)
                    , msg, Snackbar.LENGTH_SHORT);
            snackBar.setBackgroundTint(context.getColor(R.color.colorPrimary));
            if (!snackBar.isShown())
                snackBar.show();
        }
    }

    public static void showInfoMessageError(Activity context, String msg) {
        if (!context.isFinishing()) {

            Snackbar snackBar = Snackbar.make(context.findViewById(R.id.main_root)
                    , msg, Snackbar.LENGTH_SHORT);
            snackBar.setBackgroundTint(context.getColor(R.color.red_btn_bg_color));
            if (!snackBar.isShown())
                snackBar.show();
        }
    }

    public static void showInfoMessageWarning(Activity context, String msg) {
        if (!context.isFinishing()) {
            Snackbar snackBar = Snackbar.make(context.findViewById(R.id.main_root)
                    , msg, Snackbar.LENGTH_SHORT);
            snackBar.setBackgroundTint(context.getColor(R.color.yellow_color));
            snackBar.setTextColor(context.getColor(R.color.black_default_color));
            if (!snackBar.isShown())
                snackBar.show();
        }
    }
}
