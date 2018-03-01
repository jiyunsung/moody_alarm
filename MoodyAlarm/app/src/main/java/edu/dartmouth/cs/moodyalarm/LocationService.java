package edu.dartmouth.cs.moodyalarm;

/**
 * Created by vivianjiang on 2/6/18.
 */

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;




public class LocationService extends Service {
    private NotificationManager mNotificationManager;
    private LocationManager locationManager;

    private static boolean isRunning = false;
    public static final String CHANNEL_ID = "notification channel";

    private List<Messenger> mClients = new ArrayList<Messenger>(); // Keeps
    // track of
    // all
    // current
    // registered
    // clients.
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_LOCATION = 3;
    public static final int MSG_REQUEST_PERMISSIONS = 4;
    private static final String TAG = "vj";


    private final Messenger mMessenger = new Messenger(new IncomingMessageHandler()); // Target we publish for clients to

    private class IncomingMessageHandler extends Handler {


        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    Log.d(TAG, "S: RX MSG_REGISTER_CLIENT:mClients.add(msg.replyTo) ");
                    mClients.add(msg.replyTo);
                    sendLocation();
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };



    private void updateWithNewLocation(Location location) {


        if (location != null) {
            //Log.d("updatewithnew loc: ", "location not null");
            sendMessageToMap(location);
        }


    }



    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "S:onBind() - return mMessenger.getBinder()");

        return mMessenger.getBinder();
    }



    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "S:onCreate(): Service Started.");

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "S:onStartCommand(): Received start id " + startId + ": " + intent);

        return START_STICKY; // Run until explicitly stopped.
    }



    private void sendMessageToMap(Location location) {
        Log.d(TAG, "S:sendMessageToMap");
        Iterator<Messenger> messengerIterator = mClients.iterator();
        while (messengerIterator.hasNext()) {
            Messenger messenger = messengerIterator.next();
            try {

                Bundle bundle = new Bundle();
                bundle.putParcelable("location", location);
                Message msg_entry = Message.obtain(null, MSG_LOCATION);
                msg_entry.setData(bundle);
                Log.d(TAG, "S:TX MSG_SET_LOCATION");
                messenger.send(msg_entry);

            } catch (RemoteException e) {
                // The client is dead. Remove it from the list.
                mClients.remove(messenger);
            }
        }
    }

    public static boolean isRunning() {
        return isRunning;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "S:onDestroy():Service Stopped");
        super.onDestroy();


        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        isRunning = false;

    }


    private void sendLocation() {

        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        String svcName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(svcName);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        String provider = locationManager.getBestProvider(criteria, true);


        Log.d("sendLocation", "in send location");
        Location l = getLastKnownLocation();
        if (l != null){
            updateWithNewLocation(l);
        } else{
            Log.d("sendLocation: ", "location is null");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Iterator<Messenger> messengerIterator = mClients.iterator();
            while (messengerIterator.hasNext()) {
                Messenger messenger = messengerIterator.next();
                try {
                    Message msg_int = Message.obtain(null, MSG_REQUEST_PERMISSIONS, 1);
                    messenger.send(msg_int);
                } catch (RemoteException e) {
                    // The client is dead. Remove it from the list.
                    mClients.remove(messenger);
                }
            }
        }

        locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);
    }

    private Location getLastKnownLocation() {
        LocationManager locationManager;
        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Iterator<Messenger> messengerIterator = mClients.iterator();
            while (messengerIterator.hasNext()) {
                Messenger messenger = messengerIterator.next();
                try {
                    Message msg_int = Message.obtain(null, MSG_REQUEST_PERMISSIONS, 1);
                    messenger.send(msg_int);
                } catch (RemoteException e) {
                    // The client is dead. Remove it from the list.
                    mClients.remove(messenger);
                }
            }
        }
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

}