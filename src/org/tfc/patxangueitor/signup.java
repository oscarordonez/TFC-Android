package org.tfc.patxangueitor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.appcelerator.cloud.sdk.*;

import java.io.IOException;
import java.util.HashMap;

public class signup extends Activity {
    protected String firstName = "";
    protected String email = "";
    protected String password = "";
    protected String password_confirm = "";

    public final static String APP_KEY = "iGXpZFRj2XCl9Aixrig80d0rrftOzRef";
    public final static String NOT_CONNECTED_TEXT = "No hi ha connexió de dades. No es pot realitzar l'operació";
    public final static String USER_OK_TEXT = "Usuari creat correctament";
    public final static String USER_NOT_OK_TEXT = "Hi hagut un error en la creació de l'usuari. Torna-ho a provar, si us plau";
    public final static String FILL_FIELDS_TEXT = "És necessari omplir tots els camps";
    public final static String PROCESSING_TEXT = "Processant. Esperi si us plau.";

    public static boolean loadData = true;
    private NetworkReceiver receiver = new NetworkReceiver();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);

        View signupButton = findViewById(R.id.button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                firstName = ((EditText) findViewById(R.id.first_name)).getText().toString();
                email = ((EditText) findViewById(R.id.email_address)).getText().toString();
                password = ((EditText) findViewById(R.id.pw)).getText().toString();
                password_confirm = ((EditText) findViewById(R.id.pw_confirm)).getText().toString();
                if (checkFields()){
                    if (loadData){
                        SignUpTask tasksignup= new SignUpTask();
                        tasksignup.execute();
                    }
                    else
                        Toast.makeText(getApplicationContext(),NOT_CONNECTED_TEXT, Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getApplicationContext(),FILL_FIELDS_TEXT, Toast.LENGTH_LONG).show();
            }
        });

        View cancelButton = findViewById(R.id.button2);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }});
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

    private class SignUpTask extends AsyncTask<Void, Void, Boolean>
    {
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(signup.this);
            dia.setMessage(PROCESSING_TEXT);
            dia.show();
            dia.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            return performSignup(firstName, email, password, password_confirm);
        }

        @Override
        protected void onPostExecute(Boolean booAux)
        {
            if (dia.isShowing()) {
                dia.dismiss();
            }
            if (booAux){
                Toast.makeText(getApplicationContext(),USER_OK_TEXT, Toast.LENGTH_LONG).show();
                //Toast toast1 =
                //        Toast.makeText(getApplicationContext(),
                //                USER_OK, Toast.LENGTH_LONG);
                //toast1.show();
                finish();
            }
            else
            {
                ((EditText) findViewById(R.id.first_name)).setText("");
                ((EditText) findViewById(R.id.email_address)).setText("");
                ((EditText) findViewById(R.id.pw)).setText("");
                ((EditText) findViewById(R.id.pw_confirm)).setText("");
                Toast.makeText(getApplicationContext(),USER_NOT_OK_TEXT, Toast.LENGTH_LONG).show();
                //Toast toast2 =
                //        Toast.makeText(getApplicationContext(),
                //                USER_NOT_OK, Toast.LENGTH_LONG);
                //toast2.show();
            }
        }
    }

    public Boolean performSignup(String str_nm, String str_em, String str_pass, String str_pass_conf){
        ACSClient sdk = new ACSClient(APP_KEY,getApplicationContext());
        Boolean booSignup = false;

        HashMap<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("username", str_nm);
        dataMap.put("email", str_em);
        dataMap.put("password", str_pass);
        dataMap.put("password_confirmation", str_pass_conf);
        dataMap.put("first_name", str_nm);

        try {
            CCResponse response = sdk.sendRequest("users/create.json", CCRequestMethod.POST, dataMap);
            CCMeta meta = response.getMeta();
            if("ok".equals(meta.getStatus())
                    && meta.getCode() == 200
                    && "createUser".equals(meta.getMethod())){
                booSignup =  true;
            }
        } catch (ACSClientError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return booSignup;
    }

    private Boolean checkFields(){
        if (firstName.equals("") || email.equals("") || password.equals("") || password_confirm.equals(""))
            return false;
        else
            return true;
    }
}