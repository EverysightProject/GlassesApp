package everysight.Activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.everysight.activities.managers.EvsPopupManager;
import com.everysight.activities.managers.EvsServiceInterfaceManager;
import com.everysight.base.EvsBaseActivity;
import com.everysight.base.EvsContext;
import com.everysight.notifications.EvsToast;
import com.everysight.utilities.EvsTimer;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import BluetoothConnection.AcceptThread;
import BluetoothConnection.BluetoothHandler;
import VoiceRecognition.Compare;
import VoiceRecorder.VoiceRecorder;
import VoiceRecorder.ExtAudioRecorder;
import VoiceRecorder.WavRecorder;


public class DirectionsActivity extends EvsBaseActivity
{
	private static final String CLICK_INTENT = "com.everysight.sample.click";
	private static final String TAG = "DirectionsActivity";
	private static final int REQUEST_ENABLE_BT = 1;
	private static final int LOGO_SHOW_UP_SCREEN = 4000; //[ms]

	private TextView mCxtCenterLabel = null;
	private int mCounter = 0;
	private EvsPopupManager mPopupManager;
	private Handler mBThandler;
	private AcceptThread acceptThread;
	private EvsTimer logoTimer= null;
	private VoiceRecorder mVoiceRecorder;
	private boolean firstRec = true;


	enum RecordingState {INIT, GESTURE}
	RecordingState recordingState = RecordingState.INIT;

	private  String[] VoiceGesturesFeatures = {"ocr", "camera", "video", "gesture"};
	private final int REQ_CODE_SPEECH_INPUT = 100;




	public String Direction = "";
	private String mVoice1Path;
	private String mVoice2Path;
	private ExtAudioRecorder mRecorder;
	private WavRecorder wavRecorder;
	private int featureIndex = 0;
	private boolean fetOrGest = true;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_layout);
		logoTimer = new EvsTimer(new EvsTimer.IEvsTimerCallback()
		{
			@Override
			public void onTick(long l)
			{
				findViewById(R.id.logo).setVisibility(View.INVISIBLE);
				logoTimer.stop();
			}
		},
				LOGO_SHOW_UP_SCREEN, false);

		logoTimer.startAfter(LOGO_SHOW_UP_SCREEN);

		//get the evs popup service
		final EvsPopupManager popupManager = (EvsPopupManager)getEvsContext().getSystemService(EvsContext.POPUP_SERVICE_EVS);
		//wait for the service to bind
		popupManager.registerForServiceStateChanges(new EvsServiceInterfaceManager.IServiceStateListener()
		{
			@Override
			public void onServiceConnected()
			{
				//mark the service as connected (binded)
				mPopupManager = popupManager;
			}
		});

		mBThandler = new BluetoothHandler(this, DirectionsActivity.this);
		acceptThread = new AcceptThread(mBThandler, DirectionsActivity.this, this);
		acceptThread.start();


		wavRecorder = new WavRecorder("music/testWav2.wav");

		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
			ImageView wifi_iv = (ImageView)findViewById(R.id.wifiImg);
			wifi_iv.setImageResource(R.drawable.wifi_on);
		}

	}

	/******************************************************************/
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		acceptThread.cancel();
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
		super.onUp();
		promptSpeechInput();
//		getSpeechInput();
//		Intent intent = new Intent(this, VoiceGestureInitActivity.class);
//		startActivity(intent);


	}

	private void recordGesture(final Context ctx, final String feature) {


		String msg = feature +" - recordeing started..";
		wavRecorder.setFilepath("music/"+feature+".wav");
		wavRecorder.startRecording();
		final TextView tv = (TextView) findViewById(R.id.Messages);
		tv.setText("Start Recording!");
		new CountDownTimer(750, 100) {
			public void onTick(long millisUntilFinished) {}
			public void onFinish() {tv.setText("");}
		}.start();

		new CountDownTimer(1500, 1000) {

			public void onTick(long millisUntilFinished) {}

			public void onFinish() {
//				mRecorder.stop();
				wavRecorder.stopRecording();
				final TextView tv = (TextView) findViewById(R.id.Messages);
				tv.setText("Stop Recording!");
				new CountDownTimer(1000, 100) {
					public void onTick(long millisUntilFinished) {}
					public void onFinish() {tv.setText("");}
				}.start();
			}
		}.start();
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

		//Initialize all basic voice gestures
		try{
			if (fetOrGest){
				recordGesture(this, "ocr");
			} else {
				recordGesture(this, "sample");
			}
			fetOrGest = !fetOrGest;
		} catch (Exception e){
			e.printStackTrace();
		}





	}

	private float compareVoices(String filePath1, String filePath2) {
		File file1 = new File(filePath1);
		File file2 = new File(filePath2);
		float accurecy = -1;
		if(file1.exists() && file2.exists()){
			accurecy = new Compare().match(filePath1, filePath2);
			EvsToast.show(this,"The accurecy between the two voice gestures is "+ accurecy+"%");
		} else {
			EvsToast.show(this,"matching failed");
		}
		return accurecy;
	}


	@Override
	public void onForward()
	{
		super.onForward();
		String filePath1 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/music/"+"ocr"+".wav";
		String filePath2 = Environment.getExternalStorageDirectory().getAbsolutePath()+"/music/"+"sample"+".wav";
		EvsToast.show(this, "comparing \n"+"ocr"+" vs. " +"sample");
		compareVoices(filePath1, filePath2);
	}

	public void getSpeechInput() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		Context context = this.getApplicationContext();
		if (intent.resolveActivity(context.getPackageManager()) != null) {
			startActivityForResult(intent, 10);
		} else {
			EvsToast.show(this, "Your Device Don't Support Speech Input");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case 10:
				if (resultCode == RESULT_OK && data != null) {
					ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					EvsToast.show(this, result.get(0));
				}
				break;
			case REQ_CODE_SPEECH_INPUT: {
				if (resultCode == RESULT_OK && null != data) {

					ArrayList<String> result = data
							.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					EvsToast.show(this, result.get(0));
				}
				break;
			}
		}
	}

	private void promptSpeechInput() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say something&#8230;");
		try {
			startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
		} catch (ActivityNotFoundException a) {
				a.printStackTrace();
		}
	}

}
