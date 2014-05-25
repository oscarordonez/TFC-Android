package org.tfc.adapters;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import org.tfc.classes.User;
import org.tfc.patxangueitor.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.appcelerator.cloud.sdk.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tfc.patxangueitor.act_newuser;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserListFragment extends Fragment {
    private ListView lv;
    private TextView tv_newuser;
    private String llista_id;
    private JSONArray llista;
    /* Test adapter */
    //private ArrayAdapter<String> adapter;
    private UserAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.adminlist, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getActivity().getIntent().getExtras();
        llista_id = bundle.getString("Llista");
        llista = new JSONArray();

        tv_newuser = (TextView)getView().findViewById(R.id.id_NewList);
        tv_newuser.setText("+ Usuari");
        tv_newuser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent_NewUser = new Intent(getActivity().getApplicationContext(), act_newuser.class);
                Bundle b = new Bundle();
                b.putString("Llista", llista_id);
                intent_NewUser.putExtras(b);
                startActivity(intent_NewUser);
            }
        });

        LoadUsersTask taskloadusers= new LoadUsersTask();
        taskloadusers.execute();
    }

    private class LoadUsersTask extends AsyncTask<Void, Void, Void>{
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(getActivity());
            dia.setMessage("Recuperant dades. Esperi...");
            dia.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            ACSClient sdk = new ACSClient("iGXpZFRj2XCl9Aixrig80d0rrftOzRef",getActivity().getApplicationContext()); // app key

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("where", "{\"id_llista\" : \"" + llista_id + "\"}");
            data.put("order", "id_usuari");

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

            /* Test adapter */
            //ArrayList<String> values = new ArrayList<String>();
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
                    String txtiduser = null;
                    txtiduser = aux.getString("id_user");
                    String txtuser = null;
                    txtuser = aux.getString("username");
                    String txtfirstname = null;
                    txtfirstname = aux.getString("firstname");
                    String txtemail = null;
                    txtemail = "";
                    //String StrACS_id, String StrUser, String StrFirstName, String StrEmail
                    User user_aux = new User(txtiduser,txtuser,txtfirstname,txtemail);
                    users.add(user_aux);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            /* Test adapter */
            //adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, values);
            adapter = new UserAdapter(getActivity(),R.layout.user, users);
            adapter.notifyDataSetChanged();

            lv = (ListView)getView().findViewById(R.id.lvAdmin);
            lv.setAdapter(adapter);
        }
    }
}