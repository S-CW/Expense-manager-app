package com.example.moneymanager.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.example.moneymanager.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppInnerDownLoader {
    private static final String TAG = AppInnerDownLoader.class.getSimpleName();
    /**
     * Download APK from server
     */
    @SuppressWarnings("unused")
    public static void downLoadApk(final Context mContext, final String downURL, final String appName) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        View view = inflater.inflate(R.layout.progress_dialog_layout, null);
//
//        TextView progressTitle = view.findViewById(R.id.dialog_title);
//        TextView progressMessage = view.findViewById(R.id.dialog_message);
//        TextView progressPercentage = view.findViewById(R.id.progress_percentage);
//        TextView progressFileSize = view.findViewById(R.id.progress_file_size);
//        ProgressBar progressBar = view.findViewById(R.id.progress_bar);

        String title = "Version Upgrade";
        String message = "Downloading installation package, please wait";
//        progressTitle.setText(title);
//        progressMessage.setText(message);

//        builder.setView(view)
//                .setCancelable(false);
//        AlertDialog mDialog = builder.create();
//        mDialog.show();

        final ProgressDialog pd;
        pd = new ProgressDialog(mContext);
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setTitle(title);
        pd.setMessage(message);
        pd.show();


        new Thread() {
            @Override
            public void run() {
                try {
                    File file = downloadFile(downURL,appName, mContext, pd);
                    Thread.sleep(3000);

                    installApk(mContext, file);
//                    mDialog.dismiss();
                    pd.dismiss();
                } catch (Exception e) {
                    Log.e(TAG, String.valueOf(e));
//                    mDialog.dismiss();
                    pd.dismiss();
                }
            }
        }.start();
    }

    /**
     * Download the latest update files from the server
     *
     * @param path
     *            Download path
     * //@param progressBar
     *            progress bar
     * @return
     * @throws Exception
     */
    private static File downloadFile(String path, String appName, Context context, ProgressDialog pd) throws Exception {
        // If they are equal, it means that the current sdcard is mounted on the phone and is available.
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);

            // Get file size
            int lengthOfFile = conn.getContentLength();
//            progressBar.setMax(lengthOfFile);
            pd.setMax(conn.getContentLength());
            InputStream is = conn.getInputStream();

            //file:///storage/emulated/0/Android/data/your-package/files/Download/
            File APP_FOLDER = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(APP_FOLDER, appName+".apk");

//        Start transferring data
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            int total = 0;

            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                total += len;

                // Get the current download volume
//                progressBar.setProgress(total);
//                progressPercentage.setText(String.valueOf((int) total * 100 / lengthOfFile) + "%");
//                progressFileSize.setText(String.valueOf(total) + "/" + String.valueOf(lengthOfFile));

                pd.setProgress(total);
            }


            fos.close();
            bis.close();
            is.close();
            return file;
        } else {
            throw new IOException("External Storage SD Card not found");
        }
    }

    /**
     * Install apk
     */
    private static void installApk(Context mContext, File file) {

        Uri fileUri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() +".file-provider", file);

        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intent.setData(fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// Prevent applications from being unable to open
        mContext.startActivity(intent);
    }
}
