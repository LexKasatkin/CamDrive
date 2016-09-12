package com.camdrive;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.squareup.timessquare.CalendarPickerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lex on 03.05.16.
 */

class RVAdapterCameras extends RecyclerView.Adapter<RVAdapterCameras.ContractsViewHolder>{
    SharedPreferences sPref;
    public String MY_PREF = "MY_PREF";
    DataAuthentication dataAuthentication;
    String urlDates="http://95.170.177.173/mobile/api_native/calendar/?camera_channel_id=";
    ArrayList<Day>daysArray;
    Dialog dialog;
    RadioButton rb;
    RadioGroup rg;
    ProgressDialog progressDialog;
    int sdvig;
    public static class ContractsViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        Context ctx;
        TextView contractName;
        TextView contractType;
        TextView contractStatusName;
        TextView contractDate;
        TextView contractID;
        ContractsViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            contractID=(TextView)itemView.findViewById(R.id.tvObjectID);
            contractName = (TextView)itemView.findViewById(R.id.tvObjectName);
            contractType = (TextView)itemView.findViewById(R.id.tvContractName);
            contractDate=(TextView)itemView.findViewById(R.id.tvContractID);
            contractStatusName=(TextView)itemView.findViewById(R.id.tvContractStatusName);
            ctx=itemView.getContext();
            contractID.setTextColor(Color.WHITE);
            contractName.setTextColor(ctx.getResources().getColor(R.color.colorPrimaryText));
            contractStatusName.setTextColor(ctx.getResources().getColor(R.color.colorPrimaryText));
            contractType.setTextColor(ctx.getResources().getColor(R.color.colorPrimaryText));
            contractDate.setTextColor(ctx.getResources().getColor(R.color.colorPrimaryText));

        }
    }


    ArrayList<Camera> cameras;
    RVAdapterCameras(ArrayList<Camera> cameras){
        this.cameras = cameras;
    }


    @Override
    public int getItemCount() {
        return cameras.size();
    }


    @Override
    public ContractsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.object_context,  viewGroup, false);
        ContractsViewHolder pvh = new ContractsViewHolder(v);
        return pvh;
    }


    @Override
    public void onBindViewHolder(final ContractsViewHolder personViewHolder, int i) {
        if(cameras.get(i).camera_channel_id!=null) {
            personViewHolder.contractID.setText(cameras.get(i).camera_channel_id);
        }
        if(cameras.get(i).camera_name!=null) {
            personViewHolder.contractName.setText("Название камеры " + cameras.get(i).camera_name);
        }
        if(cameras.get(i).camera_connected_server!=null) {
            if(cameras.get(i).camera_connected_server.equals("false")) {
                personViewHolder.contractType.setText("Камера подключена к серверу");
            }else if(cameras.get(i).camera_connected_server.equals("true")) {
                personViewHolder.contractType.setText("Камера к серверу не подключена");
            }
        }
        if(cameras.get(i).archive!=null) {
            if(cameras.get(i).archive.equals("false")) {
                personViewHolder.contractStatusName.setText("У камеры нет архива");
            }else if(cameras.get(i).archive.equals("true")) {
                personViewHolder.contractStatusName.setText("Камера содержит архив");
            }
            }
//        if(cameras.get(i).stream_url!=null) {
//            personViewHolder.contractDate.setText(cameras.get(i).stream_url);
//        }

        personViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id=personViewHolder.contractID.getText().toString();
                final Context context = v.getContext();
                ArrayList<String> years=new ArrayList<String>();
                years.add("2014");
                years.add("2015");
                years.add("2016");
                years.add("2017");
                showRadioButtonDialog(years, context);
                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (dialog.findViewById(rg.getCheckedRadioButtonId()) != null) {

                            int j = rg.indexOfChild(dialog.findViewById(rg.getCheckedRadioButtonId()));
                            if(j==0){
                                sdvig=-24;
                            }else if(j==1){
                                sdvig=-12;
                            }else if(j==2){
                                sdvig=0;
                            }else if(j==3){
                                sdvig=12;
                            }
                            dialog.dismiss();
                            ArrayList<String> months=new ArrayList<String>();
                            months.add("Jan");
                            months.add("Feb");
                            months.add("Mar");
                            months.add("Apr");
                            months.add("May");
                            months.add("Jun");
                            months.add("Jul");
                            months.add("Aug");
                            months.add("Sep");
                            months.add("Oct");
                            months.add("Nov");
                            months.add("Dec");
                            showRadioButtonDialog(months, context);
                            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {
                                    if (dialog.findViewById(rg.getCheckedRadioButtonId()) != null) {
                                        int i = rg.indexOfChild(dialog.findViewById(rg.getCheckedRadioButtonId()));
                                        Calendar cal = Calendar.getInstance();
                                        int month = cal.get(Calendar.MONTH);
                                        urlDates=urlDates+id+"&position="+String.valueOf(i-month+sdvig);
                                        DatesTask datesTask=new DatesTask(context);
                                        datesTask.execute();
                                        dialog.dismiss();
                                    } else {
                                        dialog.dismiss();
                                    }
                                }
                            });
                        }else{
                            dialog.dismiss();
                        }
                    }});



            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    class DatesTask extends AsyncTask<Void, Void, String> {
        Context ctx;

        public DatesTask(Context context) {
            ctx=context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(true, ctx);
            sPref = ctx.getSharedPreferences(MY_PREF, Activity.MODE_PRIVATE);
            dataAuthentication=new DataAuthentication();
            dataAuthentication.loadDataAuthentication(sPref);
        }

        @Override
        protected String doInBackground(Void... params) {

            HTTPrequest httpRequest=new HTTPrequest(dataAuthentication, urlDates ,sPref);
            String jsonString = httpRequest.getJSONString();
            return jsonString;
        }

        @Override
        protected void onPostExecute(final String success) {
            daysArray=new ArrayList<Day>();
            try {
                if(success!=null) {
                    JSONObject jsonObject = new JSONObject(success);
//                    JSONObject jsonDays=jsonObject.getJSONObject("days");
                    JSONArray arrayList=jsonObject.getJSONArray("days");
                    if(arrayList!=null){
                        for(int i=0;i<arrayList.length();i++){
                            if(arrayList.get(i)!=null){
                                JSONObject JSONday=arrayList.getJSONObject(i);
                                Day day=new Day();
                                if(JSONday.has("id")) {
                                    day.setId(JSONday.getString("id"));
                                }
                                if(JSONday.has("text")){
                                    day.setText(JSONday.getString("text"));
                                }
                                if (JSONday.has("enable")){
                                    day.setEnable(Boolean.valueOf(JSONday.getString("enable")));
                                }
                                if(JSONday.has("records")){
                                    day.setRecords(Boolean.valueOf(JSONday.getString("records")));
                                }
                                daysArray.add(day);
                            }
                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Date date;
            CalendarPickerView calendar1;
            Dialog dialogDays=new Dialog(ctx);
            dialogDays.setContentView(R.layout.calendar_dialog);
            calendar1 = (CalendarPickerView) dialogDays.findViewById(R.id.calendar_view);
            boolean j=false;
            for(int i=0;i<daysArray.size();i++) {
                if (daysArray.get(i).records) {
                    String parts[] = daysArray.get(i).id.split("-");
                    int day = Integer.parseInt(parts[2]);
                    int month = Integer.parseInt(parts[1]);
                    int year = Integer.parseInt(parts[0]);

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month - 1);
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    date = new Date(calendar.getTimeInMillis());
                    if(j==false) {
                        Calendar nextYear = Calendar.getInstance();
                        nextYear.add(Calendar.YEAR, 1);
                        calendar1.init(date, nextYear.getTime())
                                .inMode(CalendarPickerView.SelectionMode.MULTIPLE);
                        j=true;
                    }
                    calendar1.selectDate(date);
                }
            }
           if(j) {
                dialogDays.show();
            }else{
               Toast toast = Toast.makeText(ctx, "У камеры нет архива", Toast.LENGTH_SHORT);
               toast.show();
           }
            showProgressDialog(false, ctx);

        }
    }

    private void showRadioButtonDialog(ArrayList<String> arrayList, Context ctx) {
        dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_month);
//        ivSearchOK=(ImageView)dialog.findViewById(R.id.ivSearchOK);

        ArrayList<String> stringList=new ArrayList<>();  // here is list

        rg = (RadioGroup) dialog.findViewById(R.id.radio_group);
        for(int i=0;i<arrayList.size();i++){
            rb=new RadioButton(ctx); // dynamically creating RadioButton and adding to RadioGroup.
//            rb.setButtonTintList(ColorStateList.valueOf(R.color.colorAccent));
//            rb.setHighlightColor(R.color.colorAccent);
            rb.setText(arrayList.get(i).toString());
            rg.addView(rb);
        }

        dialog.show();
    }


    private void showProgressDialog(boolean visible, Context cont) {
        if (visible) {
            if (progressDialog == null || !progressDialog.isShowing()) {
                try {
                    progressDialog = new ProgressDialog(cont, R.style.MyTheme);
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
