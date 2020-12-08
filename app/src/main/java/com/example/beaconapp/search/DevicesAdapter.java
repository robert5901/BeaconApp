package com.example.beaconapp.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beaconapp.R;

import org.altbeacon.beacon.Beacon;

import java.util.List;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {

    private final List<BleDevice> devices;

    DevicesAdapter(List<BleDevice> devices){
        this.devices=devices;
    }

   /* private static OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick();
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }*/
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_for_ble, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text="";
        // Displaying MAC
        text=devices.get(position).mac;
        holder.mac.setText(text);

        //Displaying RSSI
        text=devices.get(position).rssi;
        holder.rssi.setText(text);
        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener!=null){
                    mListener.onItemClick();
                }
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mac;
        //RSSI
        private final TextView rssi;

        //View Holder Class Constructor
        public ViewHolder(View itemView)
        {
            super(itemView);

            //Initializing views
            rssi = itemView.findViewById(R.id.rssi);
            mac = itemView.findViewById(R.id.mac);
        }
    }
}
