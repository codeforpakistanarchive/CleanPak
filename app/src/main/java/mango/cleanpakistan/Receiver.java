package mango.cleanpakistan;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Receiver extends ParsePushBroadcastReceiver {
    public static final String PARSE_EXTRA_DATA_KEY = "com.parse.Data";
    public static final String PARSE_JSON_ALERT_KEY = "alert";
    public static final String PARSE_JSON_CHANNELS_KEY = "com.parse.Channel";
    private static final String TAG = "ParsePushBroadcastReceiver";

    @Override
    public void onPushOpen(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            JSONObject json =
                    new JSONObject(
                            intent.getExtras()
                                    .getString(PARSE_EXTRA_DATA_KEY));

            Log.d(TAG, "got action " + action);
            Iterator itr = json.keys();
            while (itr.hasNext()) {
                String key = (String) itr.next();
                if(key.equals("objectId")){
                    Intent i = new Intent(context, NotifView.class);
                    i.putExtras(intent.getExtras());
                    i.putExtra("objectId", json.getString(key));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
                Log.d(TAG, "..." + key + " => " + json.getString(key));
            }
        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }

    }
}