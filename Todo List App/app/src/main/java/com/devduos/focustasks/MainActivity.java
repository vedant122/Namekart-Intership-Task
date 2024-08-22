package com.devduos.focustasks;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.harrywhewell.scrolldatepicker.DayScrollDatePicker;

import org.joda.time.DateTime;

public class MainActivity extends AppCompatActivity {
    static RecyclerView rv_taskDisplay, rv_dateDisplay;
    ImageView iv_createTask;

    static LinearLayout ll_notaskavailble;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ll_notaskavailble=findViewById(R.id.ll_notaskavailble);

        ll_notaskavailble.setVisibility(View.GONE);

        rv_dateDisplay = findViewById(R.id.rv_dateDisplay);
        rv_dateDisplay.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rv_taskDisplay = findViewById(R.id.rv_taskDisplay);
        rv_taskDisplay.setLayoutManager(new LinearLayoutManager(this));

        DB_taskSave db_taskSave = new DB_taskSave(this);
        Cursor cursor = db_taskSave.gettaskdata();
        DateTime dateTime = new DateTime();
        String datetime = dateTime.getDayOfMonth() + "/" + dateTime.getMonthOfYear() + "/" + dateTime.getYear();

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            List<String> dataList = getDateList(cursor.getString(2));
            rvHolderDateDisplay rvHolderDateDisplay = new rvHolderDateDisplay(this, dataList);
            rv_dateDisplay.setAdapter(rvHolderDateDisplay);
            refreshLayout(MainActivity.this, datetime);
        } else {
            String infoShown = "";
            try {
                Intent intent = getIntent();
                infoShown = intent.getStringExtra("infoShown");
            } catch (Exception e) {
            }

            if (infoShown == null) {
                startActivity(new Intent(getApplicationContext(), AppFirstStartingScreen.class));
                finish();
            }
            ll_notaskavailble.setVisibility(View.VISIBLE);
            List<String> dataList = getDateList(datetime);
            rvHolderDateDisplay rvHolderDateDisplay = new rvHolderDateDisplay(this, dataList);
            rv_dateDisplay.setAdapter(rvHolderDateDisplay);
        }


        iv_createTask = findViewById(R.id.iv_createTask);
        iv_createTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createBottomSheetDialog(MainActivity.this, false);
            }
        });


    }


    private void createBottomSheetDialog(Context context, boolean isForUpdate) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);

        dialog.setContentView(R.layout.task_create_update_bottomsheetdialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setDismissWithAnimation(true);

        EditText et_task = dialog.findViewById(R.id.et_task);
        EditText et_datatime = dialog.findViewById(R.id.et_datatime);
        ImageView iv_calender = dialog.findViewById(R.id.iv_calender);
        CheckBox checkbox_reminder = dialog.findViewById(R.id.checkbox_reminder);
        MaterialButton mb_createbutton = dialog.findViewById(R.id.mb_createbutton);

        final String[] s_date = {""};
        final String[] s_time = {""};
        final String[] s_reminder = {""};
        iv_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context);
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String date = day + "/" + (month + 1) + "/" + year;
                        et_datatime.setText(date);
                        datePickerDialog.dismiss();

                        if (!datePickerDialog.isShowing()) {
                            String datetime = et_datatime.getText().toString();
                            TimePickerDialog dialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                                    et_datatime.setText(datetime + " " + hours + ":" + minutes);
                                    s_date[0] = datetime;
                                    s_time[0] = hours + ":" + minutes;
                                }
                            }, 1, 0, false);
                            dialog.show();
                        }
                    }
                });
                datePickerDialog.show();


            }
        });

        checkbox_reminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(context, b + "", Toast.LENGTH_SHORT).show();
                s_reminder[0] = b + "";
            }
        });
        mb_createbutton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (et_task.getText().toString().equals("") || et_datatime.getText().toString().equals("") || !et_datatime.getText().toString().contains(" ")) {
                    Toast.makeText(context, "Please Enter The Task Information", Toast.LENGTH_SHORT).show();
                } else {
                    DB_taskSave db_taskSave = new DB_taskSave(getApplicationContext());
                    String id = "1";
                    String task = et_task.getText().toString();
                    String date = s_date[0];
                    String time = s_time[0];
                    String reminder = s_reminder[0];

                    DateTime dateTime = new DateTime();
                    String datetime1 = dateTime.getDayOfMonth() + "/" + dateTime.getMonthOfYear() + "/" + dateTime.getYear();


                    Cursor cursor = db_taskSave.gettaskdata();
                    if (cursor.getCount() > 0) {
                        int i = 1;
                        while (cursor.moveToNext()) {
                            i++;
                        }
                        id = i + "";
                        db_taskSave.inserttaskdata(id, task, date, time, reminder, "false");
                        refreshLayout(context, datetime1);
                        dialog.dismiss();
                    } else {
                        db_taskSave.inserttaskdata(id, task, date, time, reminder, "false");
                        refreshLayout(context, datetime1);
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.show();
    }


    public static List<String> getDateList(String startDate) {
        // Define the date format
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
        }

        // Parse the start date
        LocalDate start = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            start = LocalDate.parse(startDate, formatter);
        }

        // Create a list to store the dates
        List<String> dateList = new ArrayList<>();

        // Generate the dates for 100 days
        for (int i = 0; i <= 100; i++) {
            LocalDate currentDate = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                currentDate = start.plusDays(i);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dateList.add(currentDate.format(formatter));
            }
        }

        return dateList;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void refreshLayout(Context context, String selectedDate) {
        DB_taskSave db_taskSave = new DB_taskSave(context);
        Cursor cursor = db_taskSave.gettaskdata();

        List<Task> tasks = new ArrayList<>();

        while (cursor.moveToNext()) {
            if (selectedDate.equals(cursor.getString(2))) {
                String id = cursor.getString(0);
                String task = cursor.getString(1);
                String date = cursor.getString(2);
                String time = cursor.getString(3);
                String reminder = cursor.getString(4);
                String checked = cursor.getString(5);

                Task taskObj = new Task(id, task, date, time, reminder, checked);
                tasks.add(taskObj);
            }
        }

        // Sort the list based on time using LocalTime
        Collections.sort(tasks, new Comparator<Task>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public int compare(Task t1, Task t2) {
                return t1.getTime().compareTo(t2.getTime());
            }
        });

        // Extract sorted data to lists for the adapter
        List<String> id = new ArrayList<>();
        List<String> task = new ArrayList<>();
        List<String> date = new ArrayList<>();
        List<String> time = new ArrayList<>();
        List<String> reminder = new ArrayList<>();
        List<String> checked = new ArrayList<>();

        for (Task t : tasks) {
            id.add(t.id);
            task.add(t.task);
            date.add(t.date);
            time.add(t.time);
            reminder.add(t.reminder);
            checked.add(t.checked);
        }

        if (id.size() == 0) {
            rv_taskDisplay.setVisibility(View.GONE);
            ll_notaskavailble.setVisibility(View.VISIBLE);

        } else {
            ll_notaskavailble.setVisibility(View.GONE);
            rv_taskDisplay.setVisibility(View.VISIBLE);
            rvHolderTasks rvHolderTasks = new rvHolderTasks(context, id, task, date, time, reminder, checked);
            rv_taskDisplay.setAdapter(rvHolderTasks);
        }
    }

    public static class Task {
        String id;
        String task;
        String date;
        String time;
        String reminder;
        String checked;

        @RequiresApi(api = Build.VERSION_CODES.O)
        public Task(String id, String task, String date, String time, String reminder, String checked) {
            this.id = id;
            this.task = task;
            this.date = date;
            this.time = normalizeTimeFormat(time);
            this.reminder = reminder;
            this.checked = checked;
        }

        // Normalize time format to HH:mm
        @RequiresApi(api = Build.VERSION_CODES.O)
        private String normalizeTimeFormat(String time) {
            try {
                // Handle case where minutes might be single digit
                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("H:m");
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime localTime = LocalTime.parse(time, inputFormatter);
                return localTime.format(outputFormatter);
            } catch (DateTimeParseException e) {
                // If parsing fails, return a default value or handle error
                return "00:00";
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public LocalTime getTime() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return LocalTime.parse(time, formatter);
        }

        // Getters for all fields can be added here
    }


}