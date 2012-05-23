package com.artoo.search;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class SearchService extends Service {

	private final IBinder mBinder = new LocalBinder();
	private List<Callback> callbacks = new ArrayList<Callback>();

	@Override
	public void onCreate() {
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public class LocalBinder extends Binder {
		SearchService getService() {
			return SearchService.this;
		}
	}

	/** Refreshes Tab */
	public void refresh(String query, int tab, boolean b) {
		for (Callback cb : callbacks) {
			cb.onQueryChanged(query, b);
		}

	}

	public void addCallBack(Callback callback) {
		this.callbacks.add(callback);
	}

	/** Interface is Implemented in Activities(1st, 2nd & 3rd) */
	public static interface Callback {

		public abstract void onQueryChanged(String query, Boolean b);

		public abstract void onQueryResultReceived();

	}
}
