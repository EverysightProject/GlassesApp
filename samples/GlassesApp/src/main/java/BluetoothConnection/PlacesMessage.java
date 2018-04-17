package BluetoothConnection;

import android.location.Location;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by t-aryehe on 11/6/2017.
 */

public class PlacesMessage extends BtMessage {
    @SerializedName("PlacesData")
    public Map<String, Pair<Double,Double>> PlacessData = null;
}
