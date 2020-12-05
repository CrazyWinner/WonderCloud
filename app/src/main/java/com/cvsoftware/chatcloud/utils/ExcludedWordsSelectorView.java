package com.cvsoftware.chatcloud.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.cvsoftware.chatcloud.R;

import java.util.ArrayList;

public class ExcludedWordsSelectorView extends ExpandableCardView{

    public ChipRecyclerAdapter adapter;
    public ExcludedWordsSelectorView(Context c) {
        super(c);
    }

    @Override
    public @DrawableRes
    int getResId(){
        return R.layout.excludedwords;
    }
    @Override
    public void onCreateContentView(View view) {
        final ViewGroup excludedView = (ViewGroup) view;
        RecyclerView recyclerView = excludedView.findViewById(R.id.recyclerExcluded);
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChipRecyclerAdapter((TextView) excludedView.findViewById(R.id.emptyText));

        // specify an adapter (see also next example)
        recyclerView.setAdapter(adapter);
        excludedView.findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = excludedView.findViewById(R.id.editTextExcluded);
                adapter.addItem(editText.getText().toString().toLowerCase());
                editText.setText("");
            }
        });

    }
    @Override
    public void onCreatingInnerContent(TextView title, TextView desc, ImageView image){
        title.setText(R.string.excluded_words);
        desc.setText(R.string.desc_excluded_words);
        image.setImageResource(R.drawable.ic_blur_off_black_24dp);
    }

    public ExcludedWordsSelectorView(Context c, ExpandableCardView.AsyncCreateFinishedListener listener){
        super(c,listener);
    }


    public ArrayList<String> getItems(){
        return adapter.getItems();
    }


}
