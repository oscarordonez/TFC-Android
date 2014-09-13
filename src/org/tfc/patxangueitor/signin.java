package org.tfc.patxangueitor;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
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
    protected String txtLogin;
    protected String txtPass;

    public final static String APP_KEY = "iGXpZFRj2XCl9Aixrig80d0rrftOzRef";
    public final static String NOT_CONNECTED_TEST = "No hi ha connexió de dades. No es pot realitzar l'operació";
    public final static String NO_LOGIN = "Usuari / Contrasenya incorrectes";
    public static boolean loadData = true;
    private NetworkReceiver receiver = new NetworkReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);

        View connectbtn = findViewById(R.id.btnOk);
        connectbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                txtLogin = ((EditText) findViewById(R.id.txt_user)).getText().toString();
                txtPass = ((EditText) findViewById(R.id.txt_pass)).getText().toString();
                ((EditText) findViewById(R.id.txt_user)).setText("");
                ((EditText) findViewById(R.id.txt_pass)).setText("");

                if (loadData){
                    SignInTask tasksignin= new SignInTask();
                    tasksignin.execute();
                }
                else
                    Toast.makeText(getApplicationContext(),NOT_CONNECTED_TEST, Toast.LENGTH_LONG).show();
            }
        });

        View cancelbtn = findViewById(R.id.btnCancel);
        cancelbtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){ finish(); }
        });

        View newuserbtn = findViewById(R.id.btnNewUser);
        newuserbtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intentNewUser = new Intent(signin.this,signup.class);
                startActivity(intentNewUser);
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

            if (networkInfo != null && networkInfo.isConnected())
                loadData = true;
            else
                loadData = false;
        }
    }

    private class SignInTask extends AsyncTask<Void, Void, Boolean>
    {
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(signin.this);
            dia.setMessage("Connectant. Esperi si us plau.");
            dia.show();
            dia.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            return performSignin(txtPass,txtLogin);
        }

        @Override
        protected void onPostExecute(Boolean booAux)
        {
            if (dia.isShowing()) {
                dia.dismiss();
            }
            if (booAux){
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
                Toast.makeText(getApplicationContext(),NO_LOGIN, Toast.LENGTH_LONG).show();
            }
        }
    }

    public Boolean performSignin(String strLogin, String strPass){
        ACSClient sdk = new ACSClient(APP_KEY,getApplicationContext());
        Map<String, Object> dataMap = new HashMap<String, Object>();
        Boolean booSignin = false;
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
                    booSignin = true;
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        } catch (ACSClientError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return booSignin;
    }
}
