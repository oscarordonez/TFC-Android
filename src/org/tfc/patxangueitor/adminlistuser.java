package org.tfc.patxangueitor;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.appcelerator.cloud.sdk.*;
import org.tfc.adapters.TabsPagerAdapter2;
import android.app.ActionBar;
import android.os.Bundle;

import java.io.IOException;

public class adminlistuser extends FragmentActivity implements ActionBar.TabListener {
    private ViewPager viewPager;
    private TabsPagerAdapter2 mAdapter;
    private ActionBar actionBar;

    private String[] tabs = { "Usuaris", "Events" };
    public final static String APP_KEY = "iGXpZFRj2XCl9Aixrig80d0rrftOzRef";
    public final static String NOT_CONNECTED_TEXT = "No hi ha connexió de dades. No es pot realitzar l'operació";
    public final static String SESSION_DISCONNECTED_TEXT = "La sessió s'ha desconnectat";
    public final static String SESSION_NOT_DISCONNECTED_TEXT = "No desconnectat. Si us plau, torni-ho a provar";
    public final static String PROCESSING_TEXT = "Desconnectant...";

    public static boolean loadData = true;
    private NetworkReceiver receiver = new NetworkReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainscreen);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);

        // Initilization  viewpager
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter2(getSupportFragmentManager());
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
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mainscreen, menu);
        return super.onCreateOptionsMenu(menu);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                if (checkConnection())
                    loadData = true;
                else
                    loadData = false;

                if (loadData){
                    LogOutTask tasklogout= new LogOutTask();
                    tasklogout.execute();
                }
                else
                    Toast.makeText(getApplicationContext(), NOT_CONNECTED_TEXT, Toast.LENGTH_LONG).show();
                return true;
        }
        return true;
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

    private class LogOutTask extends AsyncTask<Void, Void, Boolean>
    {
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(adminlistuser.this);
            dia.setMessage(PROCESSING_TEXT);
            dia.show();
            dia.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            return performLogout();
        }

        @Override
        protected void onPostExecute(Boolean booResult)
        {
            if (dia.isShowing()) {
                dia.dismiss();
            }
            if (booResult){
                Toast.makeText(getApplicationContext(),SESSION_DISCONNECTED_TEXT, Toast.LENGTH_LONG).show();
                //finish();
            }
            else
                Toast.makeText(getApplicationContext(),SESSION_NOT_DISCONNECTED_TEXT, Toast.LENGTH_LONG).show();
        }
    }

    public Boolean performLogout(){
        ACSClient sdk = new ACSClient(APP_KEY,getApplicationContext());
        Boolean booResult = false;

        try {
            CCResponse response;
            response = sdk.sendRequest("users/logout.json", CCRequestMethod.GET, null);
            CCMeta meta = response.getMeta();
            if("ok".equals(meta.getStatus())
                    && meta.getCode() == 200
                    && "logoutUser".equals(meta.getMethod())){
                booResult = true;
            }
        }catch (ACSClientError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return booResult;
    }

}