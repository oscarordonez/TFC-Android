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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.appcelerator.cloud.sdk.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tfc.patxangueitor.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SubsDataFragment extends Fragment {
    private String event_id;
    private JSONArray llista;

    public final static String APP_KEY = "iGXpZFRj2XCl9Aixrig80d0rrftOzRef";
    public final static String NOT_CONNECTED_TEXT = "No hi ha connexió de dades. No es pot realitzar l'operació";
    public final static String PROCESSING_TEXT = "Recuperant dades. Esperi...";
    public final static String NO_DATA_TEXT = "No s'han pogut recuperar dades event";

    public static boolean loadData = true;
    private NetworkReceiver receiver = new NetworkReceiver();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_data,
                container, false);
    }


     @Override
     public void onActivityCreated(Bundle savedInstanceState) {
         super.onActivityCreated(savedInstanceState);

         Bundle bundle = getActivity().getIntent().getExtras();
         event_id = bundle.getString("Event");
         llista = new JSONArray();

         if (checkConnection())
             loadData = true;
         else
             loadData = false;

         if (loadData){
             LoadEventDataTask eventdata= new LoadEventDataTask();
             eventdata.execute();
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
            LoadEventDataTask eventdata= new LoadEventDataTask();
            eventdata.execute();
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

    private class LoadEventDataTask extends AsyncTask<Void, Void, Void>
    {
        private ProgressDialog dia;

        @Override
        protected void onPreExecute(){
            dia = new ProgressDialog(getActivity());
            dia.setMessage(PROCESSING_TEXT);
            dia.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            ACSClient sdk = new ACSClient(APP_KEY,getActivity().getApplicationContext());

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("where", "{\"id\" : \"" + event_id + "\"}");
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
        protected void onPostExecute(Void result){
            JSONObject aux;
            aux = new JSONObject();
            TextView tv_eventname;
            TextView tv_eventdate;

            if (dia.isShowing())
                dia.dismiss();

            if (llista.length() != 1)
                Toast.makeText(getActivity(), NO_DATA_TEXT, Toast.LENGTH_SHORT).show();
            else {
                try {
                    aux = llista.getJSONObject(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String txtEventName = null;
                try {
                    txtEventName = aux.getString("id_event");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String txtEventDate = null;
                try {
                    txtEventDate = aux.getString("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                tv_eventname = (TextView)getView().findViewById(R.id.txt_eventname);
                tv_eventname.setText(txtEventName);

                tv_eventdate = (TextView)getView().findViewById(R.id.txt_eventadate);
                tv_eventdate.setText(txtEventDate);

            }
        }
    }
}

