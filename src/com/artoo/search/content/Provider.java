package com.artoo.search.content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/** Content Provider for Tables Content and Query */
public class Provider extends ContentProvider {

	private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

	static {

		matcher.addURI(Query.AUTHORITY, Query.TABLE_NAME, 1);
		matcher.addURI(Query.AUTHORITY, Query.TABLE_NAME + "/#", 2);

		matcher.addURI(Query.AUTHORITY, Content.TABLE_NAME, 3);
		matcher.addURI(Query.AUTHORITY, Content.TABLE_NAME + "/#", 4);
	}

	private DatabaseHelper mDatabaseHelper;

	@Override
	public boolean onCreate() {
		
		mDatabaseHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		
		switch (matcher.match(uri)) {
		case 1:
			return Query.LIST_TYPE;

		case 2:
			return Query.ITEM_TYPE;

		case 3:
			return Content.LIST_TYPE;
			
		case 4:
			return Content.ITEM_TYPE;
			
		default:

		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		String table = null;
		SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

		switch (matcher.match(uri)) {
		case 1:
			table = Query.TABLE_NAME;
			break;
		case 2:
			table = Query.TABLE_NAME;
			break;
		case 3:
			table = Content.TABLE_NAME;
			break;
		case 4:
			table = Content.TABLE_NAME;
			break;

		default:
			throw new IllegalArgumentException("Wrong Uri");

		}

		long id = db.insert(table, null, values);

		return Uri.withAppendedPath(uri, "" + id);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String table = null;
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		
		switch (matcher.match(uri)) {
		case 1:
			table = Query.TABLE_NAME;
			break;
		case 2:
			table = Query.TABLE_NAME;
			String id = uri.getPathSegments().get(0);
			selection = selection == null ? Query._ID + "=" + id : selection
					+ " AND " + Query._ID + "=" + id;
			break;
		case 3:
			table = Content.TABLE_NAME;
			break;
		case 4:
			table = Content.TABLE_NAME;
			String id1 = uri.getPathSegments().get(0);
			selection = selection == null ? Content._ID + "=" + id1 : selection
					+ " AND " + Content._ID + "=" + id1;
		default:
			break;
		}
		
		Cursor c = db.query(table, projection, selection, selectionArgs, null,
				null, null);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		return 0;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	private class DatabaseHelper extends SQLiteOpenHelper {

		private static final int DB_VERSION = 1;
		private static final String DB_NAME = "Search.db";

		public DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);

		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(Query.CREATE_SQL);
			db.execSQL(Content.CREATE_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			onCreate(db);
		}
	}
}
