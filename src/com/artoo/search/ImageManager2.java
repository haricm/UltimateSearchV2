package com.artoo.search;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

public class ImageManager2 extends AsyncTask<String, Void, Void> {

	private static File cacheDirectory;
	private static HashMap<String, SoftReference<Bitmap>> hashmap = new HashMap<String, SoftReference<Bitmap>>();

	Bitmap bitmap;
	String url;

	private Context context;
	private ImageView imageView;

	public ImageManager2(Context context, ImageView imageView) {
		this.context = context;
		this.imageView = imageView;
	}

	@Override
	protected Void doInBackground(String... urls) {
		url = urls[0];

		if (hashmap.containsKey(url.toString())) {
			bitmap = hashmap.get(url.toString()).get();
			return null;
		} else {

			try {
				cacheDirectory = context.getCacheDir();

				String filename;
				filename = String.valueOf(url.hashCode());
				File file = new File(cacheDirectory, filename);

				bitmap = BitmapFactory.decodeFile(file.getPath());

				if (bitmap != null) {
					hashmap.put(url.toString(), new SoftReference<Bitmap>(
							bitmap));
					return null;
				}

				Uri uri = Uri.parse(url);
				URI muri = new URI(uri.getScheme(), uri.getUserInfo(),
						uri.getHost(), uri.getPort(), uri.getPath(),
						uri.getQuery(), null);
				URL url1 = muri.toURL();

				if (url1 != null) {
					URLConnection connection = url1.openConnection();
					InputStream is = connection.getInputStream();
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 4;
					bitmap = BitmapFactory.decodeStream(is, null, options);

					if (bitmap != null) {
						hashmap.put(url, new SoftReference<Bitmap>(bitmap));

						FileOutputStream fos = new FileOutputStream(file);
						bitmap.compress(Bitmap.CompressFormat.PNG, 20, fos);
						fos.close();
					}
				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void param) {
		imageView.setImageBitmap(bitmap);
	}

}
