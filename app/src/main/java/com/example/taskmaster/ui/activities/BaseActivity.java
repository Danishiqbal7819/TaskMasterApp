package com.example.taskmaster.ui.activities;

import android.os.Bundle;
import android.os.Debug;
import android.provider.Settings;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.example.taskmaster.BuildConfig;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!BuildConfig.DEBUG && BuildConfig.FLAVOR.contains("uat")) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);}
            checkDebuggerStatus();
    }

    private void checkDebuggerStatus() {
        int adbEnabled = Settings.Global.getInt(
                getContentResolver(),
                Settings.Global.ADB_ENABLED,
                0
        );

        if (adbEnabled == 1) {
            showDebuggerDialog("USB Debugging is enabled. Please turn it off.");
        }
    }

    private void showDebuggerDialog(String msg) {
        AlertDialog dialog = new MaterialAlertDialogBuilder(
                this,
                com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog
        )
                .setTitle("Security Alert")
//                .setMessage("USB Debugging is enabled. Please turn it off.")
                .setMessage(msg)
                .setCancelable(false)
                .setNegativeButton("Ok", (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                })
                .create();

        dialog.show();
    }
}