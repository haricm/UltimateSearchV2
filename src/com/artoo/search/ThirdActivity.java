package com.artoo.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Time;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.artoo.search.SearchService.Callback;
import com.artoo.search.SearchService.LocalBinder;
import com.artoo.search.content.Content;
import com.artoo.search.content.Query;

/**
 * This activity displays results extracted from Google video search
 * 
 * @author Master
 * 
 */
public class ThirdActivity extends ListActivity {
	int start, limit;
	private ListView webresults;
	private String query;
	private ArrayList<ResultDataStructure> webr;
	private SearchService mSearchService = null;
	Boolean mBound = false;
	Boolean fav;
	private Time time3;

	public ServiceConnection mConnection1 = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			mSearchService = binder.getService();
			mBound = true;
			mSearchService.addCallBack(new Callback() {

				@Override
				public void onQueryResultReceived() {

				}

				@Override
				public void onQueryChanged(String query1, Boolean b) {
					if (UltimateSearchV2Activity.connectivity == false) {
						Toast.makeText(ThirdActivity.this, "No Network ",
								Toast.LENGTH_LONG).show();

						fav = b;
						query = query1;
						GetItFromWeb getitman = new GetItFromWeb();

						getitman.execute();

					} else {
						fav = b;
						query = query1;
						GetItFromWeb getitman = new GetItFromWeb();
						getitman.execute();
					}
				}
			});

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mSearchService = null;
			mBound = false;
		}

	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		time3 = new Time(0);
		start = 0;
		webresults = getListView();
		Intent intent = new Intent(ThirdActivity.this, SearchService.class);
		getApplicationContext().bindService(intent, mConnection1,
				Context.BIND_AUTO_CREATE);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBound) {
			mBound = false;
		}
	}

	public class GetItFromWeb extends
			AsyncTask<Void, Void, ArrayList<ResultDataStructure>> {
		ProgressDialog progress;

		@Override
		protected ArrayList<ResultDataStructure> doInBackground(Void... params) {

			limit = UltimateSearchV2Activity.preference;
			String temp = "";
			String partofurl;
			int flag = 0;
			StringBuilder responsestringbuilder = new StringBuilder();
			partofurl = Integer.toString(start) + "&num="
					+ Integer.toString(limit);
			StringTokenizer token = new StringTokenizer(query);
			while (token.hasMoreTokens()) {
				if (flag == 0) {
					temp = token.nextToken();
				} else {
					temp = temp + "+" + token.nextToken();
				}
			}
			if (UltimateSearchV2Activity.connectivity) {

				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpget = new HttpGet(
						"https://ajax.googleapis.com/ajax/services/search/video?v=1.0&q=%22"
								+ temp + "%22&rsz=8&start=" + partofurl);
				try {
					HttpResponse resp = client.execute(httpget);
					if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						InputStream stream = resp.getEntity().getContent();
						BufferedReader buffer = new BufferedReader(
								new InputStreamReader(stream));
						while ((temp = buffer.readLine()) != null) {
							responsestringbuilder.append(temp);
						}
						/**
						 * JSON Structure
						 * 
						 * { "responsedata": { "results": [ {
						 * "GSearchResultClass":".." "unescapedUrl": "..",
						 * "url": "..", "visibleUrl": "..", "cacheUrl": "..",
						 * "title": "..", "titleNoFormatting": "..", "content":
						 * ".." }, { . . . } ] "cursor": { "resultCount": "...",
						 * "pages": [ { "start": "..", "label": .. }, . . ],
						 * "estimatedResultCount": "..", "currentPageIndex": ..,
						 * "moreResultsUrl": "..", "searchResultTime": ".." } },
						 * "responseDetails": .., "responseStatus": .. }
						 * "searchResultTime": ".." } }, "responseDetails": ..,
						 * "responseStatus": .. }
						 * 
						 * 
						 */

						JSONObject json, respdata;
						JSONArray res;
						int len, i;
						webr = new ArrayList<ResultDataStructure>();
						json = new JSONObject(responsestringbuilder.toString());
						respdata = json.getJSONObject("responseData");
						res = respdata.getJSONArray("results");
						len = res.length();
						for (i = 0; i < len; ++i) {

							JSONObject obj = res.getJSONObject(i);
							ResultDataStructure rds = new ResultDataStructure(
									obj.getString("url"),
									obj.getString("titleNoFormatting"),
									obj.getString("content"));
							webr.add(rds);
							if (fav) {

								ContentResolver cr = getContentResolver();
								Cursor c;

								c = managedQuery(Query.CONTENT_URI, null,
										Query.Query + "= '" + query + "'",
										null, null);
								ContentValues contentvalues = new ContentValues();
								int id;

								if (!c.moveToFirst()) {
									ContentValues queryvalues = new ContentValues();
									queryvalues.put(Query.Query, query);
									queryvalues.put(Query.Time,
											time3.toString());
									queryvalues.put(Query.Favourite, "true");
									cr.insert(Query.CONTENT_URI, queryvalues);
								} else {
									id = c.getInt(c.getColumnIndex("_ID"));
									c = managedQuery(
											Content.CONTENT_URI,
											null,
											Content.Query_ID + " = "
													+ Integer.toString(id)
													+ " AND " + Content.Tab
													+ " = "
													+ Integer.toString(2),
											null, null);
									if (c.getCount() < 8) {
										contentvalues.put(Content.Url, rds.url);
										contentvalues.put(Content.Title,
												rds.title);
										contentvalues.put(Content.Content,
												rds.content);
										contentvalues.put(Content.Query_ID, id);
										contentvalues.put(Content.Tab, 2);
										cr.insert(Content.CONTENT_URI,
												contentvalues);
									}

								}

							}
						}
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (webr != null)
					webr.add(new ResultDataStructure("", "Next", ""));

			} else {

			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<ResultDataStructure> data) {
			if (UltimateSearchV2Activity.connectivity) {
				if (webr != null) {
					webresults.setAdapter(new ResultDataAdapter(
							ThirdActivity.this, webr));
				}
			} else {
				SimpleCursorAdapter cursorAdapter = null;
				Cursor c = managedQuery(Query.CONTENT_URI, null, Query.Query
						+ " = '" + query + "'", null, null);

				if (c.moveToFirst()) {

					int id = c.getInt(c.getColumnIndex("_ID"));
					c = managedQuery(
							Content.CONTENT_URI,
							null,
							Content.Query_ID + " = " + Integer.toString(id)
									+ " AND " + Content.Tab + " = "
									+ Integer.toString(2), null, null);
					Context context = getApplicationContext();
					cursorAdapter = new SimpleCursorAdapter(context,
							R.layout.listitem, c,
							new String[] { Content.Url, Content.Title,
									Content.Content, Content._ID }, new int[] {
									R.id.url, R.id.title, R.id.content });
				}
				webresults.setAdapter(cursorAdapter);
			}
		}

	}

	public class ResultDataAdapter extends BaseAdapter {

		private Context context;
		private ArrayList<ResultDataStructure> data;

		public ResultDataAdapter(Context context,
				ArrayList<ResultDataStructure> data) {
			this.data = data;
			this.context = context;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = ((Activity) context).getLayoutInflater().inflate(
					R.layout.listitem, null);

			ResultDataStructure rds = (ResultDataStructure) getItem(position);

			TextView title = (TextView) convertView.findViewById(R.id.title);
			title.setText(rds.title);
			TextView url = (TextView) convertView.findViewById(R.id.url);
			url.setText(rds.url);
			TextView con = (TextView) convertView.findViewById(R.id.content);
			con.setText(Html.fromHtml(rds.content));

			return convertView;
		}

	}

	@Override
	public void onListItemClick(ListView l, View view, int position, long id) {
		ResultDataStructure xyz = null;
		try {
			xyz = (ResultDataStructure) webr.get(position);
		} catch (NullPointerException e) {

		}
		if (!(xyz.title).equals("Next")) {
			Intent intentgo = new Intent(Intent.ACTION_VIEW);
			intentgo.setData(Uri.parse(xyz.url));
			startActivity(intentgo);
		} else {
			start = start + 10;
			webr.clear();
			GetItFromWeb getit = new GetItFromWeb();
			getit.execute();
		}
	}

}