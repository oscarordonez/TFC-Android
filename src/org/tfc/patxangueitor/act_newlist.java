package org.tfc.patxangueitor;

import android.app.Activity;
import android.content.Intent;
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
import java.util.Map;

public class act_newlist extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newlist);

        View createlistbtn = findViewById(R.id.btnnewlist_ok);
        View cancellistbtn = findViewById(R.id.btnnewlist_cancel);

        cancellistbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }});

        createlistbtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //fnCreateNewList()
                ACSClient sdk = new ACSClient("iGXpZFRj2XCl9Aixrig80d0rrftOzRef",getApplicationContext()); // app key
                Map<String, Object> data = new HashMap<String, Object>();

                String listName = ((EditText) findViewById(R.id.txtnewlist_name)).getText().toString();
                String listDay = ((EditText) findViewById(R.id.txtnewlist_day)).getText().toString();
                String listPlace = ((EditText) findViewById(R.id.txtnewlist_place)).getText().toString();

                //data.put("fields", "{\"nom\" : \"prova2\", \"lloc\": \"alla\"}");
                data.put("fields", "{\"nom\" : \"" + listName + "\", \"lloc\": \"" + listPlace + "\"}");

                try {
                    CCResponse response2 = sdk.sendRequest("objects/llista/create.json", CCRequestMethod.POST, data);

                    JSONObject responseJSON = response2.getResponseData();
                    CCMeta meta2 = response2.getMeta();
                    if("ok".equals(meta2.getStatus())
                            && meta2.getCode() == 200
                            && "createObject".equals(meta2.getMethod())) {
                        JSONArray newlist = responseJSON.getJSONArray("llista");
                        Toast toast1 =
                                Toast.makeText(getApplicationContext(),
                                        "Llista creada correctament", Toast.LENGTH_LONG);
                        toast1.show();
                        //StartActivity --> recrear activitat
                        finish();
                    }
                } catch (ACSClientError acsClientError) {
                    acsClientError.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (JSONException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
    }
}