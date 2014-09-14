package org.tfc.adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.tfc.classes.Llista;
import org.tfc.patxangueitor.R;

import java.util.List;

public class LlistaAdapter extends ArrayAdapter<Llista> {
    Context context;

    public LlistaAdapter(Context context, int resourceId,
                         List<Llista> llistes) {
        super(context, resourceId, llistes);
        this.context = context;
    }

    private class ViewHolder {
        TextView txtNom;
        TextView txtLloc;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Llista llista = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.llista, null);
            holder = new ViewHolder();
            holder.txtNom = (TextView) convertView.findViewById(R.id.txt_listname);
            holder.txtLloc= (TextView) convertView.findViewById(R.id.txt_listplace);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtNom.setText(llista.getNom_llista());
        holder.txtLloc.setText("Lloc: " + llista.getLloc_llista());

        return convertView;
    }


}
