package org.tfc.patxangueitor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.appcelerator.cloud.sdk.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Oscar on 9/02/14.
 */
public class signup extends Activity {

    protected String firstName;
    protected String email;
    protected String password;
    protected String password_confirm;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        View signupButton = findViewById(R.id.button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                firstName = ((EditText) findViewById(R.id.first_name)).getText().toString();
                email = ((EditText) findViewById(R.id.email_address)).getText().toString();
                password = ((EditText) findViewById(R.id.pw)).getText().toString();
                password_confirm = ((EditText) findViewById(R.id.pw_confirm)).getText().toString();

                SignUpTask tasksignup= new SignUpTask();
                tasksignup.execute();
            }
        });
        View cancelButton = findViewById(R.id.button2);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }});
    }

    public Boolean performSignup(String str_nm, String str_em, String str_pass, String str_pass_conf){
        ACSClient sdk = new ACSClient("iGXpZFRj2XCl9Aixrig80d0rrftOzRef");
        Boolean booStatus = false;

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
                booStatus =  true;
            }
        } catch (ACSClientError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return booStatus;
    }

    private class SignUpTask extends AsyncTask<Void, Void, Boolean>
    {
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(signup.this);
            dia.setMessage("Processant. Esperi si us plau.");
            dia.show();
            dia.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            Boolean status;
            status = performSignup(firstName, email, password, password_confirm);
            return status;
        }

        @Override
        protected void onPostExecute(Boolean booResult)
        {
            if (dia.isShowing()) {
                dia.dismiss();
            }
            if (booResult){
                Toast toast1 =
                        Toast.makeText(getApplicationContext(),
                                "Usuari creat correctament", Toast.LENGTH_LONG);
                toast1.show();
                finish();
            }
            else
            {
                ((EditText) findViewById(R.id.first_name)).setText("");
                ((EditText) findViewById(R.id.email_address)).setText("");
                ((EditText) findViewById(R.id.pw)).setText("");
                ((EditText) findViewById(R.id.pw_confirm)).setText("");
                Toast toast2 =
                        Toast.makeText(getApplicationContext(),
                                "Hi hagut un error en la creació de l'usuari. Torna-ho a provar, si us plau", Toast.LENGTH_LONG);
                toast2.show();
            }
        }
    }
}