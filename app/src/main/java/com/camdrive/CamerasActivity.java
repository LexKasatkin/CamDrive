package com.camdrive;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CamerasActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    ArrayList<Camera> arrayListCams;
    Camera camera;
    SharedPreferences sPref;
    public String MY_PREF = "MY_PREF";
    DataAuthentication dataAuthentication;
    String urlCam="http://95.170.177.173/mobile/api_native/cameras";
    String urlLogout="http://95.170.177.173/mobile/api_native/logout";
    CamerasTask camerasTask;
    Timer timer;
    String TAG="level";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cameras);
        getSupportActionBar().setTitle("Список камер");
        sPref = this.getSharedPreferences(MY_PREF, Activity.MODE_PRIVATE);
        dataAuthentication=new DataAuthentication();
        doSomethingRepeatedly();

    }

    class CamerasTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            HTTPrequest httPrequest=new HTTPrequest(dataAuthentication, urlCam,sPref);
            String jsonString=httPrequest.getJSONString();
            return jsonString;
        }
        @Override
        protected void onPostExecute(final String success) {
            arrayListCams=new ArrayList<Camera>();
            if(success!=null){
                try {
                    JSONObject jsonObject=new JSONObject(success);
                    if(jsonObject.has("status")){
                        if(jsonObject.getString("status").equals("1")){
                            if(jsonObject.has("data")){
                                JSONObject jsonObjectData=jsonObject.getJSONObject("data");
                                if(jsonObjectData.has("cameras")){
                                    JSONArray jsonArrayCameras=jsonObjectData.getJSONArray("cameras");
                                    for(int i=0;i<jsonArrayCameras.length();i++){
                                        camera=new Camera();
                                        JSONObject JSONObjectCamera=jsonArrayCameras.getJSONObject(i);
                                        if(JSONObjectCamera.has("camera_channel_id")){
                                            camera.setCamera_channel_id(JSONObjectCamera.getString("camera_channel_id"));
                                        }
                                        if(JSONObjectCamera.has("camera_name")){
                                            camera.setCamera_name(JSONObjectCamera.getString("camera_name"));
                                        }
                                        if(JSONObjectCamera.has("camera_connected_server")){
                                            camera.setCamera_connected_server(JSONObjectCamera.getString("camera_connected_server"));
                                        }
                                        if(JSONObjectCamera.has("preview_url")){
                                            camera.setPreview_url(JSONObjectCamera.getString("preview_url"));
                                        }
                                        if(JSONObjectCamera.has("stream_url")){
                                            camera.setStream_url(JSONObjectCamera.getString("stream_url"));
                                        }
                                        if(JSONObjectCamera.has("archive")){
                                            camera.setArchive(JSONObjectCamera.getString("archive"));
                                        }
                                        arrayListCams.add(camera);
                                    }
                                }
                            }
                        }else if(jsonObject.getString("status").equals("0")){
                            Toast toast = Toast.makeText(CamerasActivity.this, "Ошибка запроса списка камер", Toast.LENGTH_SHORT);
                            toast.show();
                        }else if(jsonObject.getString("status").equals("2")){
                            Toast toast = Toast.makeText(CamerasActivity.this, "Не авторизованный запрос" +
                                    ", необходимо авторизоваться", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            RVAdapterCameras rvAdapterCameras=new RVAdapterCameras(arrayListCams);
            RecyclerView rvCameras=(RecyclerView)findViewById(R.id.rvCameras);
            rvCameras.setAdapter(rvAdapterCameras);
            LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            rvCameras.setLayoutManager(llm);
            rvCameras.setHasFixedSize(true);

        }
    }

    private void doSomethingRepeatedly() {
        timer = new Timer();
        timer.scheduleAtFixedRate( new TimerTask() {
            public void run() {

                try{
                    if (NetworkManager.isNetworkAvailable(getApplicationContext())) {
                        dataAuthentication.loadDataAuthentication(sPref);
                        camerasTask=new CamerasTask();
                        camerasTask.execute();
                    }
                    else{
                    }

                }
                catch (Exception e) {
                    // TODO: handle exception
                }

            }
        }, 0, 10000);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
                this.onBackPressed();
                this.finish();
                return true;
            case R.id.logout:
                LogoutTask logoutTask=new LogoutTask();
                logoutTask.execute();
                camerasTask.cancel(true);
                timer.cancel();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cameras, menu);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        finish();
        camerasTask.cancel(true);
        timer.cancel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "MainActivity: onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity: onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "MainActivity: onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "MainActivity: onStop()");
        camerasTask.cancel(true);
        timer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MainActivity: onDestroy()");
        camerasTask.cancel(true);
        timer.cancel();
    }

    class LogoutTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(true);
        }

        @Override
        protected String doInBackground(Void... params) {
            HTTPrequest httPrequest = new HTTPrequest(dataAuthentication, urlLogout, sPref);
            String jsonString = httPrequest.getJSONString();
            return jsonString;
        }

        @Override
        protected void onPostExecute(final String success) {
        }
    }
}
