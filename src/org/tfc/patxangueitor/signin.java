package org.tfc.patxangueitor;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import com.appcelerator.cloud.sdk.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class signin extends Activity {
    private String user_id;
    protected boolean booResult;
    protected String txtLogin;
    protected String txtPass;

    public static boolean loadData = true;

    private NetworkReceiver receiver = new NetworkReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signin);

        // Register BroadcastReceiver to track connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);

        View connectbtn = findViewById(R.id.btnOk);

        connectbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                //booResult = false;
                txtLogin = ((EditText) findViewById(R.id.txt_user)).getText().toString();
                txtPass = ((EditText) findViewById(R.id.txt_pass)).getText().toString();
                ((EditText) findViewById(R.id.txt_user)).setText("");
                ((EditText) findViewById(R.id.txt_pass)).setText("");
                //final ProgressDialog dialog = ProgressDialog.show(signin.this, "","Connectant...", true);

                if (loadData == true){
                    SignInTask tasksignin= new SignInTask();
                    tasksignin.execute();
                }
                else
                    Toast.makeText(getApplicationContext(), "no tens internete", Toast.LENGTH_SHORT).show();
            }
        });

        View cancelbtn = findViewById(R.id.btnCancel);
        cancelbtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){ finish(); }
        });

        View newuserbtn = findViewById(R.id.btnNewUser);
        newuserbtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent3 = new Intent(signin.this,signup.class);
                startActivity(intent3);
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

    public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            // Check if network connection is available
            if (networkInfo != null && (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE || networkInfo.getType() == ConnectivityManager.TYPE_WIFI)){
                //Toast.makeText(context, networkInfo.getType(), Toast.LENGTH_SHORT).show();
                loadData = true;
            }
            else
                loadData = false;

            }
    }

    public void performsignin(String strLogin, String strPass){
        ACSClient sdk = new ACSClient("iGXpZFRj2XCl9Aixrig80d0rrftOzRef",getApplicationContext());
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("login", strLogin);
        dataMap.put("password", strPass);

        try {
            CCResponse response;
            response = sdk.sendRequest("users/login.json", CCRequestMethod.POST, dataMap);
            CCMeta meta = response.getMeta();
            if("ok".equals(meta.getStatus())
                    && meta.getCode() == 200
                    && "loginUser".equals(meta.getMethod())){
                try{
                    JSONObject json = response.getResponseData();
                    JSONArray users = json.getJSONArray("users");
                    JSONObject aux = users.getJSONObject(0);

                    user_id = aux.getString("id");
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
                booResult = true;
                }
            else{
                booResult = false;
            }
        } catch (ACSClientError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class SignInTask extends AsyncTask<Void, Void, Void>
    {
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            lockScreenOrientation();
            dia = new ProgressDialog(signin.this);
            dia.setMessage("Connectant. Esperi si us plau.");
            dia.show();
            dia.setCancelable(false);


        }

        @Override
        protected Void doInBackground(Void... params)
        {
            performsignin(txtPass,txtLogin);
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            if (dia.isShowing()) {
                dia.dismiss();
            }
            unlockScreenOrientation();
            if (booResult){
                //if (receiver != null) {
                //    signin.this.unregisterReceiver(receiver);
                //}
                Intent intent = new Intent(signin.this,mainscreen.class);
                Bundle b = new Bundle();
                b.putString("Status","Connected");
                b.putString("User", user_id);
                intent.putExtras(b);
                startActivity(intent);
            }
            else
            {
                ((EditText) findViewById(R.id.txt_user)).setText("");
                ((EditText) findViewById(R.id.txt_pass)).setText("");
                Toast toast1 =
                        Toast.makeText(getApplicationContext(),
                                "Usuari / Contrasenya incorrectes", Toast.LENGTH_LONG);
                toast1.show();
            }
        }
    }
    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void unlockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }
}
