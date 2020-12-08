package com.example.beaconapp.search;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.beaconapp.BeaconInfoFragment;
import com.example.beaconapp.MainActivity;
import com.example.beaconapp.R;
import com.example.beaconapp.Variables;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


public class SearchFragment extends Fragment implements BeaconConsumer {

    //Relative Layout
    RelativeLayout rl;
    //Recycler View
    private RecyclerView beacons_rv, devices_rv;
    private BeaconsAdapter beaconsAdapter;
    private DevicesAdapter devicesAdapter;
    //Beacon Manager
    private BeaconManager beaconManager;
    // Progress bar
    private ProgressBar pb;
    // Для поиска BLE
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    private List<BleDevice> devices;
    private List<Beacon> beacons_;


//    private final static int REQUEST_ENABLE_BT = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        devices = new ArrayList<>();
        beacons_ = new ArrayList<>();
        btManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        // получение экземпляра (объекта) beaconManager для класса Main Activity
        beaconManager = BeaconManager.getInstanceForApplication(requireActivity());

        // Чтобы обнаружить проприетарные маяки, вы должны добавить строку, как показано ниже,
        // соответствующую вашему типу маяка. Выполните поиск в интернете "setBeaconLayout", чтобы получить правильное выражение.
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"));
        beaconManager.setForegroundScanPeriod(1100L);

        beaconManager.setForegroundBetweenScanPeriod(0L);

        // Позволяет отключить использование Android L BLE сканирования API на устройствах с API 21+
        // Если установлено значение false (по умолчанию), устройства с API 21+ будут использовать API Android L для сканирования маяков
        //BeaconManager.setAndroidLScanningDisabled(true);

        // Устанавливает продолжительность в миллисекундах, затраченную на не сканирование между каждым циклом сканирования Bluetooth LE,
        // когда на переднем плане нет клиентов ранжирования/мониторинга
        beaconManager.setBackgroundBetweenScanPeriod(0L);

        // Задает длительность в миллисекундах каждого цикла сканирования Bluetooth LE для поиска маяков.
        beaconManager.setBackgroundScanPeriod(10000L);

        try {
            // Обновляет уже запущенное сканирование
            beaconManager.updateScanPeriods();
        } catch (Exception e) {
            Log.d("BeaconUpdateException", ""+e.getMessage());
        }
        beaconManager.bind(this);
        startScanning();

        //Binding MainActivity to the BeaconService.

//        if (btAdapter != null && !btAdapter.isEnabled()) {
//            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_search, container, false);

        // Intializing the Layout

        //Relative Layout
        rl = v.findViewById(R.id.Relative_One);

