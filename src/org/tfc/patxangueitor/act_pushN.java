package org.tfc.patxangueitor;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.appcelerator.cloud.push.*;
import com.appcelerator.cloud.sdk.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class act_pushN extends Activity {
    public final static String LOG_TAG = act_pushN.class.getName();
    public final static String APP_KEY = "iGXpZFRj2XCl9Aixrig80d0rrftOzRef";

    private String mDeviceID;
    private Handler activityHandler = new Handler();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pushn);

        final Button registerGCMButton = ((Button) findViewById(R.id.register_gcm_button));
        final Button startpushButton = ((Button) findViewById(R.id.start_push_button));

        startpushButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String type = "android";
                ACSClient sdk = new ACSClient("iGXpZFRj2XCl9Aixrig80d0rrftOzRef",getApplicationContext()); // app key
                Map<String, Object> data = new HashMap<String, Object>();
                //String strDeviceToken = device_.getText().toString();
                data.put("type", type);
                data.put("channel", "Hola");
                data.put("device_token", mDeviceID);
                CCResponse response = null;
                try {
                    response = sdk.sendRequest("push_notification/subscribe.json", CCRequestMethod.POST, data);
                } catch (ACSClientError acsClientError) {
                    acsClientError.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                CCMeta meta = response.getMeta();
                if ("ok".equals(meta.getStatus()) && meta.getCode() == 200 && "SubscribeNotification".equals(meta.getMethod())) {
                    Toast toast1 =
                            Toast.makeText(getApplicationContext(),
                                    "Tot OK", Toast.LENGTH_LONG);
                    toast1.show();
                }
            }
        });

        registerGCMButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PushType pushType = CCPushService.getInstance().getPushType(getApplicationContext());
                try {
                    CCPushService.getInstance().getGCMSenderIdAsnyc(getApplicationContext(), APP_KEY, new GCMSenderIdCallback() {
                        public void receivedGCMSenderId(String senderId) {
                            Log.i(LOG_TAG, "Got SenderId: " + senderId);
                            CCPushService.getInstance().registerGCM(getApplicationContext(), senderId, APP_KEY, new DeviceTokenCallback() {
                                public void receivedDeviceToken(final String deviceToken) {
                                    if (deviceToken == null || deviceToken.length() == 0) {
                                        Log.e(LOG_TAG, "GCM server refused request. Have you configured this app for ACS?");
                                    } else {
                                        activityHandler.post(new Runnable() {
                                            public void run() {
                                                mDeviceID = deviceToken;
                                                ((TextView) findViewById(R.id.device_token_text)).setText(mDeviceID);
                                                ((Button) findViewById(R.id.register_gcm_button)).setEnabled(false);
                                            }
                                        });
                                    }
                                }

                                public void failedReceiveDeviceToken(Throwable ex) {
                                    Log.e(LOG_TAG, ex.getMessage());
                                }
                            });
                        }

                        public void failedReceiveGCMSenderId(Throwable ex) {
                            Log.e(LOG_TAG, ex.getMessage());
                        }
                    });
                } catch (PushServiceException ex) {
                    Log.e(LOG_TAG, ex.getMessage());
                }
                /*
                String type = "android";
                Boolean booResult;
                ACSClient sdk = new ACSClient("QjAbwp3l8AiPMiZJ3rW2RfjBEqKsfqVB",getApplicationContext()); // app key
                String mDeviceID = ((TelephonyManager)getApplicationContext().getSystemService("phone")).getDeviceId();
                if ((mDeviceID == null) || (mDeviceID.equals("")))
                {
                    mDeviceID = "000000000000000";
                }

                Map<String, Object> data = new HashMap<String, Object>();
                data.put("device_token", mDeviceID);
                data.put("channel", "Hola");
                data.put("type", type);
                try {
                    CCResponse response;
                    response = sdk.sendRequest("push_notification/subscribe_token.json", CCRequestMethod.POST, data);
                    CCMeta meta = response.getMeta();
                    if("ok".equals(meta.getStatus())
                            && meta.getCode() == 200
                            && "SubscribeNotificationByToken".equals(meta.getMethod())){
                        booResult = true;
                    }
                } catch (ACSClientError acsClientError) {
                    acsClientError.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                    //   mDeviceID = CCPushService.getInstance().getDeviceTokenLocally(getApplicationContext());
                     //   ((TextView) findViewById(R.id.device_token_text)).setText(mDeviceID);  */
            }
        });
    }
}