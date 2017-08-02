package BluetoothConnection;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.everysight.utilities.EvsTimer;
import com.google.gson.Gson;

import LOS.Los;
import everysight.Activity.DirectionsActivity;
import everysight.Activity.R;

/**
 * Created by t-aryehe on 7/2/2017.
 */

public class BluetoothHandler extends Handler {

    private Los mLos;
    private float mYaw;
    private EvsTimer mUpdateYAWTimer = null;
    private EvsTimer mUpdateDirTimer = null;
    private static final int DELAY_FROM_NEW_MSG = 25;
    private static final int YAW_TIMER_PERIOD = 15;
    private static final int DIR_TIMER_PERIOD = 20;
    private static final int BIAS_AZIMUTH = 25;
    private static final int TURN_BIAS_AZIMUTH = 45;
    private static final int MIN_DIST_BEFORE_TURN = 30;


    private ImageView compass;
    private ImageView im;
    private TextView tv_dist;
    private TextView tv_Az;
    private boolean gotFirstMsg = false;

    private DirectionsActivity mCaller;
    private Context mContext;
    private String oldMsg;

    public BluetoothHandler(DirectionsActivity caller, Context context)
    {
        mCaller = caller;
        mContext = context;
        InitLos(mContext);
        im      = (ImageView) mCaller.findViewById(R.id.Direction);
        compass = (ImageView) mCaller.findViewById(R.id.compass);
        tv_dist = (TextView)  mCaller.findViewById(R.id.Distance);
        tv_Az   = (TextView)  mCaller.findViewById(R.id.Azimuth);

    }

    private void InitLos( Context context){
        mLos = new Los(context);


        mUpdateYAWTimer = new EvsTimer(new EvsTimer.IEvsTimerCallback()
        {
            @Override
            public void onTick(long l)
            {
                mYaw = mLos.get360Yaw();
            }
        },
                YAW_TIMER_PERIOD, false);
        mUpdateYAWTimer.start();

        mUpdateDirTimer = new EvsTimer(new EvsTimer.IEvsTimerCallback()
        {
            @Override
            public void onTick(long l)
            {
                decodeMsg(oldMsg);
                Log.i("Blutooth Activity:", "old message decoded");
            }
        },
                DIR_TIMER_PERIOD, false);
    }

    @Override
    public void handleMessage(Message msg)
    {
        if (!gotFirstMsg){
            gotFirstMsg = true;
            mUpdateDirTimer.startAfter(DELAY_FROM_NEW_MSG);
        }
        String message = new String((byte[])msg.obj,0,msg.arg1);
        if (!message.equals(oldMsg)){
            oldMsg = message;
            Log.i("Blutooth Activity:", "new message decoded");
            decodeMsg(message);
        }
    }

    public void decodeMsg(String message){
        try {
            Gson gson = new Gson();
            DirectionsMessage directionsMessage = gson.fromJson(message, DirectionsMessage.class);
            double azimuth = directionsMessage.DistanceMeter;     //AzimuthDeg;
            double distance = directionsMessage.DistanceMeter;

            mCaller.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            if ((Math.abs(azimuth - mYaw) < BIAS_AZIMUTH)){
                //keep on Forward
                im.setVisibility(View.VISIBLE);
                im.setBackgroundResource(R.drawable.forwardred);
                mCaller.Direction = "Forward";
            } else if (distance>=MIN_DIST_BEFORE_TURN){
                if((azimuth - mYaw) > BIAS_AZIMUTH){
                    //target is on Right
                    im.setVisibility(View.VISIBLE);
                    im.setBackgroundResource(R.drawable.arrowblueright);
                    mCaller.Direction = "Forward";
                } else if((azimuth - mYaw) < -BIAS_AZIMUTH){
                    //target is on Left
                    im.setVisibility(View.VISIBLE);
                    im.setBackgroundResource(R.drawable.arrowblue);
                    mCaller.Direction = "Forward";
                }
            }

            if (distance < MIN_DIST_BEFORE_TURN) {
                if (azimuth - mYaw >= TURN_BIAS_AZIMUTH) {
                    //turn Right
                    im.setVisibility(View.VISIBLE);
                    im.setBackgroundResource(R.drawable.redarrow);
                    mCaller.Direction = "Left";
                } else if (azimuth - mYaw < -TURN_BIAS_AZIMUTH) {
                    //turn Left
                    im.setVisibility(View.VISIBLE);
                    im.setBackgroundResource(R.drawable.rightred);
                    mCaller.Direction = "Right";
                }
            }

            tv_dist.setVisibility(View.VISIBLE);
            tv_Az.setVisibility(View.VISIBLE);
            tv_dist.setText("Dist: "+String.valueOf(directionsMessage.DistanceMeter)+" m");
            tv_Az.setText("Azm: "+String.valueOf(mYaw)+"°");
            compass.setRotation(mYaw);

        }
        catch(Exception e)
        {

        }
    }





}   // Bluetooth Handler
