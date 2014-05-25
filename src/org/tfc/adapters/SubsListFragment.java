package org.tfc.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.*;
import com.appcelerator.cloud.sdk.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tfc.patxangueitor.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.tfc.patxangueitor.subslistuser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SubsListFragment extends Fragment{
    private ListView lv;
    private String user_id;
    private String llista_id;
    private JSONObject auxJSON;
    private int i;
    private JSONArray llista;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.subslist, container, false);
    }

    public void onResume(){
        super.onResume();
        LoadListTask taskload= new LoadListTask();
        taskload.execute();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        llista = new JSONArray();
        Bundle bundle = getActivity().getIntent().getExtras();
        user_id = bundle.getString("User");

        LoadListTask taskload= new LoadListTask();
        taskload.execute();
    }

    private class LoadListTask extends AsyncTask<Void, Void, Void>
    {
        //private ProgressDialog dia;

        /*@Override
        protected void onPreExecute() {
            dia = new ProgressDialog(getActivity());
            dia.setMessage("Recuperant dades. Esperi...");
            dia.show();
        } */

        @Override
        protected Void doInBackground(Void... params)
        {
            /*-----------RECUPERAR LLISTES-----------------*/
            // Test ACS
            ACSClient sdk = new ACSClient("iGXpZFRj2XCl9Aixrig80d0rrftOzRef",getActivity().getApplicationContext()); // app key
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("where", "{\"id_user\" : \"" + user_id + "\"}");
            data.put("order", "id_user");

            CCResponse response = null;
            try {
                response = sdk.sendRequest("objects/subscripcio_usuari/query.json", CCRequestMethod.GET, data);
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
                    llista = responseJSON.getJSONArray("subscripcio_usuari");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            /*if (dia.isShowing()) {
                dia.dismiss();
            } */

            ArrayList<String> values = new ArrayList<String>();
            for (i = 0; i < llista.length(); i++) {
                try {
                    auxJSON = llista.getJSONObject(i);
                    values.add(i, auxJSON.getString("id_llista"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, values);
            adapter.notifyDataSetChanged();

            lv = (ListView)getView().findViewById(R.id.lvSubs);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    try {
                        auxJSON = llista.getJSONObject(position);
                        llista_id = auxJSON.getString("id_llista");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    /*Intent myIntent = new Intent(getActivity().getApplicationContext(), class);
                    Bundle b = new Bundle();
                    b.putString("Llista", llista_id);
                    myIntent.putExtras(b);
                    startActivity(myIntent);*/
                }
            });
        }
    }
}