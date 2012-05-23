package com.artoo.search;

import android.app.Service;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import com.artoo.search.SearchService.LocalBinder;
import com.artoo.search.content.Query;

/**
 * 
 * This App is a Google(web search, image search and video search) Client for Android
 * 
 * This is the main activity and it displays a Button, two EditText , one checkbox and 3 tabs
 * 
 * 
 * @author Master
 * 
 */
public class UltimateSearchV2Activity extends TabActivity implements
		OnClickListener {

	private EditText myquery;
	private TabHost tabhost;
	SearchService mService;
	boolean mBound = false;
	public static Boolean connectivity = false;
	private CheckBox favourite;
	private ConnectivityManager mConnectivityManager;
	private EditText pref;
	public static int preference = 10;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		favourite = (CheckBox) findViewById(R.id.favourite);
		pref = (EditText) findViewById(R.id.preference);
		preference = Integer.parseInt(pref.getText().toString());
		myquery = (EditText) findViewById(R.id.querytext);

		myquery.addTextChangedListener(new TextWatcher() {

			/** Called when user entered a new character */
			public void afterTextChanged(Editable s) {
				String tem = s.toString();
				int n = tem.length();

				/** Checking whether the last character entered by user is space */
				if (n > 0 && tem.charAt(n - 1) == ' ') {

					/** Checking Internet Connection */
					if (mConnectivityManager.getNetworkInfo(
							ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
							|| mConnectivityManager.getNetworkInfo(
									ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
						connectivity = true;
					} else {
						connectivity = false;

					}

					/**
					 * when user types new keyword, update list view with new
					 * results
					 */
					if (favourite.isChecked()) {
						mService.refresh(myquery.getText().toString(),
								tabhost.getCurrentTab(), true);
					} else {
						mService.refresh(myquery.getText().toString(),
								tabhost.getCurrentTab(), false);
					}

					/** Predict Query */
					String predictedquery = null;
					Cursor c;
					c = managedQuery(Query.CONTENT_URI, null, Query.Query
							+ " LIKE '" + tem + "%'", null, null);
					if (c.moveToFirst()) {
						predictedquery = c.getString(c
								.getColumnIndex(Query.Query));
					}
					if (predictedquery != null)
						Toast.makeText(UltimateSearchV2Activity.this,
								predictedquery, Toast.LENGTH_SHORT).show();
				}

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});

		Button searchbutton = (Button) findViewById(R.id.searchbutton);
		searchbutton.setOnClickListener(this);

		/** Initializing tab */
		tabhost = getTabHost();
		TabHost.TabSpec tabspec;
		Intent intent;
		intent = new Intent().setClass(this, FirstActivity.class);
		intent.putExtra("FIRSTQUERY", "");
		tabspec = tabhost.newTabSpec("first").setIndicator("Web")
				.setContent(intent);
		tabhost.addTab(tabspec);

		intent = new Intent().setClass(this, SecondActivity.class);
		intent.putExtra("QUERY", "");
		tabspec = tabhost.newTabSpec("second").setIndicator("Images")
				.setContent(intent);
		tabhost.addTab(tabspec);

		intent = new Intent().setClass(this, ThirdActivity.class);
		intent.putExtra("QUERY", "");
		tabspec = tabhost.newTabSpec("third").setIndicator("Video");
		tabspec.setContent(intent);
		tabhost.addTab(tabspec);
		tabhost.setCurrentTab(1);

		/** Connect to Service */
		Intent intent1 = new Intent(this, SearchService.class);
		startService(intent1);
		bindService(intent1, mConnection, Context.BIND_AUTO_CREATE
				| Service.START_STICKY);
	}

	@Override
	public void onClick(View v) {

		/** Checking Internet Connection */
		if (mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.isConnectedOrConnecting()
				|| mConnectivityManager.getNetworkInfo(
						ConnectivityManager.TYPE_WIFI)
						.isConnectedOrConnecting()) {
			connectivity = true;
		} else {
			connectivity = false;

		}

		/** Refresh Tab Content */
		if (favourite.isChecked()) {
			mService.refresh(myquery.getText().toString(),
					tabhost.getCurrentTab(), true);
		} else {
			mService.refresh(myquery.getText().toString(),
					tabhost.getCurrentTab(), false);
		}

		System.gc();
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;

		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			mBound = false;
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBound) {
			unbindService(mConnection);
			mBound = false;
		}
	}
}