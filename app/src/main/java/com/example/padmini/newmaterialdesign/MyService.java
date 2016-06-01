package com.example.padmini.newmaterialdesign;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by padmini on 3/24/2016.
 */
public class MyService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();



      /*  SharedPreferences preferences=getSharedPreferences("FirstTimeCheckingService", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        boolean value=true;
        editor.putBoolean("ServiceStarts",value);
        editor.commit();*/


        JokeTask task = new JokeTask();
        task.setService(this,startId);
        task.execute("http://api.icndb.com/jokes/random");


        return START_STICKY;
    }
    class JokeTask extends AsyncTask<String, Void, String> {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        Service service = null;
        int startId;

        private void setService(Service s,int startId) {
            this.service = s;
            this.startId = startId;
        }

        @Override
        protected String doInBackground(String... params) {
            String text = null;
            try {
                URL u = new URL(params[0]);

                conn = (HttpURLConnection) u.openConnection();
                conn.setRequestMethod("GET");

                conn.connect();
                InputStream stream = conn.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String finalJson = buffer.toString();

                Log.e("jsonmsg for Joke", finalJson);

                JSONObject parentObject = new JSONObject(finalJson);
                JSONObject obj = (JSONObject) parentObject.get("value");
                text = (String) obj.get("joke");

                if( text != null) {
                    text = text.replaceAll("&quot;", "\"");
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return text;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Intent intent = new Intent(service, OneItemADay.class);
                   intent.putExtra("Passing Text from ServiceNotification",s);

            TaskStackBuilder stackBuilder1 = TaskStackBuilder.create(service);
            stackBuilder1.addParentStack(OneItemADay.class);
            stackBuilder1.addNextIntent(intent);
            PendingIntent pi = stackBuilder1.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification noti = new Notification.Builder(service)
                    .setContentTitle("Service Notification")
                    .setContentText(s)
                    .setTicker("Joke & Word Notification!!")
                    .setSmallIcon(R.drawable.ic_notifications_white_24dp)
                    .setContentIntent(pi).build();
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            noti.flags |= Notification.FLAG_AUTO_CANCEL;
            nm.notify(0, noti);


            service.stopSelf(startId);

        }
        }

    @Override
    public void onDestroy() {
        //super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
