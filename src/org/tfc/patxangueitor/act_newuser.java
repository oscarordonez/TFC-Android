package org.tfc.patxangueitor;

import android.app.Activity;
import android.app.ProgressDialog;
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
    private JSONObject auxJSON;
    private int i;
    private JSONArray users;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newuser);

        users = new JSONArray();

        Bundle b = this.getIntent().getExtras();
        llista_id = b.getString("Llista");

        LoadAllUsersTask taskloadallusers= new LoadAllUsersTask();
        taskloadallusers.execute();
    }

    private class LoadAllUsersTask extends AsyncTask<Void, Void, Boolean>
    {
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(act_newuser.this);
            dia.setMessage("Carregant usuaris");
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
            return loadallusers();

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
                        //llista_id = auxJSON.getString("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    AddUserToListTask taskaddusertolist= new AddUserToListTask();
                    taskaddusertolist.execute();

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
            dia.setMessage("Afegint usuari a la llista...");
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
            return addusertolist();

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
                                "Usuari afegit a la llista", Toast.LENGTH_LONG);
                toast1.show();
                finish();
            }
            else
            {
                Toast toast2 =
                        Toast.makeText(getApplicationContext(),
                                "Hi hagut un error en la creació de l'usuari. Torna-ho a provar, si us plau", Toast.LENGTH_LONG);
                toast2.show();
            }

        }
    }

    public boolean addusertolist(){
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
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        String txtusername = null;
        try {
            txtusername = auxJSON.getString("username");
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        String txtfirstname = null;
        try {
            txtfirstname = auxJSON.getString("first_name");
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        String txtemail = null;
        try {
            txtemail = auxJSON.getString("email");
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        data.put("fields", "{\"id_llista\" : \"" + llista_id + "\", \"id_user\": \"" + txtuserid + "\", \"username\": \"" + txtusername + "\", \"firstname\": \"" + txtfirstname
                + "\", \"email\": \"" + txtemail + "\"}");


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

    public boolean loadallusers(){
        ACSClient sdk = new ACSClient("iGXpZFRj2XCl9Aixrig80d0rrftOzRef",getApplicationContext());
        CCResponse response = null;
        boolean result;
        result = false;

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
                result = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}