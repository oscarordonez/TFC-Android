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
import android.widget.*;
import com.appcelerator.cloud.sdk.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tfc.classes.Llista;
import org.tfc.patxangueitor.R;
import org.tfc.patxangueitor.act_newlist;
import org.tfc.patxangueitor.adminlistuser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminListFragment extends Fragment {
    private ListView lv;
    private TextView tv_newlist;
    private String user_id;
    private String llista_id;
    private String nom_llista;
    private JSONObject auxJSON;
    private int i;
    private JSONArray llista;
    private LlistaAdapter adapter;
    private List<Llista> llistes;

    public static boolean loadData = true;

    private NetworkReceiver receiver = new NetworkReceiver();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.adminlist, container, false);
    }

    @Override
    public void onResume(){
        super.onResume();

        // Register BroadcastReceiver to track connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        getActivity().registerReceiver(receiver, filter);

        if (loadData == true){
            LoadListTask taskload= new LoadListTask();
            taskload.execute();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        //if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        //}
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        llista = new JSONArray();
        Bundle bundle = getActivity().getIntent().getExtras();
        user_id = bundle.getString("User");

        tv_newlist = (TextView)getView().findViewById(R.id.id_NewList);
        //tv_newlist.setText("+ Llista");
        tv_newlist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent_NewList = new Intent(getActivity().getApplicationContext(), act_newlist.class);
                startActivity(intent_NewList);
            }
        });

        if (loadData == true){
            LoadListTask taskload= new LoadListTask();
            taskload.execute();
        }
    }


    public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            // Check if network connection is available
            if (networkInfo != null && (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE || networkInfo.getType() == ConnectivityManager.TYPE_WIFI)){

                loadData = true;
            }
            else{
                loadData = false;
                Toast.makeText(context, "Desconnexió", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class LoadListTask extends AsyncTask<Void, Void, Void>
    {
        private ProgressDialog dia;
        private ProgressDialog dia1;

        @Override
        protected void onPreExecute() {
            setRetainInstance(true);
            dia = new ProgressDialog(getActivity());
            dia.setMessage("Recuperant dades. Esperi...");
            dia.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            /*-----------RECUPERAR LLISTES-----------------*/
            // Test ACS
            ACSClient sdk = new ACSClient("iGXpZFRj2XCl9Aixrig80d0rrftOzRef",getActivity().getApplicationContext()); // app key
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("where", "{\"user_id\" : \"" + user_id + "\"}");
            data.put("order", "nom");

            CCResponse response = null;
            try {
                response = sdk.sendRequest("objects/llista/query.json", CCRequestMethod.GET, data);
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
                    llista = responseJSON.getJSONArray("llista");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            /*--------------------*/
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {

            if (dia.isShowing()) {
                dia.dismiss();
            }

            //ArrayList<String> values = new ArrayList<String>();

            llistes = new ArrayList<Llista>();

            for (i = 0; i < llista.length(); i++) {
                try {
                    JSONObject aux = llista.getJSONObject(i);
                    String txtidlist = "";
                    txtidlist = aux.getString("id");
                    String txtlistname = "";
                    txtlistname = aux.getString("nom");
                    String txtlistdate = "";
                    txtlistdate = aux.getString("data");

                    Llista llista_aux = new Llista(txtidlist,txtlistname,txtlistdate);
                    llistes.add(llista_aux);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, values);
            adapter = new LlistaAdapter(getActivity(),R.layout.llista,llistes);
            adapter.notifyDataSetChanged();

            lv = (ListView)getView().findViewById(R.id.lvAdmin);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    Llista aux = llistes.get(position);
                    llista_id = aux.getLlista_id();
                    nom_llista = aux.getNom_llista();

                    Intent myIntent = new Intent(getActivity().getApplicationContext(), adminlistuser.class);
                    Bundle b = new Bundle();
                    b.putString("Llista", llista_id);
                    b.putString("NomLlista", nom_llista);
                    myIntent.putExtras(b);
                    startActivity(myIntent);
                }
            });
        }
    }
}