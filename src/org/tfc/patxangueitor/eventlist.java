package org.tfc.patxangueitor;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import org.tfc.adapters.Group;
import org.tfc.adapters.ExpandableListAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.ExpandableListView;

public class eventlist extends Activity {
    // more efficient than HashMap for mapping integers to objects
    SparseArray<Group> groups = new SparseArray<Group>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventdatalist);
        createData();
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.expEventListView);
        ExpandableListAdapter adapter = new ExpandableListAdapter(this,
                groups);
        listView.setAdapter(adapter);

        TextView textView = (TextView) findViewById(R.id.EventtextView);
        textView.setText("Dades event");
    }


    public void createData() {
        Group group = new Group("Usuaris Event");
        group.children.add("Usuari event 1");
        group.children.add("Usuari event 2");
        group.children.add("Usuari event 3");
        groups.append(0,group);

        /*for (int j = 0; j < 5; j++) {
            Group group = new Group("Test " + j);
            for (int i = 0; i < 5; i++) {
                group.children.add("Sub Item" + i);
            }
            groups.append(j, group);
        }*/
    }

}