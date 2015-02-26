package com.itel.app.wrload3;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<ItemData> itemsData;

    public MyAdapter(ArrayList<ItemData> itemsData) {
        this.itemsData = itemsData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData

        Log.d("*** itemsData.get(position).getName() ***", String.valueOf(itemsData.get(position).getName()));


        viewHolder.imageView.setImageResource(itemsData.get(position).getphImageUrl());
        viewHolder.patName.setText(itemsData.get(position).getName());
        viewHolder.sessLen.setText(itemsData.get(position).getSchedule_length());



    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView patName;
        public TextView sessLen;

        public TextView txtViewSessionLength;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            imageView = (ImageView) itemLayoutView.findViewById(R.id.imageView);
            patName = (TextView) itemLayoutView.findViewById(R.id.patName);
            sessLen = (TextView) itemLayoutView.findViewById(R.id.sessLen);
        }
    }





    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemsData.size();
    }
}
