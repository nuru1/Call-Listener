package noor.callListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by asif on 25-Feb-18.
 */

public class PhoneStateReceiver extends BroadcastReceiver {

    static String[] numbers = new String[3];
    DatabaseHelper db;
    Context context;

    static String num=null;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;


        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
//                Toast.makeText(context,"Incoming Call",Toast.LENGTH_SHORT).show();
                //Toast.makeText(context,"Ringing State Number is -"+incomingNumber,Toast.LENGTH_SHORT).show();
                Log.e("Incomming","Ringing State Number is "+incomingNumber);
                if(check(incomingNumber)){
                    num=incomingNumber;
                    Intent i = new Intent(context,LocationService.class);
                    i.putExtra("key", incomingNumber);
                    context.startService(i);
                    //Log.e("phnStatRcvr","after location service call");
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private boolean check(String incommingNumber) {
        //Toast.makeText(context,"in check- -",Toast.LENGTH_SHORT).show();
        db = new DatabaseHelper(context);

        ArrayList<String> numbers = db.GetPhnNumbers();

        for (int i=0;i<numbers.size();i++){
            //Toast.makeText(context,"NUMBER FROM DB  "+numbers.get(i)+"  --" + incommingNumber,Toast.LENGTH_SHORT).show();
            //Log.e("Number checking","NUMBER FROM DB  "+numbers.get(i)+"  --" + incommingNumber);
            if(PhoneNumberUtils.compare(numbers.get(i),incommingNumber)) {
                //Toast.makeText(context,"number Matched!!",Toast.LENGTH_SHORT).show();
                Log.e("Number checking","number Matched!!");
                return true;
            }
        }
        Log.e("Number checking","No number Matched!!");
        return false;
    }



}
