package com.eunji.mobile_st_unitas;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

//    private ArrayList<ImageItem> items = new ArrayList<>();
//    Context context;


//    public RecyclerAdapter(ArrayList<ImageItem> items, Context context) {
//        this.items = items;
//        this.context = context;
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        private ImageView image;
//
//        public ViewHolder(View v) {
//            super(v);
//            image =(ImageView) v.findViewById(R.id.imgView);
//        }
//    }
//
//
//    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
//
//        public ProgressBar progressBar;
//
//        public ProgressViewHolder(View v) {
//            super(v);
//            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
//        }
//
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
//    {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
////        v.setLayoutParams(new RecyclerView.LayoutParams(1000, 800));
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
//        DisplayMetrics dm = context.getResources().getDisplayMetrics();
//        int width = dm.widthPixels;
//        int resize = width*items.get(position).getHeight()/items.get(position).getWidth();
//
//        Glide.with(this.context)
//                .load(items.get(position).getImage())
//                .override(width,resize)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(((ViewHolder) viewHolder).image);
//    }
//
//    @Override
//    public int getItemCount()
//    {
//        return items.size();
//    }

    public void setItems(ArrayList<ImageItem> items) {
        this.items = items;
    }

    private final int ITEM = 0;
    private final int LOADING = 1;
    private LoadMoreItems onLoadMoreListener;
    private boolean isLoading;
    private Activity activity;
    private ArrayList<ImageItem> items;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public RecyclerAdapter(RecyclerView recyclerView, ArrayList<ImageItem> contacts, Activity activity) {
        this.items = contacts;
        this.activity = activity;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.LoadItems();
                    }
                    isLoading = true;
                }
            }
        });
    }

    public void setOnLoadMoreListener(LoadMoreItems mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }
    @Override
    public int getItemViewType(int position) {
        return items.get(position) == null ? LOADING : ITEM;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.image_item, parent, false);
            return new UserViewHolder(view);
        } else if (viewType == LOADING) {
            View view = LayoutInflater.from(activity).inflate(R.layout.loading, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserViewHolder) {
            UserViewHolder userViewHolder = (UserViewHolder) holder;

//            DisplayMetrics dm = activity.getApplicationContext().getResources().getDisplayMetrics();
//            int width = dm.widthPixels;
//            int resize = width*contacts.get(position).getHeight()/contacts.get(position).getWidth();

            Glide.with(activity.getApplicationContext())
                    .load(items.get(position).getImage())
//                    .override(width,resize)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(userViewHolder.imageView);

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }
    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }
    public void setLoaded() {
        isLoading = false;
    }
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }
    }
    private class UserViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public UserViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.imgView);
        }
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.image_item, parent, false);
        viewHolder = new UserViewHolder(v1);
        return viewHolder;
    }


}