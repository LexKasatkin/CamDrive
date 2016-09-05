package com.camdrive;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CamerasActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    ArrayList<Camera> arrayListCams;
    SharedPreferences sPref;
    public String MY_PREF = "MY_PREF";
    DataAuthentication dataAuthentication;
    String urlCam="http://95.170.177.173/mobile/api_native/cameras";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cameras);
        sPref = this.getSharedPreferences(MY_PREF, Activity.MODE_PRIVATE);
        dataAuthentication=new DataAuthentication();
        doSomethingRepeatedly();

    }

    class CamerasTask extends AsyncTask<Void, Void, ArrayList<Camera>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(true);
        }

        @Override
        protected ArrayList<Camera> doInBackground(Void... params) {
            arrayListCams = new ArrayList<Camera>();
            HTTPrequest httPrequest=new HTTPrequest(dataAuthentication, urlCam,sPref);
            String jsonString=httPrequest.getJSONString();
            return arrayListCams;
        }
    }

    private void doSomethingRepeatedly() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate( new TimerTask() {
            public void run() {

                try{
                    if (NetworkManager.isNetworkAvailable(getApplicationContext())) {
                        dataAuthentication.loadDataAuthentication(sPref);
                        new CamerasTask().execute();
                    }
                    else{
                    }

                }
                catch (Exception e) {
                    // TODO: handle exception
                }

            }
        }, 0, 5000);
    }

    private void showProgressDialog(boolean visible) {
        if (visible) {
            if (progressDialog == null || !progressDialog.isShowing()) {
                try {
                    progressDialog = new ProgressDialog(this, R.style.MyTheme);
                    progressDialog.setProgress(R.drawable.circular_progress_bar);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                progressDialog = null;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
