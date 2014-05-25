package org.tfc.patxangueitor;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import com.appcelerator.cloud.sdk.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tfc.adapters.TabsPagerAdapter;
import android.app.ActionBar;
import android.os.Bundle;
import org.tfc.patxangueitor.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class mainscreen extends FragmentActivity implements ActionBar.TabListener {
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    private String user_id;
    // Tab titles
    private String[] tabs = { "Admin. Llistes", "Subscripcions Llistes" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainscreen);

        //Recuperamos la información pasada en el intent
        Bundle bundle = this.getIntent().getExtras();
        user_id = bundle.getString("User");

       // Initilization
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
        // Inflate the menu items for use in the action bar
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
                LogOutTask tasklogout= new LogOutTask();
                tasklogout.execute();
                return true;
        }
        return true;
    }

    private class LogOutTask extends AsyncTask<Void, Void, Boolean>
    {
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(mainscreen.this);
            dia.setMessage("Desconnectant...");
            dia.show();
            dia.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            return performlogout();
        }

        @Override
        protected void onPostExecute(Boolean booResult)
        {
            Toast toast_logout;
            if (dia.isShowing()) {
                dia.dismiss();
            }
            if (booResult){
                toast_logout = Toast.makeText(getApplicationContext(),
                        "La sessió s'ha desconnectat", Toast.LENGTH_LONG);
                toast_logout.show();
                finish();
            }
            else
            {
                toast_logout = Toast.makeText(getApplicationContext(),
                        "No desconnectat. Si us plau, torni-ho a provar", Toast.LENGTH_LONG);
                toast_logout.show();
            }
        }
    }

    public boolean performlogout(){
        ACSClient sdk = new ACSClient("iGXpZFRj2XCl9Aixrig80d0rrftOzRef",getApplicationContext());
        Boolean result;
        result = false;

        try {
            CCResponse response;
            response = sdk.sendRequest("users/logout.json", CCRequestMethod.GET, null);
            CCMeta meta = response.getMeta();
            if("ok".equals(meta.getStatus())
                    && meta.getCode() == 200
                    && "logoutUser".equals(meta.getMethod())){
                result = true;
            }
        }catch (ACSClientError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
 }