package com.cvsoftware.chatcloud.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cvsoftware.chatcloud.R;
import com.robertlevonyan.views.chip.Chip;
import com.robertlevonyan.views.chip.OnCloseClickListener;

import java.util.ArrayList;

public class ChipRecyclerAdapter extends RecyclerView.Adapter<ChipRecyclerAdapter.MyViewHolder> {
    private ArrayList<String> items;
    private TextView emptyText;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public Chip chip;
        public MyViewHolder(Chip c) {
            super(c);
            chip = c;
        }
    }

   public ChipRecyclerAdapter(TextView emptyText){
        items = new ArrayList<>();
        this.emptyText = emptyText;
   }

    // Create new views (invoked by the layout manager)
    @Override
    public ChipRecyclerAdapter.MyViewHolder onCreateViewHolder(final ViewGroup parent,
                                                               int viewType) {
        // create a new view
        Chip v = (Chip) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chip1, parent, false);
        final MyViewHolder vh = new MyViewHolder(v);
        v.setOnCloseClickListener(new OnCloseClickListener() {
            @Override
            public void onCloseClick(View v) {
                int i = ((RecyclerView)parent).getChildAdapterPosition(v);
                if(i > -1){
                items.remove(i);
                notifyItemRemoved(i);
                if(getItemCount()==0)emptyText.setVisibility(View.VISIBLE);
                }
            }
        });

        return vh;
    }
    public ArrayList<String> getItems(){
        return items;
    }
    public void addItem(String ekle){
        items.add(ekle);
        emptyText.setVisibility(View.INVISIBLE);
        notifyItemInserted(getItemCount());
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.chip.setText(items.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }
}