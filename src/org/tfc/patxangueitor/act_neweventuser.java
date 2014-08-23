package org.tfc.patxangueitor;

import android.app.Activity;
import android.app.ProgressDialog;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newuser);

        llista = new JSONArray();

        Bundle b = this.getIntent().getExtras();
        llista_id = b.getString("Llista");
        event_id = b.getString("Event");

        LoadEventUsersTask taskloadallusers = new LoadEventUsersTask();
        taskloadallusers.execute();
    }

    private class LoadEventUsersTask extends AsyncTask<Void, Void, Boolean>
    {
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(act_neweventuser.this);
            dia.setMessage("Carregant usuaris event");
            dia.show();
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            /*ACSClient sdk = new ACSClient("iGXpZFRj2XCl9Aixrig80d0rrftOzRef",getApplicationContext());
            CCResponse response = null;

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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;*/
            return loadeventusers();

        }

        @Override
        protected void onPostExecute(Boolean booresult)
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
                    /* Test adapter*/
                    //values.add(i, aux.getString("id_usuari"));
                    //JSONObject userdata = aux.getJSONObject("user");
                    //String StrACS_id, String StrUser, String StrFirstName, String StrEmail
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

            /* Test adapter */
            //adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, values);
            adapter = new UserAdapter(act_neweventuser.this,R.layout.user, users);
            adapter.notifyDataSetChanged();

            lv = (ListView)findViewById(R.id.lvNewUsers);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    try {
                        auxJSON = llista.getJSONObject(position);
                        //llista_id = auxJSON.getString("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AddUserToEventTask taskaddusertoevent= new AddUserToEventTask();
                    taskaddusertoevent.execute();

                }
            });

            /*ArrayList<String> values = new ArrayList<String>();
            for (i = 0; i < users.length(); i++) {
                try {
                    auxJSON = users.getJSONObject(i);
                    values.add(i, auxJSON.getString("username"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            adapter = new ArrayAdapter<String>(act_neweventuser.this,android.R.layout.simple_list_item_1, values);
            adapter.notifyDataSetChanged();

            lv = (ListView)findViewById(R.id.lvNewUsers);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    try {
                        auxJSON = users.getJSONObject(position);
                        //llista_id = auxJSON.getString("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AddUserToListTask taskaddusertolist= new AddUserToListTask();
                    taskaddusertolist.execute();

                }
            });*/
        }
    }

    private class AddUserToEventTask extends AsyncTask<Void, Void, Boolean>
    {
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(act_neweventuser.this);
            dia.setMessage("Afegint usuari a l'event...");
            dia.show();
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            /*ACSClient sdk = new ACSClient("iGXpZFRj2XCl9Aixrig80d0rrftOzRef",getApplicationContext());
            CCResponse response = null;

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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;*/
            return addeventusertolist();

        }

        @Override
        protected void onPostExecute(Boolean booResult)
        {
            if (dia.isShowing()) {
                dia.dismiss();
            }
            if (booResult){
                Toast toast1 =
                        Toast.makeText(getApplicationContext(),
                                "Usuari afegit a l'event", Toast.LENGTH_LONG);
                toast1.show();
                finish();
            }
            else
            {
                Toast toast2 =
                        Toast.makeText(getApplicationContext(),
                                "Hi hagut un error a l'afegir l'usuari. Torna-ho a provar, si us plau", Toast.LENGTH_LONG);
                toast2.show();
            }

        }
    }

    public boolean addeventusertolist(){
        ACSClient sdk = new ACSClient("iGXpZFRj2XCl9Aixrig80d0rrftOzRef",getApplicationContext());
        Boolean booStatus = false;

        //HashMap<String, Object> dataMap = new HashMap<String, Object>();
        //dataMap.put("id_llista", "533df8de891fdf43ba086f5d");
        //dataMap.put("id_usuari", "oscarino");


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
                + "\", \"email\": \"" + txtemail + "\"}");


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

    public boolean loadeventusers(){
        ACSClient sdk = new ACSClient("iGXpZFRj2XCl9Aixrig80d0rrftOzRef",getApplicationContext());
        CCResponse response = null;
        boolean result;
        result = false;

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
                result = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}