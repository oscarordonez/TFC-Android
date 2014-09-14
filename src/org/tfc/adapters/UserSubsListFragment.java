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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.appcelerator.cloud.sdk.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tfc.classes.User;
import org.tfc.patxangueitor.R;
import org.tfc.patxangueitor.act_newuser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserSubsListFragment extends Fragment {
    private ListView lv;
    private String llista_id;
    private String nom_llista;
    private JSONArray llista;
    private UserAdapter adapter;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getActivity().getIntent().getExtras();
        llista_id = bundle.getString("Llista");
        nom_llista = bundle.getString("NomLlista");
        llista = new JSONArray();

        if (checkConnection())
            loadData = true;
        else
            loadData = false;

        if (loadData){
            LoadUsersTask taskloadusers= new LoadUsersTask();
            taskloadusers.execute();
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
            LoadUsersTask taskloadusers= new LoadUsersTask();
            taskloadusers.execute();
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

    private class LoadUsersTask extends AsyncTask<Void, Void, Void>{
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
            ACSClient sdk = new ACSClient(APP_KEY,getActivity().getApplicationContext()); // app key

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("where", "{\"id_llista\" : \"" + llista_id + "\"}");
            data.put("order", "username");

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
            if (dia.isShowing()) {
                dia.dismiss();
            }

            List<User> users;
            users = new ArrayList<User>();

            int i;
            for (i = 0; i < llista.length(); i++) {
                try {
                    JSONObject aux = llista.getJSONObject(i);
                    String txtidobj = null;
                    txtidobj = aux.getString("id");
                    String txtiduser = null;
                    txtiduser = aux.getString("id_user");
                    String txtuser = null;
                    txtuser = aux.getString("username");
                    String txtfirstname = null;
                    txtfirstname = aux.getString("firstname");
                    String txtemail = null;
                    txtemail = aux.getString("email");
                    //txtemail = "";
                    //String StrACS_id, String StrUser, String StrFirstName, String StrEmail
                    User user_aux = new User(txtidobj,txtiduser,txtuser,txtfirstname,txtemail);
                    users.add(user_aux);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            adapter = new UserAdapter(getActivity(),R.layout.user, users);
            adapter.notifyDataSetChanged();

            lv = (ListView)getView().findViewById(R.id.lvSubs);
            lv.setAdapter(adapter);
        }
    }
}