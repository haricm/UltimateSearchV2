package com.artoo.search.content;

import android.net.Uri;
import android.provider.BaseColumns;

/** Definitions for Content Table */
public class Content implements BaseColumns {

	public static final String TABLE_NAME = "Content";

	public static final String Query_ID = "query_id";
	public static final String _ID = "_id";
	public static final String Url = "url";
	public static final String Title = "title";
	public static final String Content = "content";
	public static final String Tab = "tab";

	public static final String AUTHORITY = "com.artoo.search.mycontentprovider";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE_NAME);

	public static final String CREATE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_NAME + "( _id INTEGER PRIMARY KEY," + Query_ID + " INT,"
			+ Url + " VARCHAR," + Title + " VARCHAR, " + Content + " VARCHAR, "
			+ Tab + " INT );";

	public static final String ITEM_TYPE = "content/item";
	public static final String LIST_TYPE = "content/list";

}
