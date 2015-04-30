package com.haab.trackme.utils;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haab.trackme.DetailsActivity;
import com.haab.trackme.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by abara on 3/30/2015.
 */
public class ListAdapter extends RecyclerView.Adapter<LocationHolder> {

    private Context mContext;
    private ArrayList<String> mLocality,mDate,mAddress,mLocation;

    public ListAdapter(Context context,ArrayList<String> locality,ArrayList<String> date,ArrayList<String> address,ArrayList<String> location){
        this.mContext = context;
        mLocality = locality;
        mDate = date;
        mAddress = address;
        mLocation = location;
    }

    @Override
    public LocationHolder onCreateViewHolder(ViewGroup viewGroup, final int pos) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.simple_list,viewGroup,false);

        Collections.reverse(mAddress);
        Collections.reverse(mLocation);
        Collections.reverse(mLocality);
        Collections.reverse(mDate);

        LocationHolder holder = new LocationHolder(view, new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent detailIntent = new Intent(mContext, DetailsActivity.class);
                detailIntent.putExtra("location",mLocation.get(position))
                .putExtra("address",mAddress.get(position))
                .putExtra("date",mDate.get(position));
                mContext.startActivity(detailIntent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(LocationHolder locationHolder, int pos) {
        locationHolder.text1.setText(mLocality.get(pos));
        locationHolder.text2.setText(mDate.get(pos));
    }

    @Override
    public int getItemCount() {
        return mLocality.size();
    }
}

class LocationHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    RelativeLayout list;
    OnItemClickListener listener;
    TextView text1,text2;

    public LocationHolder(View v,OnItemClickListener l) {
        super(v);
        list = (RelativeLayout) v.findViewById(R.id.listItem);
        text1 = (TextView) v.findViewById(R.id.text1);
        text2 = (TextView) v.findViewById(R.id.text2);
        this.listener = l;
        list.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        listener.onItemClick(getPosition());
    }
}

interface OnItemClickListener{
    public void onItemClick(int position);
}