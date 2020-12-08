package com.example.beaconapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import org.altbeacon.beacon.Beacon;

public class BeaconInfoFragment extends Fragment {

    public BeaconInfoFragment() {
    }
    public static BeaconInfoFragment newInstance(){
        BeaconInfoFragment fragment=new BeaconInfoFragment();
        return fragment;
    }
    private TextView uuid;
    private TextView major;
    private TextView minor;
    private TextView distance;
    private TextView rssi;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_beacon_info, container, false);
        uuid = view.findViewById(R.id.uuid);
        major = view.findViewById(R.id.major);
        minor = view.findViewById(R.id.minor);
        distance = view.findViewById(R.id.distance);
        rssi = view.findViewById(R.id.rssi);

        if (Variables.beacon != null) {
            uuid.setText(String.valueOf(Variables.beacon.getId1()));
            major.setText(String.valueOf(Variables.beacon.getId2()));
            minor.setText(String.valueOf(Variables.beacon.getId3()));
            double distance1 = Variables.beacon.getDistance();
            String distance_ = String.valueOf(Math.round(distance1 * 100.0) / 100.0);
            distance.setText(distance_);
            rssi.setText(String.valueOf(Variables.beacon.getRssi()));
        }
//        back_btn.setOnClickListener(v -> Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_beaconInfoFragment_to_mainFragment));
        return view;
    }
}
