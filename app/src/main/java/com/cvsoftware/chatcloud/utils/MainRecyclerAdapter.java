package com.cvsoftware.chatcloud.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cvsoftware.chatcloud.R;
import com.cvsoftware.chatcloud.SavedActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    View emptyView;
    Executor executor;
    private ArrayList<RecyclerObject> objects;
    MenuItem deleteItem;
    ArrayList<Integer> selectedOnes;
    boolean isSelecting;
    RecyclerView rv;
    SparseArray<Bitmap> cache;
    SparseArray<Long> cacheDate;

    public void deleteSelected(SQLHelper helper){
      for(int i : selectedOnes){
          RecyclerObject obje = objects.get(i);
          if(cache.get((int)obje.getId())!=null){
              cache.remove((int)obje.getId());
              cacheDate.remove((int)obje.getId());
          }
          Log.i("DELETELOG","deleting:"+i);
          Log.i("DELETELOG","deleting:"+obje.getName());

              File file = new File(Utils.getImagesFolder(rv.getContext()) +obje.getName() +".png");
             file.delete();
          File thumbFile = new  File(Utils.getImagesFolder(rv.getContext()) + ".thumbs/" + obje.getName() +".png");
          thumbFile.delete();
            helper.deleteObject(obje);
      }
      ArrayList<RecyclerObject> sil = new ArrayList<>();
      for(int i : selectedOnes){
          sil.add(objects.get(i));
      }
      objects.removeAll(sil);
     selectedOnes.clear();
     isSelecting = false;
      if(deleteItem!=null)
          deleteItem.setVisible(false);
      checkEmpty();
      notifyDataSetChanged();
    }

    public MainRecyclerAdapter(ArrayList<RecyclerObject> objects,View emptyView){
        this.objects = objects;
        selectedOnes = new ArrayList<>();
        cache = new SparseArray<>();
        cacheDate = new SparseArray<>();
        executor = Executors.newSingleThreadExecutor();
        this.emptyView = emptyView;
    }

    public void setDeleteItem(MenuItem menuItem){
        this.deleteItem = menuItem;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        rv = recyclerView;
    }

    private class MainViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView desc;
        ImageView image;
        View itemView;


        public MainViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.title);
            this.desc = itemView.findViewById(R.id.desc);
            this.image = itemView.findViewById(R.id.image);
            this.itemView = itemView;
        }
    }
    public void checkEmpty(){
        if(objects.size() == 0){emptyView.setVisibility(View.VISIBLE);}
        else{emptyView.setVisibility(View.INVISIBLE);}
    }

    public void clearSelected(){
        selectedOnes.clear();
        isSelecting = false;
        if(deleteItem!=null)
        deleteItem.setVisible(false);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final @NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mainrecyclerlayout,parent,false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = ((RecyclerView)parent).getChildAdapterPosition(v);

                    if(isSelecting){
                        if(selectedOnes.contains(position)){
                            selectedOnes.remove(position);
                        }else{
                            selectedOnes.add(position);
                        }
                        notifyItemChanged(position);
                        checkIfSelectionEnded();
                    }else{
                        ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) parent.getContext(),v.findViewById(R.id.image),"imageMain");
                        Intent i = new Intent(parent.getContext(), SavedActivity.class);
                        i.putExtra("id",objects.get(position).getId());
                        parent.getContext().startActivity(i,activityOptionsCompat.toBundle());
                    }



                Log.i("LONGPRESSLOG","CLICK");
            }
        });
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = ((RecyclerView)parent).getChildAdapterPosition(v);
                    selectedOnes.add(position);
                    notifyItemChanged(position);
                    isSelecting = true;
                    if(deleteItem!=null)
                        deleteItem.setVisible(true);

                Log.i("LONGPRESSLOG","LONGCLICK");
                return true;
            }
        });


        return new MainViewHolder(v);
    }

    private void checkIfSelectionEnded(){
        if(selectedOnes.size()>0){
            if(deleteItem != null)
            deleteItem.setVisible(true);
            isSelecting = true;
        }else{
            if(deleteItem != null)
                deleteItem.setVisible(false);
            isSelecting = false;
        }

    }

    public void setObjects(ArrayList<RecyclerObject> objects){
        this.objects = objects;
        notifyDataSetChanged();
    }
    private synchronized void putToCache(int position, Bitmap bitmap, File file){
        cache.put(position,bitmap);
        cacheDate.put(position,file.lastModified());
    }
    private void createThumb(final Context c, final int position, final File imgFile, final File thumbFile){
        File f = new File(Utils.getImagesFolder(rv.getContext()) + ".thumbs/");
        if(!f.exists()){
            f.mkdir();
        }
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                float multiplier = 200f / Math.max(myBitmap.getHeight(),myBitmap.getWidth());
                Bitmap resized = Bitmap.createScaledBitmap(myBitmap,(int)(myBitmap.getWidth() * multiplier),(int)(myBitmap.getHeight() * multiplier),false);
                try (FileOutputStream out = new FileOutputStream(thumbFile)) {
                    resized.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored9
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ((Activity)c).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyItemChanged(position);
                    }
                });
            }
        });

    }

    private void cacheBitmap(final Context c, final int position, final File thumbFile,final int id){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap myBitmap = BitmapFactory.decodeFile(thumbFile.getAbsolutePath());
                putToCache(id,myBitmap,thumbFile);
                ((Activity)c).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyItemChanged(position);
                    }
                });
            }
        });

    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        long a = System.currentTimeMillis();
        ((MainViewHolder)holder).title.setText(objects.get(position).getName());
        ((MainViewHolder)holder).desc.setText(objects.get(position).getName());
        File imgFile = new  File(Utils.getImagesFolder(rv.getContext()) + objects.get(position).getName() +".png");
        if(imgFile.exists()){
            File thumbFile = new  File(Utils.getImagesFolder(rv.getContext()) + ".thumbs/" + objects.get(position).getName() +".png");
            if(thumbFile.exists()){
                if(cache.get((int)objects.get(position).getId()) == null || thumbFile.lastModified()>cacheDate.get((int)objects.get(position).getId(), 0L)){
                    cacheBitmap(rv.getContext(),position,thumbFile,(int)objects.get(position).getId());
                }else{
                    if(!cache.get((int)objects.get(position).getId()).isRecycled())
                    ((MainViewHolder)holder).image.setImageBitmap(cache.get((int)objects.get(position).getId()));
                    Log.i("RECYCLERLOG","Got from cache");
                }
            }else{
                createThumb(rv.getContext(),position,imgFile,thumbFile);
            }
        }else{
            ((MainViewHolder)holder).image.setImageResource(R.drawable.ic_priority_high_black_24dp);

        }
      if(selectedOnes.contains(position)){
          ((MainViewHolder)holder).itemView.setBackgroundColor(rv.getResources().getColor(R.color.colorPrimaryDark));
      }else{
          ((MainViewHolder)holder).itemView.setBackgroundColor(Color.WHITE);
      }
      Log.i("RECYCLERLOG",""+(System.currentTimeMillis() - a) +"ms");
    }
    @Override
    public int getItemCount() {
        return objects.size();
    }
}
