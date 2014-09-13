package org.tfc.patxangueitor;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.appcelerator.cloud.sdk.*;
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

    public final static String APP_KEY = "iGXpZFRj2XCl9Aixrig80d0rrftOzRef";
    public final static String NOT_CONNECTED_TEXT = "No hi ha connexió de dades. No es pot realitzar l'operació";
    public final static String PROCESSING_TEXT = "Creant llista. Esperi si us plau.";
    public final static String LIST_CREATED_TEXT = "S'ha creat una nova llista";
    public final static String LIST_NOT_CREATED_TEXT = "No s'ha pogut crear la llista. Torna-ho a provar, si us plau";

    public static boolean loadData = true;
    private NetworkReceiver receiver = new NetworkReceiver();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newlist);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);

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

                if (checkConnection())
                    loadData = true;
                else
                    loadData = false;

                if (loadData){
                    CreateListTask taskcreatelist= new CreateListTask();
                    taskcreatelist.execute();
                }
                else
                    Toast.makeText(getApplicationContext(),NOT_CONNECTED_TEXT, Toast.LENGTH_SHORT).show();
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

    private class CreateListTask extends AsyncTask<Void, Void, Boolean>
    {
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(act_newlist.this);
            dia.setMessage(PROCESSING_TEXT);
            dia.show();
            dia.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            return createlist(listName,listPlace,listDate);
        }

        @Override
        protected void onPostExecute(Boolean booResult)
        {
            if (dia.isShowing())
                dia.dismiss();

            if (booResult){
                Toast.makeText(getApplicationContext(),LIST_CREATED_TEXT, Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                ((EditText) findViewById(R.id.txtnewlist_name)).setText("");
                ((EditText) findViewById(R.id.txtnewlist_place)).setText("");
                ((EditText) findViewById(R.id.txtnewlist_day)).setText("");
                ((EditText) findViewById(R.id.txtnewlist_time)).setText("");
                Toast.makeText(getApplicationContext(),LIST_NOT_CREATED_TEXT, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Boolean createlist(String strlistName, String strlistPlace, String strlistDate){
        ACSClient sdk = new ACSClient(APP_KEY,getApplicationContext()); // app key
        Map<String, Object> data = new HashMap<String, Object>();
        Boolean booListCreated = false;

        data.put("fields", "{\"nom\" : \"" + strlistName + "\", \"lloc\": \"" + strlistPlace + "\" , \"data\": \"" + strlistDate + "\"}");

        try {
            CCResponse response2 = sdk.sendRequest("objects/llista/create.json", CCRequestMethod.POST, data);
            CCMeta meta2 = response2.getMeta();
            if("ok".equals(meta2.getStatus())
                    && meta2.getCode() == 200
                    && "createObject".equals(meta2.getMethod())) {
                booListCreated = true;
            }
            else{
                booListCreated = false;
            }
        } catch (ACSClientError acsClientError) {
            acsClientError.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return booListCreated;
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(et_Date);
        newFragment.show(getSupportFragmentManager(),"datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment(et_Time);
        newFragment.show(getSupportFragmentManager(),"timePicker");
    }
}
