package app.com.socialAuth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

public class Utils {

	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}

	public static String getPath() {
		String path = "";
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			path = Environment.getExternalStorageDirectory().getAbsolutePath();
		} else if ((new File("/mnt/emmc")).exists()) {
			path = "/mnt/emmc";
		} else {
			path = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return path;
	}

	public static String getImagePath(Intent data, Activity activity) {
		String path = null;
		try {
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(activity.getContentResolver()
						.openInputStream(data.getData()), new Rect(), options);
				final int REQUIRED_SIZE = 200;
				int scale = 1;
				while (options.outWidth / scale / 2 >= REQUIRED_SIZE
						&& options.outHeight / scale / 2 >= REQUIRED_SIZE)
					scale *= 2;

				options.inSampleSize = scale;
				options.inJustDecodeBounds = false;

				Bitmap bm = BitmapFactory.decodeStream(activity
						.getContentResolver().openInputStream(data.getData()),
						new Rect(), options);
				String fileName = String.valueOf(System.currentTimeMillis())
						+ ".jpg";
				OutputStream fOut = activity.openFileOutput(fileName,
						Context.MODE_PRIVATE);
				bm.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
				fOut.flush();
				fOut.close();
				path = activity.getFileStreamPath(fileName).getAbsolutePath();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			System.gc();
			Toast.makeText(activity,
					"Image was large Please Try another Image",
					Toast.LENGTH_LONG).show();
		}
		return path;
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public static String streamToString(InputStream is) throws IOException {
		String str = "";

		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));

				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}

				reader.close();
			} finally {
				is.close();
			}

			str = sb.toString();
		}
		return str;
	}
	public static String getPathDropBox() {
		String path = "";
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			path = Environment.getExternalStorageDirectory().getAbsolutePath();
		} else if ((new File("/mnt/emmc")).exists()) {
			path = "/mnt/emmc";
		} else {
			path = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return path + "/DropBoxDemo";
	}



	public static void showNetworkAlert(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Network Alert");
		builder.setMessage("Please check your network connection and try again");
		builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
	public static final int MY_PERMISSIONS_REQUEST_CAMERA = 124;

	public static boolean checkPermission(final Context context) {
		int currentAPIVersion = Build.VERSION.SDK_INT;
		if (currentAPIVersion >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
					alertBuilder.setCancelable(true);
					alertBuilder.setTitle("Permission necessary");
					alertBuilder.setMessage("External storage permission is necessary");
					alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
						}
					});
					AlertDialog alert = alertBuilder.create();
					alert.show();
				} else {
					ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
				}
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}

	public static boolean checkPermissionCam(final Context context) {
		int currentAPIVersion = Build.VERSION.SDK_INT;
		if (currentAPIVersion >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
					alertBuilder.setCancelable(true);
					alertBuilder.setTitle("Permission necessary");
					alertBuilder.setMessage("Camera permission is necessary");
					alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA},
									MY_PERMISSIONS_REQUEST_CAMERA);
						}
					});
					AlertDialog alert = alertBuilder.create();
					alert.show();
				} else {
					ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
				}
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}
	public static void showAlertBox(Context context, String msg, DialogInterface.OnClickListener okListener) {
		new AlertDialog.Builder(context).setTitle(null).setMessage(msg).setPositiveButton("OK", okListener).show().
				setCancelable(false);
	}
	public static void showAlertBoxForConfirmation(Context context, String msg, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {

		new AlertDialog.Builder(context).setTitle(null).setMessage(msg)
				.setPositiveButton("YES", okListener)
				.setNegativeButton("NO", cancelListener)
				.show().setCancelable(false);
	}

	public static void showAlertDialog(Context context, String message, String[] arrayList, DialogInterface.OnClickListener onClickedListener) {
		android.support.v7.app.AlertDialog.Builder dialogBuilder = new android.support.v7.app.AlertDialog.Builder(context);
		dialogBuilder.setTitle(message);
		dialogBuilder.setItems(arrayList, onClickedListener);
		//Create alert dialog object via builder
		android.support.v7.app.AlertDialog alertDialogObject = dialogBuilder.create();
		//Show the dialog
		alertDialogObject.show();
	}
}
