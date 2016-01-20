package com.pexample.videocompressdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button btn,btn1;
    TextView txtFile;
    private NotificationHelper mNotificationHelper;
    private static final int FILE_SELECT_CODE = 0;
    private String path = "", filename="", outputFilePath = "/storage/emulated/0/Developer/result2.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.btn);
        btn1 = (Button) findViewById(R.id.btn1);
        txtFile = (TextView) findViewById(R.id.txtFile);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCompression();
            }
        });

      btn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              //showFileChooser();
              path = "/storage/emulated/0/Developer/test.mp4";
              outputFilePath = "/storage/emulated/0/Developer/result2.mp4";
              txtFile.setText(path);
          }
      });


    }

    private void startCompression() {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Compressing the video");

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.show();
                mNotificationHelper = new NotificationHelper(MainActivity.this);
                mNotificationHelper.createNotification();
            }

            @Override
            protected Void doInBackground(Void... params) {
                processVideoFile();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressDialog.dismiss();
                mNotificationHelper.completed();
            }
        }.execute();
    }

    private void processVideoFile() {
        final File primaryExternalStorage = Environment.getExternalStorageDirectory();
        Log.e("PATH", primaryExternalStorage.getAbsolutePath());

        File temp = new File(primaryExternalStorage.getAbsolutePath() + "/Developer/test.mp4");
        Log.e("temp", temp.exists()+"");

        // input source
        final Clip clip_in = new Clip(path/*"/storage/emulated/0/Developer/test.mp4"*/);

        Activity activity = (Activity) MainActivity.this;
        File fileTmp = activity.getCacheDir();
        File fileAppRoot = new File(activity.getApplicationInfo().dataDir);

        final Clip clip_out = new Clip(outputFilePath);
        //put flags in clip
        clip_out.videoFps = "30";
        clip_out.width = 480;
        clip_out.height = 320;
        clip_out.videoCodec = "libx264";
        clip_out.audioCodec = "copy";
        clip_out.size = 1000000;
        clip_out.startTime = "00:00:10.0000";
        clip_out.duration = 40;

        try {
            FfmpegController fc = new FfmpegController(activity, fileAppRoot);
            fc.processVideo(clip_in, clip_out, false, new ShellUtils.ShellCallback() {

                @Override
                public void shellOut(String shellLine) {
                    System.out.println("MIX> " + shellLine);
                }

                @Override
                public void processStarted(int exitValue) {
                    Log.e("ffmpeg", "processStarted "+exitValue);
                }

                @Override
                public void processComplete(int exitValue) {
                    Log.e("ffmpeg", "processComplete "+exitValue);
                    if (exitValue != 0) {
                        System.err.println("concat non-zero exit: " + exitValue);
                        Log.e("ffmpeg", "Compilation error. FFmpeg failed");
                        Toast.makeText(MainActivity.this, "result: ffmpeg failed", Toast.LENGTH_LONG).show();
                    } else {
                        if (new File(outputFilePath).exists()) {
                            Log.e("ffmpeg", "Success file:" + outputFilePath);
                        }
                    }
                }
            });
        } catch (FileNotFoundException e) {
            Log.e("ffmpeg", e.toString());
        } catch (IOException e) {
            Log.e("ffmpeg", e.toString());
        } catch (Exception e) {
            Log.e("ffmpeg", e.toString());
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("file/*");
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Log.e("FILE URI", "File Uri: " + uri.toString());
                    if (uri != null && "content".equals(uri.getScheme())) {
                        Cursor cursor = getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
                        cursor.moveToFirst();
                        path = cursor.getString(0);
                        cursor.close();
                    } else {
                        path = uri.getPath();
                    }
                    Log.e("FILE URI", "File Path: " + path);
                    File sourceFile = new File(path);
                    filename = sourceFile.getName();
                    int pos = filename.lastIndexOf(".");
                    if (pos > 0) {
                        filename = filename.substring(0, pos);
                    }

                    Log.e("FILE URI", "File fileName: " + filename);

                    String type = null;
                    String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
                    if (extension != null) {
                        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                    }
                    Log.e("type", type+"");
                    Log.e("size in MB", (double)sourceFile.length()/(1024*1024)+"");
                    txtFile.setText("File Path: "+ path);

                } else {
                    Log.e("FILE URI", "File Path: " + "else");
                }
                break;
        }
    }
}
