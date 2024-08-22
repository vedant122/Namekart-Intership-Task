package com.devduos.focustasks;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import org.joda.time.DateTime;

import java.util.List;
import java.util.MissingFormatArgumentException;


public class rvHolderTasks extends RecyclerView.Adapter<rvHolderTasks.ViewHolder> {
    private Context context;
    private List<String> id, task, date, time, reminder, checked;

    public rvHolderTasks(Context context, List<String> id, List<String> task, List<String> date, List<String> time, List<String> reminder, List<String> checked) {
        this.context = context;
        this.id = id;
        this.task = task;
        this.date = date;
        this.time = time;
        this.reminder = reminder;
        this.checked = checked;
    }

    @NonNull

    // for adding the layout inflater
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rv_task_items, parent, false);
        return new ViewHolder(view);
    }

    //adding the data to the files
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String sid = id.get(holder.getAdapterPosition());
        String stask = task.get(holder.getAdapterPosition());
        String sdate = date.get(holder.getAdapterPosition());
        String stime = time.get(holder.getAdapterPosition());
        String sreminder = reminder.get(holder.getAdapterPosition());
        String schecked = checked.get(holder.getAdapterPosition());

        holder.tv_task.setText(stask);
        holder.tv_datetime.setText(sdate + " " + stime);

        if (schecked.equals("true")) {
            holder.cb_checkbox.setChecked(true);
        } else {
            holder.cb_checkbox.setChecked(false);
        }
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));

        holder.cb_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                DB_taskSave db_taskSave = new DB_taskSave(context);
                db_taskSave.updatetaskdata(sid, stask, sdate, stime, sreminder, b + "");
            }
        });

        holder.mv_mainTaskContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                et_task.setText(stask);
                et_datatime.setText(sdate + " " + stime);


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


                if (sreminder.equals("true")) {
                    checkbox_reminder.setChecked(true);
                } else {
                    checkbox_reminder.setChecked(false);
                }

                mb_createbutton.setText("Update");
                mb_createbutton.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(View view) {
                        if (et_task.getText().toString().equals("") || et_datatime.getText().toString().equals("") || !et_datatime.getText().toString().contains(" ")||s_date[0].equals("")||s_time[0].equals("")) {
                            Toast.makeText(context, "Renter All Details", Toast.LENGTH_SHORT).show();
                        } else {
                            DB_taskSave db_taskSave = new DB_taskSave(context);
                            String task = et_task.getText().toString();
                            String date = s_date[0];
                            String time = s_time[0];

                            DateTime dateTime = new DateTime();
                            String datetime = dateTime.getDayOfMonth() + "/" + dateTime.getMonthOfYear() + "/" + dateTime.getYear();

                            Cursor cursor = db_taskSave.gettaskdata();
                            if (cursor.getCount() > 0) {
                                while (cursor.moveToNext()) {
                                    if (cursor.getString(0).equals(sid)) {
                                        db_taskSave.updatetaskdata(sid, task, date, time, checkbox_reminder.isChecked() + "", holder.cb_checkbox.isChecked() + "");
                                        Toast.makeText(context, "Task Update", Toast.LENGTH_SHORT).show();
                                        MainActivity.refreshLayout(context, datetime);
                                        dialog.dismiss();
                                    }
                                }
                            }
                        }
                    }
                });
                dialog.show();
            }
        });




        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_delete);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.round_corners));

                MaterialButton mb_cancel=dialog.findViewById(R.id.mb_cancel);
                MaterialButton mb_delete=dialog.findViewById(R.id.mb_delete);
                ImageView iv_crossButton=dialog.findViewById(R.id.iv_crossButton);

                iv_crossButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                mb_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                mb_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DB_taskSave db_taskSave=new DB_taskSave(context);
                        db_taskSave.deletetaskdata(sid);
                        MainActivity.refreshLayout(context,sdate);
                        Toast.makeText(context,"Task Deleted",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });



                dialog.show();
                return true;
            }
        });

    }

    //for running the recyclerview for specific length
    @Override
    public int getItemCount() {
        return id.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView mv_mainTaskContainer;
        TextView tv_task, tv_datetime;
        CheckBox cb_checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            mv_mainTaskContainer = itemView.findViewById(R.id.mv_mainTaskContainer);
            tv_task = itemView.findViewById(R.id.tv_task);
            tv_datetime = itemView.findViewById(R.id.tv_datetime);
            cb_checkbox = itemView.findViewById(R.id.cb_checkbox);
        }
    }
}