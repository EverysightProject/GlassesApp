package BluetoothConnection;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

/**
 * Created by t-aryehe on 11/6/2017.
 */

public class BtMessage {

    @SerializedName("MessageType")
    public String MessageType = null;

    @SerializedName("GeoLocation")
    public Location GeoLocation = null;
}
