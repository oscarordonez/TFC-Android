package org.tfc.patxangueitor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.appcelerator.cloud.sdk.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class act_newuser extends Activity {
    private ListView lv;
    private String llista_id;
    private String nom_llista;
    private String lloc_llista;
    private JSONObject auxJSON;
    private int i;
    private JSONArray users;
    private ArrayAdapter<String> adapter;

    public final static String APP_KEY = "iGXpZFRj2XCl9Aixrig80d0rrftOzRef";
    public final static String NOT_CONNECTED_TEXT = "No hi ha connexió de dades. No es pot realitzar l'operació";
    public final static String USER_ADDED_TEXT = "Usuari afegit a la llista";
    public final static String USER_NOT_ADDED_TEXT = "Hi hagut un error a l'afegir l'usuari. Torna-ho a provar, si us plau";
    public final static String PROCESSING_TEXT = "Carregant usuaris. Esperi si us plau.";
    public final static String PROCESSING_TEXT2 = "Afegint usuari a la llista... Esperi si us plau.";

    public static boolean loadData = true;
    private NetworkReceiver receiver = new NetworkReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newuser);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);

        users = new JSONArray();

        Bundle b = this.getIntent().getExtras();
        llista_id = b.getString("Llista");
        nom_llista = b.getString("NomLlista");
        lloc_llista = b.getString("LlocLlista");

        if (checkConnection())
            loadData = true;
        else
            loadData = false;

        if (loadData){
            LoadAllUsersTask taskloadallusers= new LoadAllUsersTask();
            taskloadallusers.execute();
        }
        else
            Toast.makeText(getApplicationContext(),NOT_CONNECTED_TEXT, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            this.unregisterReceiver(receiver);
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
        ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected())
            booLoad = true;
        else
            booLoad = false;

        return booLoad;
    }

    private class LoadAllUsersTask extends AsyncTask<Void, Void, Boolean>
    {
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(act_newuser.this);
            dia.setMessage(PROCESSING_TEXT);
            dia.show();
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            return loadAllUsers();
        }

        @Override
        protected void onPostExecute(Boolean booresult)
        {
            if (dia.isShowing()) {
                dia.dismiss();
            }

            ArrayList<String> values = new ArrayList<String>();
            for (i = 0; i < users.length(); i++) {
                try {
                    auxJSON = users.getJSONObject(i);
                    values.add(i, auxJSON.getString("username"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            adapter = new ArrayAdapter<String>(act_newuser.this,android.R.layout.simple_list_item_1, values);
            adapter.notifyDataSetChanged();

            lv = (ListView)findViewById(R.id.lvNewUsers);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    try {
                        auxJSON = users.getJSONObject(position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (loadData){
                        AddUserToListTask taskaddusertolist= new AddUserToListTask();
                        taskaddusertolist.execute();
                    }
                    else
                        Toast.makeText(getApplicationContext(),NOT_CONNECTED_TEXT, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private class AddUserToListTask extends AsyncTask<Void, Void, Boolean>
    {
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(act_newuser.this);
            dia.setMessage(PROCESSING_TEXT2);
            dia.show();
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            return addUserToList();
        }

        @Override
        protected void onPostExecute(Boolean booResult)
        {
            if (dia.isShowing()) {
                dia.dismiss();
            }

            if (booResult){
                Toast.makeText(getApplicationContext(),USER_ADDED_TEXT, Toast.LENGTH_LONG).show();
                finish();
            }
            else

                Toast.makeText(getApplicationContext(),USER_NOT_ADDED_TEXT, Toast.LENGTH_LONG).show();
        }
    }

    public Boolean addUserToList(){
        ACSClient sdk = new ACSClient(APP_KEY,getApplicationContext());
        Boolean booStatus = false;

        Map<String, Object> data = new HashMap<String, Object>();

        String txtuserid = "";
        try {
            txtuserid = auxJSON.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String txtusername = "";
        try {
            txtusername = auxJSON.getString("username");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String txtfirstname = "";
        try {
            txtfirstname = auxJSON.getString("first_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String txtemail = "";
        try {
            txtemail = auxJSON.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //data.put("fields", "{\"id_llista\" : \"" + llista_id + "\", \"nom_llista\": \"" + nom_llista + "\", \"id_user\": \"" + txtuserid + "\", \"username\": \"" + txtusername + "\", \"firstname\": \"" + txtfirstname
        //        + "\", \"email\": \"" + txtemail + "\"}");
        data.put("fields", "{\"id_llista\" : \"" + llista_id + "\", \"nom_llista\": \"" + nom_llista + "\" , \"id_user\": \"" + txtuserid + "\" , \"titular_llista\": \"" + true + "\", \"lloc_llista\": \"" + lloc_llista
                  + "\", \"username\": \"" + txtusername + "\", \"firstname\": \"" + txtfirstname + "\", \"email\": \"" + txtemail + "\"}");
        try {
            CCResponse response = sdk.sendRequest("objects/subscripcio_usuari/create.json", CCRequestMethod.POST, data);
            CCMeta meta = response.getMeta();
            if("ok".equals(meta.getStatus())
                    && meta.getCode() == 200
                    && "createObject".equals(meta.getMethod())){
                booStatus =  true;
            }
        } catch (ACSClientError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return booStatus;
    }

    public boolean loadAllUsers(){
        ACSClient sdk = new ACSClient(APP_KEY,getApplicationContext());
        CCResponse response = null;
        boolean booResult = false;

        try {
            response = sdk.sendRequest("users/search.json", CCRequestMethod.GET, null);
        } catch (ACSClientError acsClientError) {
            acsClientError.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject responseJSON = response.getResponseData();
        CCMeta meta = response.getMeta();
        if("ok".equals(meta.getStatus())
                && meta.getCode() == 200
                && "searchUsers".equals(meta.getMethod())) {
            try {
                users = responseJSON.getJSONArray("users");
                booResult = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return booResult;
    }
}