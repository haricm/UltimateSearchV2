package com.artoo.search.content;

import android.net.Uri;
import android.provider.BaseColumns;

/** Definitions for Content Table */
public class Query implements BaseColumns {

	public static final String TABLE_NAME = "Query";
	public static final String _ID = "_ID";
	public static final String Query = "query";
	public static final String Time = "time";
	public static final String Favourite = "favourite";

	public static final String AUTHORITY = "com.artoo.search.mycontentprovider";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE_NAME);

	public static final String CREATE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME + "(" + " _ID INTEGER PRIMARY KEY , " + Query
			+ " VARCHAR," + Time + " VARCHAR, " + Favourite + " BOOLEAN);";

	public static final String ITEM_TYPE = "query/item";
	public static final String LIST_TYPE = "query/list";
}
