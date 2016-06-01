package com.example.padmini.newmaterialdesign;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by padmini on 3/4/2016.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private final LayoutInflater inflater;
    List<Information> data= Collections.emptyList();
    private Context context;
    private ClickListener clickListener;


    public CustomAdapter(Context context,List<Information> data)
    {
        this.context=context;
        inflater = LayoutInflater.from(context);
        this.data=data;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view=inflater.inflate(R.layout.custom_row,parent,false);
        Log.d("CustomAdapter","onCreateViewHolder is called");
        MyViewHolder holder=new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Information current=data.get(position);
        Log.d("CustomAdapter","onBindViewHolder is called"+position);
        holder.title.setText(current.title);
        holder.icon.setImageResource(current.iconId);

    }

    public void setClickListener(ClickListener clickListener)
    {
        this.clickListener=clickListener;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title;
        ImageView icon;
        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title= (TextView) itemView.findViewById(R.id.listText);
            icon= (ImageView) itemView.findViewById(R.id.listIcon);
        }

        @Override
        public void onClick(View v) {
            if(clickListener!=null)
            {
                clickListener.itemClicked(v,getAdapterPosition());
            }
        }
    }
    public interface ClickListener{
        public void itemClicked(View view,int recyclerItemPosition);
    }

}
