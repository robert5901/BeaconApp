package com.example.beaconapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
import org.jetbrains.annotations.Nullable;

/*
    Этот класс представляет собой службу, которая будет работать в фоновом режиме
    и уведомит пользователя через уведомление о том, входит ли он в область маяка или вышел из нее.
 */

public class BeaconService extends Service implements BeaconConsumer,MonitorNotifier {

    @Override
    public void onCreate() {
        super.onCreate();
        // Binding the BeaconNotification Application Class BeaconManager to BeaconService.
        BeaconNotification.beaconManager.bind(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Печать для проверки того, вызывается ли служба. в logcat
        System.out.println("SERVICE CALLED ------------------------------------------------->");

        return Service.START_STICKY;
    }


    @Override
    public void onBeaconServiceConnect() {
        //Указывает класс, который должен вызываться каждый раз,
        // когда BeaconService видит или перестает видеть Region маяков.
        BeaconNotification.beaconManager.addMonitorNotifier(this);
    }

    /*
      Этот метод переопределения запускается, когда какой-то маяк попадет в зону действия устройства.
    */
    @Override
    public void didEnterRegion(Region region) {

        // Отображение уведомления о том, что Маяк найден
        showNotification("Found Beacon in the range","For more info go the app");
    }


    /*
        Этот метод переопределения запускается, когда маяк, который входит в диапазон действия устройства,
        теперь был выведен из диапазона действия устройства.
     */
    @Override
    public void didExitRegion(Region region) {

        // Отображение уведомления о выходе маяка из региона
        showNotification("Founded Beacon Exited","For more info go the app");
    }


    /*
      Этот метод переопределения определит состояние устройства, находится ли устройство в зоне действия
      маяка или нет , если да, то i = 1, а если нет, то i = 0
    */
    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }

    //  Метод для показа уведомлений
    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    // Метод чтобы начать Broadcasting чтобы перезапустить службу
    // (BeaconBroadcast class)
    public void startBroadcasting(){
        Intent broadcastIntent = new Intent("com.example.beaconapp.RestartBeaconService");
        sendBroadcast(broadcastIntent);
    }

    // Override onDestroy method
    @Override
    public void onDestroy() {
        super.onDestroy();
        // если случайно служба будет уничтожена, то startBroadcasting(), чтобы снова запустить службу.
        startBroadcasting();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setAction("");
        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);
    }
}
