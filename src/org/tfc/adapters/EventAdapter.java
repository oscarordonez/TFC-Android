package org.tfc.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.tfc.classes.Event;
import org.tfc.patxangueitor.R;

import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {
    Context context;

    public EventAdapter(Context context, int resourceId,
                        List<Event> events) {
        super(context, resourceId, events);
        this.context = context;
    }

    private class ViewHolder {
        TextView tvEventName;
        TextView tvEventDate;
        TextView tvLocation;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Event event = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.user, null);
            holder = new ViewHolder();
            holder.tvEventName = (TextView) convertView.findViewById(R.id.txt_username);
            holder.tvEventDate = (TextView) convertView.findViewById(R.id.txt_firstname);
            holder.tvLocation = (TextView) convertView.findViewById(R.id.txt_id);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.tvEventName.setText(event.getEventName());
        holder.tvEventDate.setText("Data: " + event.getEventDate());
        holder.tvLocation.setText("Lloc: ");

        return convertView;
    }
}
