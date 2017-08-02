package LOS;
        import android.content.Context;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.os.Bundle;
        import android.os.PowerManager;
        import android.util.Log;
        import android.view.View;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.everysight.base.EvsBaseActivity;
        import com.everysight.utilities.EvsTimer;
        import com.everysight.utilities.SensorOrientationUtils;

        import java.text.DecimalFormat;

/*
This is a standard Android sensors example, with translation to the glasses Axis
*/
public class Los  implements SensorEventListener
{
    private boolean mIsLosActive = false;
    private SensorManager mSensorManager;
    private PowerManager.WakeLock mWakeLock;

    private float[] mLosAngles = null;
    private Sensor mQuaternion;
    private EvsTimer mUpdateTimer = null;
    private static final int TIMER_PERIOD = 200;
    private DecimalFormat mFormat;
    private Context mCtx;
    private float mYaw;

    /******************************************************************/
    public Los(Context context){
        mCtx = context;

        final PowerManager pm = (PowerManager)  mCtx.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "LosSample");
        mWakeLock.acquire();

        mSensorManager = (SensorManager) mCtx.getSystemService(Context.SENSOR_SERVICE);
        mQuaternion = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        mFormat = new DecimalFormat("#.##");

        mUpdateTimer = new EvsTimer(new EvsTimer.IEvsTimerCallback()
        {
            @Override
            public void onTick(long l)
            {
                updateLos();
            }
        },
                TIMER_PERIOD, false);
        activateLos();


    }


    private void activateLos()
    {
        mSensorManager.registerListener(this, mQuaternion, SensorManager.SENSOR_DELAY_FASTEST);
        mUpdateTimer.start();
        mIsLosActive = true;
    }


    private void updateLos()
    {
        float[] losAngles = mLosAngles.clone();
        mYaw= Float.parseFloat(mFormat.format(Math.toDegrees(losAngles[0])));
    }


    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if (event == null)
        {
            return;
        }

        int sensor_type = event.sensor.getType();
        if(sensor_type != Sensor.TYPE_ROTATION_VECTOR)
        {
            return;
        }

        float[] quaternion = event.values.clone();//Quarernion is [x,y,z,w]
        mLosAngles = SensorOrientationUtils.QuaternionToAngles(quaternion);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }

    public float getYaw(){
        return mYaw;
    }

    public float get360Yaw(){
        if (mYaw<0){
            return mYaw+360;
        }
    return mYaw;
}
}
