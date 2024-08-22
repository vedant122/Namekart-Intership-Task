package com.devduos.focustasks;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class rvHolderDateDisplay extends RecyclerView.Adapter<rvHolderDateDisplay.ViewHolder> {
    private Context context;
    private List<String> dates;
    private int selectedPosition = 0;

    public rvHolderDateDisplay(Context context,List<String>dates) {
        this.context=context;
        this.dates=dates;
    }

    @NonNull

    // for adding the layout inflater
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.rv_dates,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in));

        // Set the background and text color based on whether this item is selected
        if (position == selectedPosition) {
            holder.mv_mainDateContainer.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.selectedBackgroundColor)));  // Set your selected background color
            holder.tv_date.setTextColor(context.getResources().getColor(R.color.selectedTextColor));  // Set your selected text color
            holder.tv_day.setTextColor(context.getResources().getColor(R.color.selectedTextColor));  // Set your selected text color
        } else {
            holder.mv_mainDateContainer.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.defaultBackgroundColor)));  // Set your default background color
            holder.tv_date.setTextColor(context.getResources().getColor(R.color.defaultTextColor));  // Set your default text color
            holder.tv_day.setTextColor(context.getResources().getColor(R.color.defaultTextColor));  // Set your default text color
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update the selected position and notify the adapter
                notifyItemChanged(selectedPosition);  // Reset the previously selected item
                selectedPosition = holder.getAdapterPosition();  // Set the new selected item
                notifyItemChanged(selectedPosition);  // Refresh the new selected item
                MainActivity.refreshLayout(context, dates.get(selectedPosition));  // Update the main layout based on the selected date
            }
        });


        String dateString = dates.get(holder.getAdapterPosition());  // Example date
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy", Locale.ENGLISH);

        holder.tv_date.setText(dates.get(holder.getAdapterPosition()).substring(0, dates.get(holder.getAdapterPosition()).indexOf("/")));
        try {
            Date date = dateFormat.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            String dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(calendar.getTime());
            holder.tv_day.setText(dayOfWeek.substring(0, 3));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //for running the recyclerview for specific length
    @Override
    public int getItemCount() {
        return dates.size();
    }





    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_date,tv_day;
        MaterialCardView mv_mainDateContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_date=itemView.findViewById(R.id.tv_date);
            tv_day=itemView.findViewById(R.id.tv_day);
            mv_mainDateContainer=itemView.findViewById(R.id.mv_mainDateContainer);


        }
    }
}