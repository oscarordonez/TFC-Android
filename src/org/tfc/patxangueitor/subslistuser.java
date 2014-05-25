package org.tfc.patxangueitor;

import android.content.Intent;
import android.view.View;
import org.tfc.adapters.Group;
import org.tfc.adapters.ExpandableListAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.ExpandableListView;

public class subslistuser extends Activity {
    // more efficient than HashMap for mapping integers to objects
   /* SparseArray<Group> groups = new SparseArray<Group>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subslist);
        createData();
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.explist);
        ExpandableListAdapter adapter = new ExpandableListAdapter(this,
                groups);
        listView.setAdapter(adapter);
    }
 
    
    public void createData() {
        Group group = new Group("Usuaris");
        group.children.add("Usuari 1");
        group.children.add("Usuari 2");
        group.children.add("Usuari 3");
        groups.append(0,group);

        Group group2 = new Group("Events");
        group2.children.add("Events 1");
        group2.children.add("Events 2");
        group2.children.add("Events 3");
        groups.append(1,group2);

        /*for (int j = 0; j < 5; j++) {
            Group group = new Group("Test " + j);
            for (int i = 0; i < 5; i++) {
                group.children.add("Sub Item" + i);
            }
            groups.append(j, group);
        }
    }   */

}
