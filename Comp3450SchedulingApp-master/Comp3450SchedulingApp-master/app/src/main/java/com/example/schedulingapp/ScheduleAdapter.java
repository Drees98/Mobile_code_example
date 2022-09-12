
package com.example.schedulingapp;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

//Sets up the cardview to be used in the main activity for the recycler view
class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.MyViewHolder> {
    static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView name;
        MyViewHolder(View itemView) {
            super(itemView);

            cv = itemView.findViewById(R.id.cv);
            name = itemView.findViewById(R.id.scheduleName);
        }
    }
    List<Schedule> schedule;
    ScheduleAdapter(List<Schedule> schedule){
        this.schedule = schedule;
    }

    @Override
    public int getItemCount() {
        if(schedule != null)
            return schedule.size();
        else
            return 0;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.schedule_card, viewGroup, false);

        MyViewHolder pvh = new MyViewHolder(v);
        return pvh;
    }
    @Override
    public void onBindViewHolder(MyViewHolder scheduleViewHolder, int i) {
        scheduleViewHolder.name.setText(schedule.get(i).getName());
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

