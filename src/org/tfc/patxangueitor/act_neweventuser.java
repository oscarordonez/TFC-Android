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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.appcelerator.cloud.sdk.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tfc.adapters.UserAdapter;
import org.tfc.classes.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class act_neweventuser extends Activity {
    private ListView lv;
    private String llista_id;
    private String event_id;
    private JSONObject auxJSON;
    private int i;
    private JSONArray llista;
    private UserAdapter adapter;

    public final static String APP_KEY = "iGXpZFRj2XCl9Aixrig80d0rrftOzRef";
    public final static String NOT_CONNECTED_TEXT = "No hi ha connexió de dades. No es pot realitzar l'operació";
    public final static String PROCESSING_TEXT = "Carregant usuaris de l'event. Esperi si us plau.";
    public final static String PROCESSING_TEXT2 = "Afegint usuari a l'event... Esperi si us plau";
    public final static String USER_CREATED_TEXT = "S'ha afegit un nou usuari a l'event";
    public final static String USER_NOT_CREATED_TEXT = "No s'ha pogut sfegir l'usuari. Torna-ho a provar, si us plau";

    public static boolean loadData = true;
    private NetworkReceiver receiver = new NetworkReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newuser);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);

        llista = new JSONArray();
        Bundle b = this.getIntent().getExtras();
        llista_id = b.getString("Llista");
        event_id = b.getString("Event");

        if (checkConnection())
            loadData = true;
        else
            loadData = false;

        if (loadData){
            LoadEventUsersTask taskloadallusers = new LoadEventUsersTask();
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

    private class LoadEventUsersTask extends AsyncTask<Void, Void, Boolean>
    {
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(act_neweventuser.this);
            dia.setMessage(PROCESSING_TEXT);
            dia.show();
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            return loadEventUsers();

        }

        @Override
        protected void onPostExecute(Boolean booresult)
        {
            if (dia.isShowing())
                dia.dismiss();

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
                    txtemail = "";
                    //String StrACS_id, String StrUser, String StrFirstName, String StrEmail
                    User user_aux = new User(txtidobj,txtiduser,txtuser,txtfirstname,txtemail);
                    users.add(user_aux);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            adapter = new UserAdapter(act_neweventuser.this,R.layout.user, users);
            adapter.notifyDataSetChanged();

            lv = (ListView)findViewById(R.id.lvNewUsers);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    try {
                        auxJSON = llista.getJSONObject(position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AddUserToEventTask taskaddusertoevent= new AddUserToEventTask();
                    taskaddusertoevent.execute();

                }
            });
        }
    }

    private class AddUserToEventTask extends AsyncTask<Void, Void, Boolean>
    {
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(act_neweventuser.this);
            dia.setMessage(PROCESSING_TEXT2);
            dia.show();
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            return addEventUserToList();
        }

        @Override
        protected void onPostExecute(Boolean booResult)
        {
            if (dia.isShowing())
                dia.dismiss();

            if (booResult) {
                Toast.makeText(getApplicationContext(),USER_CREATED_TEXT, Toast.LENGTH_LONG).show();
                finish();
            }
            else
                Toast.makeText(getApplicationContext(),USER_NOT_CREATED_TEXT, Toast.LENGTH_LONG).show();
        }
    }

    public boolean addEventUserToList(){
        ACSClient sdk = new ACSClient(APP_KEY,getApplicationContext());
        Boolean booStatus = false;
        Map<String, Object> data = new HashMap<String, Object>();

        String txtuserid = null;
        try {
            txtuserid = auxJSON.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String txtusername = null;
        try {
            txtusername = auxJSON.getString("username");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String txtfirstname = null;
        try {
            txtfirstname = auxJSON.getString("first_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String txtemail = null;
        try {
            txtemail = auxJSON.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        data.put("fields", "{\"id_llista\" : \"" + llista_id + "\" , \"id_event\": \"" + event_id + "\", \"id_user\": \"" + txtuserid + "\", \"username\": \"" + txtusername + "\", \"firstname\": \"" + txtfirstname
                + "\", \"email\": \"" + txtemail + "\" , \"titular\": \"" + false + "\", \"convocat\": \"" + false + "\"}");

        try {
            CCResponse response = sdk.sendRequest("objects/subscripcio_event/create.json", CCRequestMethod.POST, data);
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

    public boolean loadEventUsers(){
        ACSClient sdk = new ACSClient(APP_KEY,getApplicationContext());
        CCResponse response = null;
        Boolean booResult = false;

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("where", "{\"id_llista\" : \"" + llista_id + "\"}");
        data.put("order", "id_user");

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
                booResult = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return booResult;
    }
}