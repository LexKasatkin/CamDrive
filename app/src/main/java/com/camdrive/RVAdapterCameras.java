package com.camdrive;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.CalendarView;
import android.widget.DatePicker;
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
    public static class ContractsViewHolder extends RecyclerView.ViewHolder {
        Context ctx;
        CardView cv;
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
                String id=personViewHolder.contractID.getText().toString();
                Context context = v.getContext();
                urlDates=urlDates+id+"&position=";
                        DatesTask datesTask=new DatesTask(context);
                datesTask.execute();
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
            Dialog dialog=new Dialog(ctx);
            dialog.setContentView(R.layout.calendar_dialog);
            CalendarPickerView calendarView = (CalendarPickerView) dialog.findViewById(R.id.calendar_view);
            for(int i=0;i<daysArray.size();i++) {
                String parts[] = daysArray.get(i).id.split("-");

                int day = Integer.parseInt(parts[2]);
                int month = Integer.parseInt(parts[1]);
                int year = Integer.parseInt(parts[0]);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month-1);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                Date date=new Date(calendar.getTimeInMillis());
                calendarView.init(date, date);
                long milliTime = calendar.getTimeInMillis();
                if(i==0){

                }
                if(i==daysArray.size()-1) {
                }
//                CalendarView calendarView=datePicker.getCalendarView();
//                calendarView.setDate(milliTime);
            }
//            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//
//                @Override
//                public void onSelectedDayChange(CalendarView view, int year,
//                                                int month, int dayOfMonth) {
//                    int mYear = year;
//                    int mMonth = month;
//                    int mDay = dayOfMonth;
//                    String selectedDate = new StringBuilder().append(mMonth + 1)
//                            .append("-").append(mDay).append("-").append(mYear)
//                            .append(" ").toString();
//                    Toast.makeText(ctx.getApplicationContext(), selectedDate, Toast.LENGTH_LONG).show();
//
//                }
//            });
            dialog.show();
        }
    }
    public void showDialogCalendar(){

    }
}
