package org.tfc.adapters;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import com.appcelerator.cloud.sdk.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tfc.classes.Event;
import org.tfc.patxangueitor.*;
import org.tfc.patxangueitor.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventListFragment extends Fragment {
    private ListView lv;
    private TextView tv_newevent;
    private String llista_id;
    private String event_id;
    private JSONArray llista;
    private EventAdapter adapter;
    protected Object mActionMode;
    private String strId;
    List<Event> events;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.adminlist, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getActivity().getIntent().getExtras();
        llista_id = bundle.getString("Llista");
        llista = new JSONArray();

        tv_newevent = (TextView)getView().findViewById(org.tfc.patxangueitor.R.id.id_NewList);
        tv_newevent.setText("+ Event");
        tv_newevent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent_NewEvent = new Intent(getActivity().getApplicationContext(), act_newevent.class);
                Bundle b = new Bundle();
                b.putString("Llista", llista_id);
                intent_NewEvent.putExtras(b);
                startActivity(intent_NewEvent);
            }
        });

        LoadEventsTask eventload= new LoadEventsTask();
        eventload.execute();
    }

    private class LoadEventsTask extends AsyncTask<Void, Void, Void>
    {
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(getActivity());
            dia.setMessage("Recuperant dades. Esperi...");
            dia.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            ACSClient sdk = new ACSClient("iGXpZFRj2XCl9Aixrig80d0rrftOzRef",getActivity().getApplicationContext());

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("where", "{\"id_llista\" : \"" + llista_id + "\"}");
            data.put("order", "id_event");

            CCResponse response = null;
            try {
                response = sdk.sendRequest("objects/event/query.json", CCRequestMethod.GET, data);
            } catch (ACSClientError acsClientError) {
                acsClientError.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject responseJSON = response.getResponseData();
            CCMeta meta = response.getMeta();
            if("ok".equals(meta.getStatus())
                    && meta.getCode() == 200
                    && "queryCustomObjects".equals(meta.getMethod())) {
                try {
                    llista = responseJSON.getJSONArray("event");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            if (dia.isShowing()) {
                dia.dismiss();
            }

            /* Test adapter */

            ArrayList<String> values = new ArrayList<String>();


            events = new ArrayList<Event>();


            int i;
            for (i = 0; i < llista.length(); i++) {
                try {
                    JSONObject aux = llista.getJSONObject(i);
                    /* Test adapter */
                    String txteventid = null;
                    txteventid = aux.getString("id");
                    String txtEventName = null;
                    txtEventName = aux.getString("id_event");
                    String txtEventDate = null;
                    txtEventDate = aux.getString("data");
                    //String StrACS_idLlista, String StrEventName, String StrEventDate
                    Event event_aux = new Event(txteventid,llista_id,txtEventName,txtEventDate);
                    events.add(event_aux);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            /* Test adapter */
            //adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, values);
            adapter = new EventAdapter(getActivity(),R.layout.user,events);
            adapter.notifyDataSetChanged();

            lv = (ListView)getView().findViewById(R.id.lvAdmin);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    try {
                        JSONObject auxJSON = llista.getJSONObject(position);
                        event_id = auxJSON.getString("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent intent_Event = new Intent(getActivity().getApplicationContext(), adminlistevent.class);
                    Bundle b = new Bundle();
                    b.putString("Llista", llista_id);
                    b.putString("Event", event_id);
                    intent_Event.putExtras(b);
                    startActivity(intent_Event);
                }
            });

            lv.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int pos, long id) {
                    if (mActionMode != null) {
                        return false;
                    }
                    // Start the CAB using the ActionMode.Callback defined above
                    //mActionMode = getActivity().startActionMode(mActionModeCallback);
                    //lv.setSelected(true);
                    //return true;
                    //lv.selectedPosition = pos;
                    strId = events.get(pos).getObj_id();


                    lv.setItemChecked(pos, true);

                    mActionMode = getActivity().startActionMode(mActionModeCallback);
                    return true;
                }
            });

        }
    }

    private class DelEventTask extends AsyncTask<Void, Void, Void>{
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(getActivity());
            dia.setMessage("Esborrant event. Esperi...");
            dia.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            ACSClient sdk = new ACSClient("iGXpZFRj2XCl9Aixrig80d0rrftOzRef",getActivity().getApplicationContext()); // app key

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("id", strId);

            CCResponse response = null;
            try {
                response = sdk.sendRequest("objects/event/delete.json", CCRequestMethod.DELETE, data);
            } catch (ACSClientError acsClientError) {
                acsClientError.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject responseJSON = response.getResponseData();
            CCMeta meta = response.getMeta();
            if("ok".equals(meta.getStatus())
                    && meta.getCode() == 200
                    && "deleteObjects".equals(meta.getMethod())) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            if (dia.isShowing()) {
                dia.dismiss();
            }
        }
    }


    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    //shareCurrentItem();
                    DelEventTask taskDelEventUser= new DelEventTask();
                    taskDelEventUser.execute();

                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };
}
