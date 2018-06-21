package noor.callListener;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class LocationService extends Service {

    DatabaseHelper db;

    static LocationManager locationManager;
    final Looper looper = null;
    private Location mlocation;
    Criteria criteria;
    static String incomingNumber = null;
    int smsStat=0,smsReady=0;
    String url = "http://maps.google.com/maps?q=";
    static String SMS;


    public LocationService() {
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();

        //Log.e("LocationService", "In Oncreate");
        db = new DatabaseHelper(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        smsStat=0;
        smsReady=0;
        if( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            Log.e("Loc Provider Disabled", "Current location Unavailable");
            //Toast.makeText(getApplicationContext(),"  Provider disabled   ",Toast.LENGTH_SHORT).show();
            Locations loc = new Locations();
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
        else{
            Log.e("Loc Provider enabled","requesting location....");
            //Toast.makeText(getApplicationContext(),"Provider enabled requesting location....",Toast.LENGTH_SHORT).show();
            locationManager.requestSingleUpdate(criteria, locationListener, looper);
            }
    }


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mlocation = location;
            //Toast.makeText(getApplicationContext(),"     "+location.getLongitude(),Toast.LENGTH_SHORT).show();

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

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("Status Changed", String.valueOf(status));
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("Provider Enabled", provider);

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e("Provider Disabled", provider);
            //Toast.makeText(getApplicationContext(),"  Provider disabled   ",Toast.LENGTH_SHORT).show();


        }
    };


    @SuppressLint("MissingPermission")
    void location() {
        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/

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
        //Log.e("Incomming: ",incomingNumber);
        if(smsStat==0 && smsReady==1)
            sendSMS();
        smsStat=0;
        return Service.START_STICKY;
    }

    public void sendSMS(){
        Log.e("sesndSMS","no "+incomingNumber);
        //Toast.makeText(getApplicationContext(),"sesndSMS "+"no "+incomingNumber,Toast.LENGTH_SHORT).show();
        if(incomingNumber!=null && SMS != null  && smsReady ==1) {
            smsStat=1;
            smsReady=0;
            //Toast.makeText(getApplicationContext(),"Sending sms... ",Toast.LENGTH_SHORT).show();
            Log.e("Sending sms:","  "+SMS+"   to: "+incomingNumber);
            SmsManager smsManager = SmsManager.getDefault();
            //msg= "http://maps.google.com/maps?q="+location.getLattitude()+","+location.getLongitude()+"       Last Seen: "+location.getTime();
            smsManager.sendTextMessage(incomingNumber, null, SMS, null, null);
            this.stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        //Log.e("OnDestroy","bfore super");
        super.onDestroy();
        //Log.e("OnDestroy","after super");
    }
}
