package com.example.beaconapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.altbeacon.beacon.service.BeaconService;

/*
    Это класс, который наследует широковещательный приемник
    Этот класс BroadcastReceiver используется для перезапуска службы всякий раз,
    когда случайно наша служба будет уничтожена.
 */
public class BeaconBroadCast extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Again Starting the service");
        //Again starting the service
        context.startService(new Intent(context, com.example.beaconapp.BeaconService.class));
    }
}
