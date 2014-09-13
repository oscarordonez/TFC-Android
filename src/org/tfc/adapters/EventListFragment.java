package org.tfc.adapters;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    public final static String APP_KEY = "iGXpZFRj2XCl9Aixrig80d0rrftOzRef";
    public final static String NOT_CONNECTED_TEXT = "No hi ha connexió de dades. No es pot realitzar l'operació";
    public final static String PROCESSING_TEXT = "Recuperant dades. Esperi...";
    public final static String PROCESSING_TEXT2 = "Esborrant event. Esperi...";

    public static boolean loadData = true;
    private NetworkReceiver receiver = new NetworkReceiver();

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

        if (checkConnection())
            loadData = true;
        else
            loadData = false;

        if (loadData){
            LoadEventsTask eventload= new LoadEventsTask();
            eventload.execute();
        }
        else
            Toast.makeText(getActivity(), NOT_CONNECTED_TEXT, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume(){
        super.onResume();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        getActivity().registerReceiver(receiver, filter);

        if (checkConnection())
            loadData = true;
        else
            loadData = false;

        if (loadData){
            LoadEventsTask eventload= new LoadEventsTask();
            eventload.execute();
        }
        else
            Toast.makeText(getActivity(),NOT_CONNECTED_TEXT, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause(){
        super.onPause();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
    }

    public class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected())
                loadData = true;
            else
                loadData = false;
        }
    }

    private Boolean checkConnection(){
        Boolean booLoad;
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected())
            booLoad = true;
        else
            booLoad = false;

        return booLoad;
    }

    private class LoadEventsTask extends AsyncTask<Void, Void, Void>
    {
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(getActivity());
            dia.setMessage(PROCESSING_TEXT);
            dia.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            ACSClient sdk = new ACSClient(APP_KEY,getActivity().getApplicationContext());

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

            events = new ArrayList<Event>();
            int i;
            for (i = 0; i < llista.length(); i++) {
                try {
                    JSONObject aux = llista.getJSONObject(i);
                    String txteventid = null;
                    txteventid = aux.getString("id");
                    String txtEventName = null;
                    txtEventName = aux.getString("id_event");
                    String txtEventDate = null;
                    txtEventDate = aux.getString("data");
                    Event event_aux = new Event(txteventid,llista_id,txtEventName,txtEventDate);
                    events.add(event_aux);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

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
                    strId = events.get(pos).getObj_id();
                    lv.setItemChecked(pos, true);
                    mActionMode = getActivity().startActionMode(mActionModeCallback);
                    return true;
                }
            });
        }
    }

    private class DelEventTask extends AsyncTask<Void, Void, Boolean>{
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(getActivity());
            dia.setMessage(PROCESSING_TEXT2);
            dia.show();
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            Boolean booStatus = false;
            ACSClient sdk = new ACSClient(APP_KEY,getActivity().getApplicationContext()); // app key

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

            CCMeta meta = response.getMeta();
            if("ok".equals(meta.getStatus())
                    && meta.getCode() == 200
                    && "deleteObjects".equals(meta.getMethod())) {
                booStatus = true;
            }
            return booStatus;
        }

        @Override
        protected void onPostExecute(Boolean booStatus)
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
                    if (checkConnection())
                        loadData = true;
                    else
                        loadData = false;

                    if (loadData){
                        DelEventTask taskDelEventUser= new DelEventTask();
                        taskDelEventUser.execute();
                    }
                    else
                        Toast.makeText(getActivity(), NOT_CONNECTED_TEXT, Toast.LENGTH_LONG).show();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };
}
