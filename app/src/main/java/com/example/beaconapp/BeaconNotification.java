package com.example.beaconapp;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.service.BeaconService;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

/*
    Это класс, реализующий интерфейс BootstrapNotifier
*/
public class BeaconNotification extends Application implements BootstrapNotifier {
    public static BeaconManager beaconManager;
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    public static Region region1;

    @Override
    public void onCreate() {
        super.onCreate();

        // Получение объекта bluetooth adapter
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Проверка, поддерживается ли bluetooth устройством или нет
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"Bluetooth Not Supported",Toast.LENGTH_LONG).show();
        } else {
            // если bluetooth поддерживается, но не включен, включите его
            if (!mBluetoothAdapter.isEnabled()) {
                Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                bluetoothIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(bluetoothIntent);
            }
        }

        // получение экземпляра (объекта) beaconManager для класса, реализующего интерфейс BootstrapNotifier.
        beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);

        //Чтобы обнаружить проприетарные маяки, вы должны добавить строку, как показано ниже,
        // соответствующую вашему типу маяка. Выполните поиск в интернете "setBeaconLayout", чтобы получить правильное выражение.
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"));

        // Изменяет периоды сканирования по умолчанию при выполнении ранжирования.
        // Время периода сканирования может регулироваться внутренними алгоритмами или операционной системой.
        beaconManager.setForegroundScanPeriod(1100L);

        beaconManager.setForegroundBetweenScanPeriod(0L);

        // Позволяет отключить использование Android L BLE сканирования API на устройствах с API 21+
        // Если установлено значение false (по умолчанию), устройства с API 21+ будут использовать API Android L для сканирования маяков
        BeaconManager.setAndroidLScanningDisabled(true);

        // Устанавливает продолжительность в миллисекундах, затраченную на не сканирование между каждым циклом сканирования Bluetooth LE,
        // когда на переднем плане нет клиентов ранжирования/мониторинга
        beaconManager.setBackgroundBetweenScanPeriod(0L);

        // Задает длительность в миллисекундах каждого цикла сканирования Bluetooth LE для поиска маяков.
        beaconManager.setBackgroundScanPeriod(1100L);

        try {
            // Обновляет уже запущенное сканирование
            beaconManager.updateScanPeriods();
        } catch (Exception e) {
        }
        // разбудите приложение, когда будет виден маяк
        region1 = new Region("backgroundRegion",
                null, null, null);
        regionBootstrap = new RegionBootstrap(this, region1);

        // простое создание этого класса и удержание ссылки на него в пользовательском классе приложений автоматически приведет к тому,
        // что BeaconLibrary будет экономить батарею всякий раз, когда приложение не видно.
        // Это снижает энергопотребление bluetooth примерно на 60%.

        backgroundPowerSaver = new BackgroundPowerSaver(this);
    }

     /*
        Этот метод переопределения запускается, когда какой-то маяк попадет в зону действия устройства.
     */
    @Override
    public void didEnterRegion(Region region) {
        try {

            // Запуск the BeaconService class that extends Service
            Intent i = new   Intent(getApplicationContext(), com.example.beaconapp.BeaconService.class);
            startService(i);
        } catch (Exception e){}
    }

     /*
        Этот метод переопределения запускается, когда маяк, который входит в диапазон действия устройства,
        теперь был выведен из диапазона действия устройства.
     */
    @Override
    public void didExitRegion(Region region) {

        try {
            // Запуск the BeaconService class that extends Service
            Intent k = new Intent(getApplicationContext(), com.example.beaconapp.BeaconService.class);
            startService(k);
        }
        catch (Exception e) {
      }

    }

     /*
       Этот метод переопределения будет определять состояние устройства, находится ли оно в зоне действия маяка или нет,
       если да, то i = 1, а если нет, то i = 0
     */
    @Override
    public void didDetermineStateForRegion(int i, Region region) {

        try {
            // Запуск the BeaconService class that extends Service
            Intent k = new Intent(getApplicationContext(), com.example.beaconapp.BeaconService.class);
            startService(k);
        }
        catch (Exception e) {
    }
    }
}