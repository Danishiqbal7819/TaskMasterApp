package com.example.taskmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
public class SplashActivity extends AppCompatActivity {

    private TextView text;
    private final Handler splashHandler = new Handler(Looper.getMainLooper());
    private final Runnable launchTaskApp = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(SplashActivity.this, TaskDashboardActivity.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setStatusBarColor(ContextCompat.getColor(SplashActivity.this,R.color.colorPrimarydark));

        text=findViewById(R.id.splashLoading);
        Animation animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.blinkanimation);
        text.startAnimation(animation);

        splashHandler.postDelayed(launchTaskApp, 1800);
    }

    @Override
    protected void onDestroy() {
        splashHandler.removeCallbacks(launchTaskApp);
        super.onDestroy();
    }
}
