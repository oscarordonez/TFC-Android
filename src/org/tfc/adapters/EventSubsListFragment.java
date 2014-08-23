package org.tfc.adapters;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.appcelerator.cloud.sdk.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tfc.classes.Event;
import org.tfc.patxangueitor.R;
import org.tfc.patxangueitor.act_newevent;
import org.tfc.patxangueitor.adminlistevent;
import org.tfc.patxangueitor.subslistevent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventSubsListFragment extends Fragment {
    private ListView lv;
    private String llista_id;
    private String event_id;
    private JSONArray llista;
    private EventAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.subslist, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getActivity().getIntent().getExtras();
        llista_id = bundle.getString("Llista");
        llista = new JSONArray();

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

            List<Event> events;
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
                    //String StrACS_idLlista, String StrEventName, String StrEventDate
                    Event event_aux = new Event(txteventid,llista_id,txtEventName,txtEventDate);
                    events.add(event_aux);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            adapter = new EventAdapter(getActivity(),R.layout.user,events);
            adapter.notifyDataSetChanged();

            lv = (ListView)getView().findViewById(R.id.lvSubs);
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

                    Intent intent_Event = new Intent(getActivity().getApplicationContext(), subslistevent.class);
                    Bundle b = new Bundle();
                    b.putString("Llista", llista_id);
                    b.putString("Event", event_id);
                    intent_Event.putExtras(b);
                    startActivity(intent_Event);
                }
            });
        }
    }
}
