package org.tfc.patxangueitor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.appcelerator.cloud.sdk.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tfc.fragments.DatePickerFragment;
import org.tfc.fragments.TimePickerFragment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class act_newlist extends FragmentActivity {
    TextView et_Date;
    TextView et_Time;
    String listName;
    String listPlace;
    String listDate;
    protected boolean booResult;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newlist);

        View createlistbtn = findViewById(R.id.btnnewlist_ok);
        View cancellistbtn = findViewById(R.id.btnnewlist_cancel);

        et_Date = (TextView) findViewById(R.id.txtnewlist_date);
        et_Time = (TextView) findViewById(R.id.txtnewlist_time);


        cancellistbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }});

        createlistbtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                listName = ((EditText) findViewById(R.id.txtnewlist_name)).getText().toString();
                listDate = ((TextView) findViewById(R.id.txtnewlist_date)).getText().toString();
                listPlace = ((EditText) findViewById(R.id.txtnewlist_place)).getText().toString();

                CreateListTask taskcreatelist= new CreateListTask();
                taskcreatelist.execute();
            }
        });
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(et_Date);
        newFragment.show(getSupportFragmentManager(),"datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment(et_Time);
        newFragment.show(getSupportFragmentManager(),"timePicker");
    }
    private class CreateListTask extends AsyncTask<Void, Void, Void>
    {
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(act_newlist.this);
            dia.setMessage("Creant llista...");
            dia.show();
            dia.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            createlist(listName,listPlace,listDate);
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            if (dia.isShowing()) {
                dia.dismiss();
            }
            if (booResult){

            }
            else
            {
                ((EditText) findViewById(R.id.txtnewlist_name)).setText("");
                ((EditText) findViewById(R.id.txtnewlist_place)).setText("");
                ((EditText) findViewById(R.id.txtnewlist_day)).setText("");
                ((EditText) findViewById(R.id.txtnewlist_time)).setText("");
                Toast toast1 =
                        Toast.makeText(getApplicationContext(),
                                "No s'ha pogut crear la llista", Toast.LENGTH_LONG);
                toast1.show();
            }
        }
    }
    public void createlist(String strlistName, String strlistPlace, String strlistDate){


        ACSClient sdk = new ACSClient("iGXpZFRj2XCl9Aixrig80d0rrftOzRef",getApplicationContext()); // app key
        Map<String, Object> data = new HashMap<String, Object>();

        //data.put("fields", "{\"nom\" : \"" + listName + "\", \"lloc\": \"" + listPlace + "\"}");
        data.put("fields", "{\"nom\" : \"" + strlistName + "\", \"lloc\": \"" + strlistPlace + "\" , \"data\": \"" + strlistDate + "\"}");

        try {
            CCResponse response2 = sdk.sendRequest("objects/llista/create.json", CCRequestMethod.POST, data);
            CCMeta meta2 = response2.getMeta();
            if("ok".equals(meta2.getStatus())
                    && meta2.getCode() == 200
                    && "createObject".equals(meta2.getMethod())) {
                booResult = true;
            }
            else{
                booResult = false;
            }
        } catch (ACSClientError acsClientError) {
            acsClientError.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
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
        }*/
    }
}