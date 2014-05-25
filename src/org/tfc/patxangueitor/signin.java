package org.tfc.patxangueitor;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.ProgressBar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        View connectbtn = findViewById(R.id.btnOk);

        connectbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                //booResult = false;
                txtLogin = ((EditText) findViewById(R.id.txt_user)).getText().toString();
                txtPass = ((EditText) findViewById(R.id.txt_pass)).getText().toString();
                //final ProgressDialog dialog = ProgressDialog.show(signin.this, "","Connectant...", true);

                SignInTask tasksignin= new SignInTask();
                tasksignin.execute();
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
            if (booResult){
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
}
