package com.example.beaconapp.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beaconapp.R;

import java.util.ArrayList;

/*
     Adapter for Recycler View
*/
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    ArrayList<ArrayList<String>> arr;

    // Constructor
    public RecyclerAdapter(ArrayList<ArrayList<String>> arr)
    {
        this.arr = arr;
    }

    /*
       View Holder class to instantiate views
     */
    static class ViewHolder extends RecyclerView.ViewHolder{

        //UUID
        private final TextView uuid;

        //Major
        private final TextView major;

        //Minor
        private final TextView minor;

        //Distance
        private final TextView distance;

        //View Holder Class Constructor
        public ViewHolder(View itemView)
        {
            super(itemView);

            //Initializing views
            uuid = itemView.findViewById(R.id.uuid);
            major = itemView.findViewById(R.id.major);
            minor = itemView.findViewById(R.id.minor);
            distance = itemView.findViewById(R.id.distance);
        }
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder holder, int position) {

        // Getting Array List within respective position
        ArrayList<String> arrayList = arr.get(position);

        // Checking if arrayList size > 0
        if (arrayList.size()>0){

            // Displaying UUID
            holder.uuid.setText(arrayList.get(0));

            //Displaying Major
            holder.major.setText(arrayList.get(1));

            //Displaying Minor
            holder.minor.setText(arrayList.get(2));

            //Displaying distance
            holder.distance.setText(arrayList.get(3));
        }
    }
    @Override
    public int getItemCount()
    {
        return arr.size();
    }
}