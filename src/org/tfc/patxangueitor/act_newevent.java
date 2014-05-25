package org.tfc.patxangueitor;

import android.app.Activity;
import android.app.ProgressDialog;
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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newevent);

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
                NewEventTask eventCreate= new NewEventTask();
                eventCreate.execute();
            }});
    }

    private class NewEventTask extends AsyncTask<Void, Void, Boolean>
    {
        private ProgressDialog dia;

        @Override
        protected void onPreExecute() {
            dia = new ProgressDialog(act_newevent.this);
            dia.setMessage("Creant nou event...");
            dia.show();
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            return addeventtolist();
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
                                "Event afegit a la llista", Toast.LENGTH_LONG);
                toast1.show();
                finish();
            }
            else
            {
                Toast toast2 =
                        Toast.makeText(getApplicationContext(),
                                "Hi hagut un error en la creació de l'event. Torna-ho a provar, si us plau", Toast.LENGTH_LONG);
                toast2.show();
            }

        }
    }

    public boolean addeventtolist(){
        ACSClient sdk = new ACSClient("iGXpZFRj2XCl9Aixrig80d0rrftOzRef",getApplicationContext()); // app key
        boolean booStatus = false;

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