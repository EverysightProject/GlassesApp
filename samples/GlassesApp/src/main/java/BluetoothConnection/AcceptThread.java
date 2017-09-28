package BluetoothConnection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.everysight.notifications.EvsToast;

import java.io.IOException;
import java.util.UUID;

import everysight.Activity.DirectionsActivity;
import everysight.Activity.R;

import static com.everysight.base.EvsContext.TAG;

/**
 * Created by t-aryehe on 6/23/2017.
 */

public class AcceptThread extends Thread {
    private final String NAME = "Glasses";
    private final BluetoothServerSocket mmServerSocket;
    private final UUID MY_UUID = new UUID(1,1);
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private Context mContext;
    private DirectionsActivity mCaller;

    public AcceptThread(Handler handler, Context context, DirectionsActivity caller) {
        mContext = context;
        mCaller = caller;
        // Use a temporary object that is later assigned to mmServerSocket
        // because mmServerSocket is final.
        BluetoothServerSocket tmp = null;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            // MY_UUID is the app's UUID string, also used by the client code.
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's listen() method failed", e);
        }
        mmServerSocket = tmp;
        mHandler = handler;
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e(TAG, "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                manageMyConnectedSocket(socket);
                try {
                    mmServerSocket.close();
                }
                catch(IOException e)
                {
                    Log.i(TAG,e.toString());
                }
                break;
            }
        }
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }

    public void manageMyConnectedSocket(BluetoothSocket socket)
    {
        BluetoothCommunicator bc = BluetoothCommunicator.getInstance();
        bc.connect(socket,mHandler);
        EvsToast.show(mContext,"Bluetooth Connected");
        final ImageView bt_iv = (ImageView)mCaller.findViewById(R.id.bluetoothImg);


        mCaller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bt_iv.setImageResource(R.drawable.bt_on);
            }
        });

        Log.i(TAG,"Connected");
    }
}
