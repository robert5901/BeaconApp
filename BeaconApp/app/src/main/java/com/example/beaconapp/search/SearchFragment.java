package com.example.beaconapp.search;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.beaconapp.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;


public class SearchFragment extends Fragment implements BeaconConsumer {

    //Relative Layout
    RelativeLayout rl;
    //Recycler View
    private RecyclerView rv;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    //Beacon Manager
    private BeaconManager beaconManager;
    // Progress bar
    private ProgressBar pb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //получение экземпляра (объекта) beaconManager для класса Main Activity
        beaconManager = BeaconManager.getInstanceForApplication(Objects.requireNonNull(getActivity()));

        // Чтобы обнаружить проприетарные маяки, вы должны добавить строку, как показано ниже,
        // соответствующую вашему типу маяка. Выполните поиск в интернете "setBeaconLayout", чтобы получить правильное выражение.
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"));


        //Binding MainActivity to the BeaconService.
        beaconManager.bind(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_search, container, false);

        // Intializing the Layout

        //Relative Layout
        rl = v.findViewById(R.id.Relative_One);

        // Recycler View
        rv = v.findViewById(R.id.search_recycler);

        //Progress Bar
        pb = v.findViewById(R.id.pb);
        return v;
    }

    @Override
    public void onBeaconServiceConnect() {

        //Построение нового объекта Region, который будет использоваться для ранжирования или мониторинга
        final Region region = new Region("myBeaons",null, null, null);

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
                System.out.println( "I have just switched from seeing/not seeing beacons: " + state);
            }
        });


        /*Указывает класс, который должен вызываться каждый раз,
        когда BeaconService получает данные о дальности, что номинально происходит один раз в секунду при обнаружении маяков.*/
        beaconManager.addRangeNotifier(new RangeNotifier() {

            /*
               Этот метод сообщает нам все коллекции маяков и их детали,
               которые обнаруживаются в пределах диапазона устройством
             */
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

                // проверка наличия Маяка внутри коллекции (например, списка) или его отсутствия

                // если Маяк обнаружен, то размер коллекции равен > 0
                if (beacons.size() > 0) {
                    try{
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                // Make ProgressBar Invisible
                                pb.setVisibility(View.INVISIBLE);

                                // Make Relative Layout to be Gone
                                rl.setVisibility(View.GONE);

                                //Make RecyclerView to be visible
                                rv.setVisibility(View.VISIBLE);

                                // Setting up the layout manager to be linear
                                layoutManager = new LinearLayoutManager(getActivity());
                                rv.setLayoutManager(layoutManager);
                            }
                        });
                    }
                    catch(Exception e){

                    }
                    final ArrayList<ArrayList<String>> arrayList = new ArrayList<ArrayList<String>>();

                    // Итерация по всем маякам из коллекции маяков
                    for (Beacon b:beacons){

                        //UUID
                        String uuid = String.valueOf(b.getId1());

                        //Major
                        String major = String.valueOf(b.getId2());

                        //Minor
                        String minor = String.valueOf(b.getId3());

                        //RSSI
                      //  String rssi = String.valueOf(b.getRssi());

                        //Distance
                        double distance1 =b.getDistance();
                        String distance = String.valueOf(Math.round(distance1*100.0)/100.0);

                        ArrayList<String> arr = new ArrayList<String>();
                        arr.add(uuid);
                        arr.add(major);
                        arr.add(minor);
                     //   arr.add(rssi);
                        arr.add(distance + " meters");
                        arrayList.add(arr);

                    }
                    try {
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                // Setting Up the Adapter for Recycler View
                                adapter = new RecyclerAdapter(arrayList);
                                rv.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }catch(Exception e){

                    }
                }


                // если Маяк не обнаружен, то размер коллекции равен = 0
                else if (beacons.size()==0) {
                    try {
                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                // Setting Progress Bar InVisible
                                pb.setVisibility(View.INVISIBLE);

                                // Setting RelativeLayout to be Visible
                                rl.setVisibility(View.VISIBLE);

                                // Setting RecyclerView to be Gone
                                rv.setVisibility(View.GONE);
                            }
                        });
                    } catch (Exception e) {

                    }
                }
            }
        });
        try {

            // Сообщает службе маяков начать поиск маяков, соответствующих переданному объекту региона.
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {    }
    }

    /*
         Если мы реализуем BeaconConsumer интерфейс во фрагменте
         (а не в Activity, службы или экземпляра приложения), нам нужно связать все методы в цепочку.
     */
    @Override
    public Context getApplicationContext() {
        return Objects.requireNonNull(getActivity()).getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        Objects.requireNonNull(getActivity()).unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return Objects.requireNonNull(getActivity()).bindService(intent, serviceConnection, i);
    }


    // Override onDestroy Method
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Чтобы избежать утечки, отключаем Activity или Service от BeaconService.
        beaconManager.unbind(this);
    }
}