package everysight.Activity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.everysight.activities.managers.EvsPopupManager;
import com.everysight.activities.managers.EvsServiceInterfaceManager;
import com.everysight.base.EvsBaseActivity;
import com.everysight.base.EvsContext;
import com.everysight.notifications.EvsToast;
import com.everysight.utilities.EvsTimer;

import java.io.File;

import BluetoothConnection.AcceptThread;
import BluetoothConnection.BluetoothHandler;
import VoiceRecognition.Compare;
import VoiceRecorder.VoiceRecorder;
import VoiceRecorder.WavRecorder;

public class VoiceGestureInitActivity extends EvsBaseActivity
{
    private String TAG = getLocalClassName();
    private  String[] VoiceGesturesFeatures = {"ocr"};//, "camera", "video"};
    private int index = -1;
    private WavRecorder wavRecorder;

    /******************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_gesture_init);
        wavRecorder = new WavRecorder("");

    }

    /******************************************************************/
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    /******************************************************************/
    @Override
    public void onResume()
    {
        super.onResume();
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        Log.d(TAG, "onResume : We are running again!");
    }

    /******************************************************************/
    @Override
    public void onPause()
    {
        super.onPause();
    }

    /******************************************************************/
    @Override
    public void onTap()
    {
        super.onTap();
    }


    /******************************************************************/
    @Override
    public void onUp()
    {


    }


    /******************************************************************/
    @Override
    public void onDown()
    {
        //the default behaviour of down is to close the activity
        super.onDown();
    }

    /******************************************************************/
    @Override
    public void onBackward()
    {
        super.onBackward();
        TextView tv = (TextView) findViewById(R.id.voiceGestureText);
        tv.setText("Sample");
        recordAndSaveGesture("sample");
        String filePath1 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/music/"+"ocr"+".wav";  //VoiceGesturesFeatures[i]
        String filePath2 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/music/"+"sample"+".wav";
        EvsToast.show(this, "comparing \n"+"ocr"+" vs. " +"sample");
        compareVoices(filePath1, filePath2);
//        float accurecy = compareVoices(filePath1, filePath2);


    }



    @Override
    public void onForward() {
        super.onForward();
        index++;
        if (index >= VoiceGesturesFeatures.length){
            index=0;
        }
        TextView tv = (TextView) findViewById(R.id.voiceGestureText);
        tv.setText(VoiceGesturesFeatures[index]);
        recordAndSaveGesture(VoiceGesturesFeatures[index]);
    }

    private void recordAndSaveGesture(String voiceGesturesFeature) {
        try{
            recordGesture(this, voiceGesturesFeature);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void recordGesture(final Context ctx, final String feature) {
        String msg = feature +" - recordeing started..";
        wavRecorder.setFilepath("music/"+feature+".wav");
        wavRecorder.startRecording();
        EvsToast.show(ctx,msg);

        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
                wavRecorder.stopRecording();
                EvsToast.show(ctx,feature+" recording finished..");
            }
        }.start();
    }

    private float compareVoices(String filePath1, String filePath2) {
        File file = new File(filePath2);
        float accurecy = -1;
        if(file.exists()){
            accurecy = new Compare().match(filePath1, filePath2);
            EvsToast.show(this,"The accurecy between the two voice gestures is "+ accurecy+"%");
        } else {
            EvsToast.show(this,"matching failed");
        }
        return accurecy;
    }





    private void playBeep(){
//        MediaPlayer mp = MediaPlayer.create(this, R.raw.soho);
    }
}
