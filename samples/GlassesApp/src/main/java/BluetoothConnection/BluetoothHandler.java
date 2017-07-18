package BluetoothConnection;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import everysight.Activity.DirectionsActivity;
import everysight.Activity.R;

/**
 * Created by t-aryehe on 7/2/2017.
 */

public class BluetoothHandler extends Handler {

    private DirectionsActivity mCaller;

    public BluetoothHandler(DirectionsActivity caller)
    {
        mCaller = caller;
    }

    @Override
    public void handleMessage(Message msg)
    {
        ImageView im = (ImageView) mCaller.findViewById(R.id.Direction);
        TextView tv = (TextView) mCaller.findViewById(R.id.Distance);

        String message = new String((byte[])msg.obj,0,msg.arg1);

        try {
            Gson gson = new Gson();
            DirectionsMessage directionsMessage = gson.fromJson(message, DirectionsMessage.class);

            if(directionsMessage.Direction.equals("Right"))
            {
                if(!mCaller.Direction.equals("Right")){
                    im.setVisibility(View.VISIBLE);
                    im.setBackgroundResource(R.drawable.rightred);
                    mCaller.Direction = "Right";
                }
            }
            else if (directionsMessage.Direction.equals("Left"))
            {
                if(!mCaller.Direction.equals("Left")) {
                    im.setVisibility(View.VISIBLE);
                    im.setBackgroundResource(R.drawable.redarrow);
                    mCaller.Direction = "Left";
                }
            }
            else if (directionsMessage.Direction.equals("Forward"))
            {
                if(!mCaller.Direction.equals("Forward")) {
                    im.setVisibility(View.VISIBLE);
                    im.setBackgroundResource(R.drawable.forwardred);
                    mCaller.Direction = "Forward";
                }
            }
            else
            {
                im.setVisibility(View.INVISIBLE);
                tv.setVisibility(View.INVISIBLE);
                mCaller.Direction = "";
            }

            tv.setVisibility(View.VISIBLE);
            tv.setText(String.valueOf(directionsMessage.DistanceMeter)+" m");
        }
        catch(Exception e)
        {

        }
    }
}
