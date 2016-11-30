package com.example.maximeweekhout.bioscoopvandaag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Maxime Weekhout on 18-10-2016.
 */

public class TheaterAdapter extends ArrayAdapter<Theater> {

    /**
     * Load theaters in listview
     * @param context Application Context
     * @param theaters List with theaters
     */
    public TheaterAdapter(Context context, List<Theater> theaters) {
        super(context, 0, theaters);
    }

    /**
     * Actually loads theater in list item
     * @param position Position of element
     * @param convertView view to be converted
     * @param parent the parent
     * @return Filled view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Theater theater = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.theater_item, parent, false);
        }
        TextView name = (TextView) convertView.findViewById(R.id.Name);
        name.setText(theater.getName());
        return convertView;
    }
}
