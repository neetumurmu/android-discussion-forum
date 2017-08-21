package com.example.discussionforum.adapter;

/**
 * Created by neetu on 15/8/17.
 */
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.discussionforum.R;

import java.util.List;

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.MyViewHolder> {

    private List<Thread> threadList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView topic, body, author, date;

        public Typeface tf ;

        public String fontPath;

        public MyViewHolder(View view) {
            super(view);

            topic = (TextView) view.findViewById(R.id.topic);
            body = (TextView) view.findViewById(R.id.body);
            author = (TextView) view.findViewById(R.id.author);
            date = (TextView) view.findViewById(R.id.date);

        }
    }

    public ThreadAdapter(List<Thread> threadList){
        this.threadList = threadList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.thread_list_row , parent , false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder , int position){
        Thread thread  = threadList.get(position);

        holder.topic.setText(thread.getTopic());
        holder.body.setText(thread.getBody());
        holder.author.setText(thread.getAuthor());
        holder.date.setText(thread.getDate());
    }

    @Override
    public int getItemCount(){
        return threadList.size();
    }


}
