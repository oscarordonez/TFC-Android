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
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.appcelerator.cloud.push.*;
import com.appcelerator.cloud.sdk.*;
import org.json.JSONException;
import org.tfc.adapters.TabsPagerAdapter;
import android.app.ActionBar;

import android.os.Bundle;
import org.tfc.functions.PushNotificationsManager;

import java.io.IOException;

public class mainscreen extends FragmentActivity implements ActionBar.TabListener {
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    private String user_id;

    private String[] tabs = { "Admin. Llistes", "Subscripcions Llistes" };
    public final static String APP_KEY = "iGXpZFRj2XCl9Aixrig80d0rrftOzRef";
    public final static String NOT_CONNECTED_TEXT = "No hi ha connexió de dades. No es pot realitzar l'operació";
    public final static String SESSION_DISCONNECTED_TEXT = "La sessió s'ha desconnectat";
    public final static String SESSION_NOT_DISCONNECTED_TEXT = "No desconnectat. Si us plau, torni-ho a provar";
    public final static String PROCESSING_TEXT = "Desconnectant...";

    public static boolean loadData = true;
    private NetworkReceiver receiver = new NetworkReceiver();

    public final static String LOG_TAG = mainscreen.class.getName();

    private Handler activityHandler = new Handler();
    private String mDeviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mainscreen);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);

        if (loadData){
            GetDeviceIDTask taskgetdeviceid = new GetDeviceIDTask();
            taskgetdeviceid.execute();
        }
        else
            Toast.makeText(getApplicationContext(),NOT_CONNECTED_TEXT, Toast.LENGTH_LONG).show();

        //Get Intent info
        Bundle bundle = this.getIntent().getExtras();
        user_id = bundle.getString("User");

       // Initilization viewpager
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
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
                if (loadData){
                    LogOutTask tasklogout= new LogOutTask();
                    tasklogout.execute();
                }
                else
                    Toast.makeText(getApplicationContext(),NOT_CONNECTED_TEXT, Toast.LENGTH_LONG).show();
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

    private class LogOutTask extends AsyncTask<Void, Void, Boolean>
    {
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(mainscreen.this);
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
                finish();
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

    private class UnSubscribeTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            ACSClient sdk = new ACSClient(APP_KEY,getApplicationContext());
            try {
                PushNotificationsManager.unsubscribePushNotifications(sdk, mDeviceID, "event");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ACSClientError acsClientError) {
                acsClientError.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class GetDeviceIDTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {

            PushType pushType = CCPushService.getInstance().getPushType(getApplicationContext());
            try {
                CCPushService.getInstance().getGCMSenderIdAsnyc(getApplicationContext(), APP_KEY, new GCMSenderIdCallback() {
                    public void receivedGCMSenderId(String senderId) {
                        Log.i(LOG_TAG, "Got SenderId: " + senderId);
                        CCPushService.getInstance().registerGCM(getApplicationContext(), senderId, APP_KEY, new DeviceTokenCallback() {
                            public void receivedDeviceToken(final String deviceToken) {
                                if (deviceToken == null || deviceToken.length() == 0) {
                                    Log.e(LOG_TAG, "GCM server refused request. Have you configured this app for ACS?");
                                } else {
                                    activityHandler.post(new Runnable() {
                                        public void run() {
                                            mDeviceID = deviceToken;
                                        }
                                    });
                                }
                            }

                            public void failedReceiveDeviceToken(Throwable ex) {
                                Log.e(LOG_TAG, ex.getMessage());
                            }
                        });
                    }

                    public void failedReceiveGCMSenderId(Throwable ex) {
                        Log.e(LOG_TAG, ex.getMessage());
                    }
                });
            } catch (PushServiceException ex) {
                Log.e(LOG_TAG, ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            if (loadData){
                SubscribeTask tasksubscribe= new SubscribeTask();
                tasksubscribe.execute();
            }
            else
                Toast.makeText(getApplicationContext(),NOT_CONNECTED_TEXT, Toast.LENGTH_LONG).show();
            //SubscribeTask tasksubscribe= new SubscribeTask();
            //tasksubscribe.execute();
        }
    }

    private class SubscribeTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            ACSClient sdk = new ACSClient(APP_KEY,getApplicationContext());
            try {
                PushNotificationsManager.subscribePushNotifications(sdk, mDeviceID, "event");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ACSClientError acsClientError) {
                acsClientError.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
 }