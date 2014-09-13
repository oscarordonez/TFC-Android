package org.tfc.patxangueitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ArrivalActivity extends Activity {
    private String message;
    private String aux;
    JSONObject json;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arrival);

        Intent intent = getIntent();
        message = intent.getExtras().getString("payload");

        try{
            JSONObject json = new JSONObject(message);
            JSONArray msg = json.getJSONArray("android");
            JSONObject auxObj = msg.getJSONObject(0);

            aux = auxObj.getString("alert");
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        ((TextView) findViewById(R.id.message_text)).setText(aux);

        final Button gobackButton = ((Button) findViewById(R.id.goback_button));
        gobackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), signin.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}