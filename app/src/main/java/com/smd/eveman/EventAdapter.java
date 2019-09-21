package com.smd.eveman;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class EventAdapter extends BaseAdapter {


    ArrayList<Event> eventList;
    Context context;
    LayoutInflater inflater;
    ConstraintLayout constraintLayout;

    EventAdapter(Context c, ArrayList<Event> list){
        inflater =  (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context=c;
        eventList=list;
    }


    @Override
    public int getCount() {
        return eventList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if( convertView == null ){
            //We must create a View:
            convertView = inflater.inflate(R.layout.event_layout, parent, false);

        }

        Event eventItem = eventList.get(position);

        TextView title = (TextView) convertView.findViewById(R.id.etitle);
        TextView place = (TextView) convertView.findViewById(R.id.eplace);
        TextView desc = (TextView) convertView.findViewById(R.id.edesc);
        ImageButton viewloc = (ImageButton) convertView.findViewById(R.id.eviewloc);
        viewloc.setTag(position);
        viewloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Event temp = eventList.get((Integer) v.getTag());
                Intent intent = new Intent(context,MapsActivity.class);
                intent.putExtra("location",temp);
                context.startActivity(intent);

            }
        });

        title.setText(eventItem.EventTitle);
        place.setText(eventItem.Place);
        desc.setText(eventItem.Desc);


        return convertView;
    }


}
