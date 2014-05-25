package org.tfc.adapters;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.appcelerator.cloud.sdk.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tfc.classes.User;
import org.tfc.patxangueitor.R;
import org.tfc.patxangueitor.act_neweventuser;
import org.tfc.patxangueitor.act_newuser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventUserListFragment extends Fragment {
    private ListView lv;
    private TextView tv_neweventuser;
    private String llista_id;
    private String event_id;
    private JSONArray llista;
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
        event_id = bundle.getString("Event");
        llista = new JSONArray();

        tv_neweventuser = (TextView)getView().findViewById(R.id.id_NewList);
        tv_neweventuser.setText("+ Usuari event");
        tv_neweventuser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent_NewEventUser = new Intent(getActivity().getApplicationContext(), act_neweventuser.class);
                Bundle b = new Bundle();
                b.putString("Llista", llista_id);
                b.putString("Event", event_id);
                intent_NewEventUser.putExtras(b);
                startActivity(intent_NewEventUser);
            }
        });

        LoadEventUsersTask taskloadeventusers= new LoadEventUsersTask();
        taskloadeventusers.execute();
    }

    private class LoadEventUsersTask extends AsyncTask<Void, Void, Void>{
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(getActivity());
            dia.setMessage("Recuperant usuaris event. Esperi...");
            dia.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            ACSClient sdk = new ACSClient("iGXpZFRj2XCl9Aixrig80d0rrftOzRef",getActivity().getApplicationContext()); // app key

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("where", "{\"id\" : \"" + event_id + "\"}");
            data.put("order", "id_usuari");

            CCResponse response = null;
            try {
                response = sdk.sendRequest("objects/subscripcio_event/query.json", CCRequestMethod.GET, data);
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
                    llista = responseJSON.getJSONArray("subscripcio_usuari_event");
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