        // Recycler View
        beacons_rv = v.findViewById(R.id.beacons_rv);
        devices_rv=v.findViewById(R.id.devices_rv);
        beaconsAdapter = new BeaconsAdapter(beacons_);
        devicesAdapter = new DevicesAdapter(devices);
        beacons_rv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        devices_rv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        beacons_rv.setAdapter(beaconsAdapter);
        devices_rv.setAdapter(devicesAdapter);
        //Progress Bar
        pb = v.findViewById(R.id.pb);
        return v;
    }


    private ScanCallback leScanCallback = new ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            /*Log.d("result", result.toString());
            Log.d("name", result.getDevice().getName() + "\n");
            Log.d("rssi", String.valueOf(result.getRssi()));
            Log.d("mac", String.valueOf(result.getDevice()));*/
            BleDevice bleDevice = new BleDevice(String.valueOf(result.getDevice()), String.valueOf(result.getRssi()));
            boolean flag = false;
            for (BleDevice device : devices) {
                if (bleDevice.mac.equals(device.mac)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                devices.add(bleDevice);
               /* adapter=new RecyclerAdapter(beacons_, devices);
                rv.setAdapter(adapter);*/
                requireActivity().runOnUiThread(() -> {
                    devicesAdapter=new DevicesAdapter(devices);
                    devices_rv.setAdapter(devicesAdapter);
                    /*devicesAdapter.setOnItemClickListener(new DevicesAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick() {
                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_mainFragment_to_beaconInfoFragment);
                        }
                    });*/
                });
            }
            //peripheralTextView.append("Device Name: " + result.getDevice().getName() + " rssi: " + result.getRssi() + "\n");
        }
    };


    public void startScanning() {
        try {
            requireActivity().runOnUiThread(() -> {
                btScanner.startScan(leScanCallback);

                // Make ProgressBar Invisible
                pb.setVisibility(View.GONE);

                // Make Relative Layout to be Gone
                rl.setVisibility(View.GONE);

                //Make RecyclerView to be visible
                devices_rv.setVisibility(View.VISIBLE);

                // Setting up the layout manager to be linear


            });
        } catch (Exception e) {
            Log.d("DEVICE_SCANNING", "Device scanning error");
        }


    }


    @Override
    public void onBeaconServiceConnect() {

        //Построение нового объекта Region, который будет использоваться для ранжирования или мониторинга
        final Region region = new Region("myBeacons", null, null, null);

        //Указывает класс, который должен вызываться каждый раз, когда служба маяков видит или перестает видеть область маяков.
        beaconManager.addMonitorNotifier(new MonitorNotifier() {

            //Этот override метод запускается, когда какой-то маяк попадет в зону действия устройства.
            @Override
            public void didEnterRegion(Region region) {
                System.out.println("ENTER ------------------->");
                try {

                    /*Cообщает BeaconService начать поиск маяков, соответствующих переданному объекту региона,
                      и предоставлять обновления по расчетному расстоянию mDistance каждые секунды, пока маяки в регионе видны.*/
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            /*
                 Этот метод запускается, когда маяк,
                 который входит в диапазон действия устройства,теперь был выведен из диапазона действия устройства.
             */
            @Override
            public void didExitRegion(Region region) {
                System.out.println("EXIT----------------------->");
                try {

                    /*Говорит BeaconService прекратить поиск маяков, соответствующих объекту пройденного региона,
                      и предоставлять для них информацию о расстоянии(mDistance).*/
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }


            /*
               Этот метод будет определять состояние устройства, независимо от того,
               находится ли устройство в зоне действия маяка или нет , если да, то i = 1, а если нет, то i = 0
            */
            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                switch (state){
                    case 0:
                        System.out.println("Not seeing beacons");
                        break;
                    case 1:
                        System.out.println("Seeing beacons");
                        break;
                }
            }
        });


        /*Указывает класс, который должен вызываться каждый раз,
        когда BeaconService получает данные о дальности, что номинально происходит один раз в секунду при обнаружении маяков.*/
        /*
           Этот метод сообщает нам все коллекции маяков и их детали,
           которые обнаруживаются в пределах диапазона устройством
         */
        beaconManager.addRangeNotifier((beacons, region1) -> {
            // проверка наличия Маяка внутри коллекции (например, списка) или его отсутствия

            // если Маяк обнаружен, то размер коллекции равен > 0
            if (beacons.size() > 0) {
                beacons_=new ArrayList<>(beacons);
                if (pb.getVisibility()!=View.INVISIBLE) {
                    pb.setVisibility(View.INVISIBLE);
                }

                // Make Relative Layout to be Gone
                if (rl.getVisibility()!=View.GONE) {
                    rl.setVisibility(View.GONE);
                }

                //Make RecyclerView to be visible
                if (beacons_rv.getVisibility()!=View.VISIBLE) {
                    beacons_rv.setVisibility(View.VISIBLE);
                }
                requireActivity().runOnUiThread(() -> {
                    beaconsAdapter=new BeaconsAdapter(beacons_);
                    beaconsAdapter.setOnItemClickListener(beacon -> {
                        Variables.beacon=beacon;
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_mainFragment_to_beaconInfoFragment);
                    });
                    beacons_rv.setAdapter(beaconsAdapter);
                });
                // final ArrayList<ArrayList<String>> arrayList = new ArrayList<>();

                // Итерация по всем маякам из коллекции маяков
           /* for (Beacon b : beacons) {

                //UUID
                String uuid = String.valueOf(b.getId1());

                //Major
                String major = String.valueOf(b.getId2());

                //Minor
                String minor = String.valueOf(b.getId3());

                //Distance
                double distance1 = b.getDistance();
                String distance = String.valueOf(Math.round(distance1 * 100.0) / 100.0);

                //RSSI
                String rssi = String.valueOf(b.getRssi());


                ArrayList<String> arr = new ArrayList<String>();
                arr.add(uuid);
                arr.add(major);
                arr.add(minor);
                arr.add(distance + " meters");
                arr.add(rssi);
                arrayList.add(arr);

            }*/
            }
            // если Маяк не обнаружен, то размер коллекции равен = 0
            else
                if (beacons.size() == 0) {

                // Setting Progress Bar InVisible
                pb.setVisibility(View.INVISIBLE);

                // Setting RelativeLayout to be Visible
                Toast.makeText(getContext(), "No beacons nearby", Toast.LENGTH_SHORT).show();
                //rl.setVisibility(View.VISIBLE);

                // Setting RecyclerView to be Gone
                beacons_rv.setVisibility(View.GONE);
            }
        });
        try {

            // Сообщает службе маяков начать поиск маяков, соответствующих переданному объекту региона.
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            Log.d("MonitoringException", ""+e.getMessage());
        }
    }

    /*
         Если мы реализуем BeaconConsumer интерфейс во фрагменте
         (а не в Activity, службы или экземпляра приложения), нам нужно связать все методы в цепочку.
     */
    @Override
    public Context getApplicationContext() {
        return requireActivity().getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        requireActivity().unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return requireActivity().bindService(intent, serviceConnection, i);
    }


    // Override onDestroy Method
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Чтобы избежать утечки, отключаем Activity или Service от BeaconService.
        beaconManager.unbind(this);
    }
}