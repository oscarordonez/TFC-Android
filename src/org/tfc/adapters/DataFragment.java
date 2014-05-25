package org.tfc.adapters;

import android.app.ProgressDialog;
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

public class DataFragment extends Fragment {
    private String event_id;
    private JSONArray llista;

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

         LoadEventDataTask eventdata= new LoadEventDataTask();
         eventdata.execute();
     }

     private class LoadEventDataTask extends AsyncTask<Void, Void, Void>
     {
         private ProgressDialog dia;

        @Override
         protected void onPreExecute(){
             dia = new ProgressDialog(getActivity());
             dia.setMessage("Recuperant dades event. Esperi...");
             dia.show();
        }

         @Override
         protected Void doInBackground(Void... params)
         {
             ACSClient sdk = new ACSClient("iGXpZFRj2XCl9Aixrig80d0rrftOzRef",getActivity().getApplicationContext());

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
             TextView tv_eventname;
             TextView tv_eventdate;

             aux = new JSONObject();

             if (dia.isShowing()){
             dia.dismiss();
             }

             if (llista.length() != 1) {
                 Toast toast1 =
                         Toast.makeText(getActivity().getApplicationContext(),
                                 "No s'han pogut recuperar dades event", Toast.LENGTH_LONG);
                 toast1.show();
             } else {
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

