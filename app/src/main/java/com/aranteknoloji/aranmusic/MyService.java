package com.aranteknoloji.aranmusic;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;

public class MyService extends Service {

    private static final String TAG = "MyService";
    private static final int NOTI_ID = 32;
    public static boolean isServiceRunning = false;

    private MediaPlayer player;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate:");
//        startServiceWithNotification();
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind:");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand:");
        if (intent != null) {
            if (intent.getAction().equals(PlayerTasksHelper.PlayerUtils.ACTION_PLAYER_STOP)) {
                stopMyService();
            } else if (intent.getAction().equals(PlayerTasksHelper.PlayerUtils.ACTION_PLAYER_START)) {
                startServiceWithNotification();
//                player = MediaPlayer.create(this, Uri.parse(intent.getStringExtra("path")));
                player = MediaPlayer.create(this, Uri.parse(PlayerTasksHelper.getSongPath()));
                player.start();
            } else if (intent.getAction().equals(PlayerTasksHelper.PlayerUtils.ACTION_PLAYER_FORWARD)) {
                PlayerTasksHelper.currentPosition++;
                startServiceWithNotification();
                player = MediaPlayer.create(this, Uri.parse(PlayerTasksHelper.getSongPath()));
                player.start();
            }
        }
        return START_STICKY;
    }

    private void stopMyService() {
        Log.d(TAG, "stopMyService:");
        stopForeground(true);
        stopSelf();
        isServiceRunning = false;
    }

    private void startServiceWithNotification() {
        Log.d(TAG, "startServiceWithNotification:");
        if (player != null) player.reset();
        if (isServiceRunning) return;
        isServiceRunning = true;

        Intent notiIntent = new Intent(getApplicationContext(), MainActivity.class);
        notiIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, notiIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);

        Intent stopIntent = new Intent(getApplicationContext(), MyService.class);
        stopIntent.setAction(PlayerTasksHelper.PlayerUtils.ACTION_PLAYER_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, 0);

        Intent nextIntent = new Intent(getApplicationContext(), MyService.class);
        nextIntent.setAction(PlayerTasksHelper.PlayerUtils.ACTION_PLAYER_FORWARD);
        PendingIntent nextPendingIntent = PendingIntent.getService(this, 0, nextIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, "My Channel")
                .setSmallIcon(R.drawable.ic_android)
                .setContentText("Song name")
                .setContentTitle("Song Title")
                .setLargeIcon(icon)
                .setPriority(Notification.PRIORITY_HIGH)
//                .setStyle(new Notification.MediaStyle())
                .addAction(new NotificationCompat.Action(R.drawable.ic_stop_black_24dp, "Stop", stopPendingIntent))
                .addAction(new NotificationCompat.Action(R.drawable.ic_next, "Next", nextPendingIntent))
                .setContentIntent(contentPendingIntent)
                .build();

        notification.flags = notification.flags | Notification.FLAG_NO_CLEAR;     // NO_CLEAR makes the notification stay when the user performs a "delete all" command
        startForeground(NOTI_ID, notification);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy:");
        isServiceRunning = false;
        player.stop();
        player.release();
        super.onDestroy();
    }
}
