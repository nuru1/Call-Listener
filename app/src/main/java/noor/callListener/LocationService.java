package noor.callListener;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class LocationService extends Service {

    private static final int PERMISSION_ALL = 1;
    DatabaseHelper db;

    static String incomingNumber = null;
    int smsStat = 0, smsReady = 0;
    String url = "http://maps.google.com/maps?q=";
    static String SMS;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private String TAG = "Location Service";

    public LocationService() {
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();

        //Log.e("LocationService", "In Oncreate");
        db = new DatabaseHelper(this);

        RequestLocation();
    }


    private void RequestLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.e("F LOCATION", location.getLatitude() + " " + location.getLongitude());
                    setSMS(location);
                } else {
                    Log.e("F LOCATION", "Location Unavailable");
                    showSettingAlert();
                    sendSMSfromDB();
                }
            }
        });

    }


    public void showSettingAlert() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Log.e("loc setting",intent.getAction());
        }




    private void sendSMSfromDB() {
        Locations loc ;
        if(!db.isEmptyLocations()){
            loc = db.GetLocations();
            String msg = "Location disabled by user "+url+loc.lattitude+","+loc.longitude+" Last seen at: "+loc.getTime();
            Log.e("Provider disabled",msg);
            //Toast.makeText(getApplicationContext(),"  Provider disabled   "+msg,Toast.LENGTH_SHORT).show();
            SMS=msg;
            smsReady=1;
            sendSMS();
        }
    }

    void setSMS(Location location){

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());

        String lat = String.valueOf(location.getLatitude());
        String lon = String.valueOf(location.getLongitude());
        Locations locations   = new Locations(1,date,lat,lon);
        db.UpdateLocations(locations);

        Log.e("Current Location:  ", location.toString());
        String sms ="My location "+ url+lat+","+lon+"   Last seen at: "+date;
        Log.e("message: ","  "+sms);
        SMS=sms;
        smsReady=1;
        sendSMS();


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        incomingNumber = intent.getStringExtra("key");
        //Toast.makeText(getApplicationContext(),"num:     "+incomingNumber,Toast.LENGTH_SHORT).show();
        Log.e("Incomming-LCTN_SRVC:",incomingNumber);
        if(smsStat==0 && smsReady==1)
            sendSMS();
        smsStat=0;
        return Service.START_STICKY;
    }

    public void sendSMS(){
        Log.e("sendSMS","no "+incomingNumber);
        Toast.makeText(getApplicationContext(),"sesndSMS "+"no "+incomingNumber,Toast.LENGTH_SHORT).show();
        if(incomingNumber!=null && SMS != null  && smsReady ==1) {
            smsStat=1;
            smsReady=0;
            Toast.makeText(getApplicationContext(),"Sending sms... ",Toast.LENGTH_SHORT).show();
            Log.e("Sending sms:","  "+SMS+"   to: "+incomingNumber);
            SmsManager smsManager = SmsManager.getDefault();
            //msg= "http://maps.google.com/maps?q="+location.getLattitude()+","+location.getLongitude()+"       Last Seen: "+location.getTime();
            smsManager.sendTextMessage(incomingNumber, null, SMS, null, null);
            this.stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        Log.e("OnDestroy","before super.onDestroy()");
        super.onDestroy();
        Log.e("OnDestroy","after super.onDestroy()");
    }

}
