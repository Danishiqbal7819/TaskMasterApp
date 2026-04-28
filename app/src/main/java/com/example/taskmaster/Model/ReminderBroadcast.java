//package com.example.taskmaster.Model;
//
//import android.annotation.SuppressLint;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;
//
//import com.example.taskmaster.R;
//
//public class ReminderBroadcast extends BroadcastReceiver {
//    @SuppressLint("MissingPermission")
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,"notifylemubit")
//                .setSmallIcon(R.drawable.baseline_update_24)
//                .setContentTitle("Task_master Reminder")
//                .setContentText("Reminding your task")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        NotificationManagerCompat notificationManager= NotificationManagerCompat.from(context);
//        notificationManager.notify(200,builder.build());
//
//    }
//}
