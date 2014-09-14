package org.tfc.patxangueitor;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.appcelerator.cloud.sdk.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tfc.adapters.TabsPagerAdapter5;
import org.tfc.adapters.UserAdapter;
import org.tfc.classes.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class subslistevent extends FragmentActivity implements ActionBar.TabListener {
    private ViewPager viewPager;
    private TabsPagerAdapter5 mAdapter;
    private ActionBar actionBar;
    private Boolean booConv = false;
    private String llista_id;
    private String event_id;
    private String user_id;
    private JSONArray llista;
    private String subs_id;
    // Tab titles
    private String[] tabs = { "Dades", "Usuaris" };
    public final static String APP_KEY = "iGXpZFRj2XCl9Aixrig80d0rrftOzRef";
    public final static String NOT_CONNECTED_TEXT = "No hi ha connexió de dades. No es pot realitzar l'operació";
    public final static String ACCEPT_CONV_TEXT = "S'ha acceptat l'event";
    public final static String CANCEL_CONV_TEXT = "S'ha declinat l'event";

    public static boolean loadData = true;
    private NetworkReceiver receiver = new NetworkReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainscreen);

        llista = new JSONArray();
        Bundle bundle = this.getIntent().getExtras();
        llista_id = bundle.getString("Llista");
        event_id = bundle.getString("Event");
        user_id = bundle.getString("Usuari");

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter5(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subslistevent, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accept_conv:
                booConv = true;
                if (loadData){
                    ConvTask convtask= new ConvTask();
                    convtask.execute();
                }
                else
                    Toast.makeText(getApplicationContext(), NOT_CONNECTED_TEXT, Toast.LENGTH_LONG).show();
                return true;
            case R.id.cancel_conv:
                booConv = false;
                if (loadData){
                    ConvTask convtask= new ConvTask();
                    convtask.execute();
                }
                else
                    Toast.makeText(getApplicationContext(),NOT_CONNECTED_TEXT, Toast.LENGTH_LONG).show();;
                return true;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    public class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()){
                loadData = true;
            }
            else
                loadData = false;
        }
    }

    private class ConvTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Void... params)
        {
            return loadEventUser();

        }

        @Override
        protected void onPostExecute(Boolean booresult)
        {

            //List<User> users;
            //users = new ArrayList<User>();
            //int i;
            //for (i = 0; i < llista.length(); i++) {
                try {
                    JSONObject aux = llista.getJSONObject(0);
                           subs_id = aux.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            //}
            ConvTask1 convtask1= new ConvTask1();
            convtask1.execute();
        }
    }

    private class ConvTask1 extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Void... params)
        {
            return updateEvent();
        }

        @Override
        protected void onPostExecute(Boolean booResult)
        {
            if (booResult) {
                Toast.makeText(getApplicationContext(),ACCEPT_CONV_TEXT, Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(getApplicationContext(),CANCEL_CONV_TEXT, Toast.LENGTH_LONG).show();
        }
    }

    public boolean updateEvent(){
        ACSClient sdk = new ACSClient(APP_KEY,getApplicationContext());
        Boolean booStatus = false;
        Map<String, Object> data = new HashMap<String, Object>();

        //data.put("fields", "{\"id_llista\" : \"" + llista_id + "\" , \"id_event\": \"" + event_id + "\", \"id_user\": \"" + txtuserid + "\", \"username\": \"" + txtusername + "\", \"firstname\": \"" + txtfirstname
        //        + "\", \"email\": \"" + txtemail + "\" , \"titular\": \"" + false + "\", \"convocat\": \"" + false + "\"}");

        data.put("id",subs_id);
        data.put("fields","{\"convocat\" : \"" + true + "\"}");
        try {
            CCResponse response = sdk.sendRequest("objects/subscripcio_event/update.json", CCRequestMethod.PUT, data);
            CCMeta meta = response.getMeta();
            if("ok".equals(meta.getStatus())
                    && meta.getCode() == 200
                    && "updateCustomObject".equals(meta.getMethod())){
                booStatus =  true;
            }
        } catch (ACSClientError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return booStatus;
    }

    public boolean loadEventUser(){
        ACSClient sdk = new ACSClient(APP_KEY,getApplicationContext());
        CCResponse response = null;
        Boolean booResult = false;

        Map<String, Object> data = new HashMap<String, Object>();
        //data.put("where", "{\"id_llista\" : \"" + llista_id + "\"}");
        data.put("where", "{\"id_llista\" : \"" + llista_id + "\" , \"id_event\" : \"" + event_id + "\", \"id_user\" : \"" + user_id + "\"}");
        data.put("order", "id_user");

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
                llista = responseJSON.getJSONArray("subscripcio_event");
                booResult = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return booResult;
    }
}