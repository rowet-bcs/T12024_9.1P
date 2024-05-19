package com.example.task91p;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<Advert> {

    // Custom list adapter class to display advert list
    public ListAdapter(@NonNull Context context, ArrayList<Advert> advertArrayList){
        super(context, R.layout.advert_item_layout, advertArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent){
        // Retrieve advert for given position
        Advert advert = getItem(position);

        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.advert_item_layout, parent, false);
        }

        // Populate display with advert type, lost/found date and title
        TextView type = view.findViewById(R.id.typeText);
        TextView date = view.findViewById(R.id.dateText);
        TextView title = view.findViewById(R.id.advertNameText);

        type.setText(advert.getType());
        date.setText(advert.getDate());
        title.setText(advert.getTitle());

        return view;
    }
}

