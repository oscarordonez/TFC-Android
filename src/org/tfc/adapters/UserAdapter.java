package org.tfc.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.tfc.classes.User;
import org.tfc.patxangueitor.R;

import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {
    Context context;

    public UserAdapter(Context context, int resourceId,
                                 List<User> users) {
        super(context, resourceId, users);
        this.context = context;
    }

    private class ViewHolder {
        TextView txtUser;
        TextView txtName;
        TextView txtACSId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        User user = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.user, null);
            holder = new ViewHolder();
            holder.txtUser = (TextView) convertView.findViewById(R.id.txt_username);
            holder.txtName = (TextView) convertView.findViewById(R.id.txt_firstname);
            holder.txtACSId = (TextView) convertView.findViewById(R.id.txt_id);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtUser.setText(user.getUser());
        holder.txtName.setText("Nom: " + user.getFirstName());
        holder.txtACSId.setText("Id usuari: " + user.getACS_id());

        return convertView;
    }


}
