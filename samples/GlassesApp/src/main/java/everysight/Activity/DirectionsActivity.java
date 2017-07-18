package everysight.Activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.everysight.activities.managers.EvsPopupManager;
import com.everysight.activities.managers.EvsServiceInterfaceManager;
import com.everysight.base.EvsBaseActivity;
import com.everysight.base.EvsContext;
import com.everysight.notifications.EvsAlertNotification;
import com.everysight.notifications.EvsNotification;
import com.everysight.notifications.EvsToast;

import BluetoothConnection.AcceptThread;
import BluetoothConnection.BluetoothHandler;

public class DirectionsActivity extends EvsBaseActivity
{
	private static final String CLICK_INTENT = "com.everysight.sample.click";
	private static final String TAG = "DirectionsActivity";
	private static final int REQUEST_ENABLE_BT = 1;

	private TextView mCxtCenterLabel = null;
	//private ImageView mPrevButton = null;
	//private ImageView mNextButton = null;
	private int mCounter = 0;
	private EvsPopupManager mPopupManager;
	private Handler mBThandler;
	private AcceptThread acceptThread;

	public String Direction = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_layout);

	//	mCxtCenterLabel = (TextView) this.findViewById(R.id.centerLable);
	//	mPrevButton = (ImageView) this.findViewById(R.id.prevButton);
//		mNextButton = (ImageView) this.findViewById(R.id.nextButton);
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

		mBThandler = new BluetoothHandler(this);
		acceptThread = new AcceptThread(mBThandler);
		acceptThread.start();
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
	public void onForward()
	{
		super.onForward();
	//	animateClick(mNextButton,false);
		EvsToast.show(this,"Forward swipe");
	}


	@Override
	public void onBackward()
	{
		super.onBackward();
	//	animateClick(mPrevButton,true);
		if(mPopupManager!=null)
		{
			//show a popup message upon backward swipe

			//create the callback intent
			PendingIntent onClick = PendingIntent.getBroadcast(this, 0, new Intent(CLICK_INTENT), PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT);

			//create the popup notification
			EvsNotification notif = new EvsAlertNotification()
					.setTapAction(this,R.drawable.ic_launcher,null,onClick)
					.setTitle("Back swipe")
					.setMessage("Tap me");

			//ask Everysight OS to show the popup
			mPopupManager.notify(notif);
		}
	}

}
