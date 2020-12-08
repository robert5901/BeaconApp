package com.example.beaconapp.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beaconapp.R;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.List;

/*
     Adapter for Recycler View
*/
public class BeaconsAdapter extends RecyclerView.Adapter<BeaconsAdapter.ViewHolder> {
    private List<Beacon> beacons;
    public BeaconsAdapter(List<Beacon> beacons) {
        this.beacons=beacons;
    }

    private static OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(Beacon beacon);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text="";
        // Getting Array List within respective position

        // Displaying UUID
        text = String.valueOf(beacons.get(position).getId1());
        holder.uuid.setText(text);

        //Displaying Major
        text = String.valueOf(beacons.get(position).getId2());
        holder.major.setText(text);

        //Displaying Minor
        text = String.valueOf(beacons.get(position).getId3());
        holder.minor.setText(text);

        //Displaying distance
        text = String.valueOf(Math.round(beacons.get(position).getDistance() * 100.0) / 100.0);
        holder.distance.setText(text);

        //Displaying RSSI
        text = String.valueOf(beacons.get(position).getRssi());
        holder.rssi.setText(text);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener!=null){
                    mListener.onItemClick(beacons.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount(){
        return beacons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //UUID
        private final TextView uuid;

        //Major
        private final TextView major;

        //Minor
        private final TextView minor;

        //Distance
        private final TextView distance;

        //UUID
        private final TextView rssi;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            uuid = itemView.findViewById(R.id.uuid);
            major = itemView.findViewById(R.id.major);
            minor = itemView.findViewById(R.id.minor);
            distance = itemView.findViewById(R.id.distance);
            rssi = itemView.findViewById(R.id.rssi);
        }
    }
}