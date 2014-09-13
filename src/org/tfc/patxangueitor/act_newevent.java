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
import java.util.Map;

public class act_newevent extends Activity {
    private String llista_id;

    public final static String APP_KEY = "iGXpZFRj2XCl9Aixrig80d0rrftOzRef";
    public final static String NOT_CONNECTED_TEXT = "No hi ha connexió de dades. No es pot realitzar l'operació";
    public final static String PROCESSING_TEXT = "Creant event. Esperi si us plau.";
    public final static String EVENT_CREATED_TEXT = "S'ha creat un nou event";
    public final static String EVENT_NOT_CREATED_TEXT = "No s'ha pogut crear la llista. Torna-ho a provar, si us plau";

    public static boolean loadData = true;
    private NetworkReceiver receiver = new NetworkReceiver();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newevent);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);


        Bundle b = this.getIntent().getExtras();
        llista_id = b.getString("Llista");

        View createeventbtn = findViewById(R.id.btn_newevent);
        View canceleventbtn = findViewById(R.id.btn_cancelevent);

        canceleventbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }});

        createeventbtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if (checkConnection())
                    loadData = true;
                else
                    loadData = false;

                if (loadData){
                    NewEventTask eventCreate= new NewEventTask();
                    eventCreate.execute();
                }
                else
                    Toast.makeText(getApplicationContext(),NOT_CONNECTED_TEXT, Toast.LENGTH_SHORT).show();
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

    private class NewEventTask extends AsyncTask<Void, Void, Boolean>
    {
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(act_newevent.this);
            dia.setMessage(PROCESSING_TEXT);
            dia.show();
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            return addEventToList();
        }

        @Override
        protected void onPostExecute(Boolean booResult)
        {
            if (dia.isShowing())
                dia.dismiss();

            if (booResult){
                Toast.makeText(getApplicationContext(),EVENT_CREATED_TEXT, Toast.LENGTH_SHORT).show();
                finish();
            }
            else
                Toast.makeText(getApplicationContext(),EVENT_NOT_CREATED_TEXT, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean addEventToList(){
        ACSClient sdk = new ACSClient(APP_KEY,getApplicationContext()); // app key
        Boolean booStatus = false;

        Map<String, Object> data = new HashMap<String, Object>();

        String eventName = ((EditText) findViewById(R.id.txtEventName)).getText().toString();
        String eventDate = ((EditText) findViewById(R.id.txtEventDate)).getText().toString();

        data.put("fields", "{\"nom\" : \"" + eventName + "\", \"lloc\": \"" + eventDate + "\"}");
        data.put("fields", "{\"id_llista\" : \"" + llista_id + "\", \"id_event\": \"" + eventName + "\", \"data\": \"" + eventDate + "\"}");

        try {
            CCResponse response = sdk.sendRequest("objects/event/create.json", CCRequestMethod.POST, data);
            CCMeta meta = response.getMeta();
            if("ok".equals(meta.getStatus())
                    && meta.getCode() == 200
                    && "createObject".equals(meta.getMethod())) {
               booStatus = true;
            }
        } catch (ACSClientError acsClientError) {
            acsClientError.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return booStatus;
    }
}