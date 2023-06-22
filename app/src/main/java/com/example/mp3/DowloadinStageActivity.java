package com.example.mp3;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.bumptech.glide.Glide;

import java.io.File;

public class DowloadinStageActivity extends AppCompatActivity {
    private ImageView imageImageNine;
    private TextView txtBLACKPINKHo, txt143BViews, txtDistance, txtFilesize;
    private ProgressBar progressBarGroupThree;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private long downloadId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dowloading_stage);

        imageImageNine = findViewById(R.id.imageImageNine);
        txtBLACKPINKHo = (TextView) findViewById(R.id.txtBLACKPINKHo);
        txt143BViews = (TextView) findViewById(R.id.txt143BViews);
        txtDistance = (TextView) findViewById(R.id.txtDistance);
        txtFilesize = (TextView) findViewById(R.id.txtFilesize);
        progressBarGroupThree = findViewById(R.id.progressBarGroupThree);

        String thumbnailUrl = getIntent().getStringExtra("thumbnailUrl");

        Glide.with(this)
                .load(thumbnailUrl)
                .into(imageImageNine);

        txtBLACKPINKHo.setText(getIntent().getStringExtra("title"));
        txt143BViews.setText(getIntent().getStringExtra("viewCount"));
        txtDistance.setText(getIntent().getStringExtra("length"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            downloadSong(getIntent().getStringExtra("link"), getIntent().getStringExtra("title"));
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadSong(getIntent().getStringExtra("link"), getIntent().getStringExtra("title"));
        } else {
            Toast.makeText(this, "Permission denied. Cannot download the song.", Toast.LENGTH_SHORT).show();
        }
    }
    private void downloadSong(String url, String title) {
        //download song in Download folder
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String formOneValue = getIntent().getStringExtra("formOneValue");
        request.setTitle(title);
        String fileName = title + ".mp3";
        File destinationDirectory;

        if (formOneValue != null && !formOneValue.isEmpty()) {
            destinationDirectory = new File(getExternalFilesDir(null), formOneValue);
        } else {
            destinationDirectory = new File(getExternalFilesDir(null), Environment.DIRECTORY_DOWNLOADS);
        }

        if (!destinationDirectory.exists()) {
            destinationDirectory.mkdirs();
        }

        File destinationFile = new File(destinationDirectory, fileName);
        request.setDestinationUri(Uri.fromFile(destinationFile));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadId = manager.enqueue(request);
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long completedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (completedDownloadId == downloadId) {
                    //send date from SuccessScreenActivity
                    unregisterReceiver(this);
                    intent = new Intent(DowloadinStageActivity.this, SuccessScreenActivity.class);
                    intent.putExtra("thumbnailUrl", getIntent().getStringExtra("thumbnailUrl"));
                    intent.putExtra("length", getIntent().getStringExtra("length"));
                    intent.putExtra("title", title);
                    intent.putExtra("viewCount", getIntent().getStringExtra("viewCount"));
                    intent.putExtra("link", getIntent().getStringExtra("link"));
                    intent.putExtra("size",getIntent().getStringExtra("size"));
                    intent.putExtra("formValue", getIntent().getStringExtra("formValue"));
                    intent.putExtra("formOneValue", getIntent().getStringExtra("formOneValue"));
                    startActivity(intent);
                }
            }
        };

        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(downloadId);
                Cursor cursor = manager.query(query);
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int status = cursor.getInt(columnIndex);
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        handler.removeCallbacks(this);
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        handler.removeCallbacks(this);
                        Intent intent = new Intent(DowloadinStageActivity.this, FailedScreenActivity.class);
                        //send date from FailedScreenActivity
                        intent.putExtra("thumbnailUrl", getIntent().getStringExtra("thumbnailUrl"));
                        intent.putExtra("length", getIntent().getStringExtra("length"));
                        intent.putExtra("title", title);
                        intent.putExtra("viewCount", getIntent().getStringExtra("viewCount"));
                        intent.putExtra("link", getIntent().getStringExtra("link"));
                        intent.putExtra("size",getIntent().getStringExtra("size"));
                        intent.putExtra("formValue", getIntent().getStringExtra("formValue"));
                        intent.putExtra("formOneValue", getIntent().getStringExtra("formOneValue"));
                        startActivity(intent);
                    } else {
                        int progressIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                        int totalIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                        int downloadedBytes = cursor.getInt(progressIndex);
                        int totalBytes = cursor.getInt(totalIndex);

                        if (totalBytes != 0) {
                            //send move the progress with a handler with delay 1 miliseconds
                            int progressPercentage = (int) ((downloadedBytes * 100L) / totalBytes);
                            String fileSizeString = getIntent().getStringExtra("size");
                            double fileSize = parseFileSize(fileSizeString);
                            double downloadedSize = Math.abs(progressPercentage * fileSize / 100);
                            String progressText = formatFileSize(downloadedSize) + " / " + formatFileSize(fileSize);
                            txtFilesize.setText(progressText);

                            progressBarGroupThree.setProgress(progressPercentage);
                        }
                        handler.postDelayed(this, 1);
                    }
                }
                cursor.close();
            }
        };
        handler.post(runnable);
    }
    //fom size memory in number
    private double parseFileSize(String fileSizeString) {
        String[] parts = fileSizeString.split(" ");
        if (parts.length < 2) {
            return 0;
        }
        double fileSize = Double.parseDouble(parts[0]);
        String unit = parts[1];
        unit = unit.toUpperCase();
        switch (unit) {
            case "KB":
                fileSize *= 1024;
                break;
            case "MB":
                fileSize *= 1024 * 1024;
                break;
            case "GB":
                fileSize *= 1024 * 1024 * 1024;
                break;
            case "TB":
                fileSize *= 1024 * 1024 * 1024 * 1024;
                break;
            case "PB":
                fileSize *= 1024 * 1024 * 1024 * 1024 * 1024;
                break;
        }
        return fileSize;
    }
    //from number in size
    private String formatFileSize(double fileSize) {
        String[] units = {"B", "KB", "MB", "GB", "TB", "PB"};
        int unitIndex = 0;
        while (fileSize >= 1024 && unitIndex < units.length - 1) {
            fileSize /= 1024;
            unitIndex++;
        }
        return String.format("%.2f %s", fileSize, units[unitIndex]);
    }
}
