package org.tfc.adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.*;
import android.widget.*;
import com.appcelerator.cloud.sdk.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tfc.classes.Llista;
import org.tfc.patxangueitor.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import org.tfc.patxangueitor.subslistuser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubsListFragment extends Fragment{
    private ListView lv;
    private String user_id;
    private String llista_id;
    private String lloc_llista;
    private int i;
    private JSONArray llista;
    private LlistaAdapter adapter;
    private List<Llista> llistes;

    public final static String APP_KEY = "iGXpZFRj2XCl9Aixrig80d0rrftOzRef";
    public final static String NOT_CONNECTED_TEXT = "No hi ha connexió de dades. No es pot realitzar l'operació";
    public final static String PROCESSING_TEXT = "Recuperant dades. Esperi...";

    public static boolean loadData = true;
    private NetworkReceiver receiver = new NetworkReceiver();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.subslist, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        llista = new JSONArray();
        Bundle bundle = getActivity().getIntent().getExtras();
        user_id = bundle.getString("User");

        if (checkConnection())
            loadData = true;
        else
            loadData = false;

        if (loadData == true){
            LoadListTask taskload= new LoadListTask();
            taskload.execute();
        }
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

        if (loadData == true){
            LoadListTask taskload= new LoadListTask();
            taskload.execute();
        }
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

    private class LoadListTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute() {
            setRetainInstance(true);
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            ACSClient sdk = new ACSClient(APP_KEY,getActivity().getApplicationContext()); // app key
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
            llistes = new ArrayList<Llista>();

            for (i = 0; i < llista.length(); i++) {
                try {
                    JSONObject aux = llista.getJSONObject(i);
                    String txtidlist = null;
                    txtidlist = aux.getString("id_llista");
                    String txtlistname = null;
                    txtlistname = aux.getString("nom_llista");
                    String txtlistplace = null;
                    txtlistplace = aux.getString("lloc_llista");

                    Llista llista_aux = new Llista(txtidlist,txtlistname,txtlistplace);
                    llistes.add(llista_aux);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            adapter = new LlistaAdapter(getActivity(),R.layout.llista2, llistes);
            adapter.notifyDataSetChanged();

            lv = (ListView)getView().findViewById(R.id.lvSubs);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    Llista aux = llistes.get(position);
                    llista_id = aux.getLlista_id();
                    lloc_llista = aux.getLloc_llista();


                    Intent myIntent = new Intent(getActivity().getApplicationContext(), subslistuser.class);
                    Bundle b = new Bundle();
                    b.putString("Llista", llista_id);
                    b.putString("Usuari", user_id);
                    b.putString("LlocLlista", lloc_llista);

                    myIntent.putExtras(b);
                    startActivity(myIntent);
                }
            });
        }
    }
}