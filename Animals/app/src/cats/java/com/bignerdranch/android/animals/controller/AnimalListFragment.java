package com.bignerdranch.android.animals.controller;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bignerdranch.android.animals.R;
import com.bignerdranch.android.animals.listener.PhotoSearchListener;
import com.bignerdranch.android.animals.model.GalleryItem;
import com.bignerdranch.android.animals.web.PhotoFetcher;
import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

public class AnimalListFragment extends Fragment implements PhotoSearchListener{

    RecyclerView mCatRecyclerView;
    PhotoFetcher mPhotoFetcher;
    List<GalleryItem> mGalleryItems;
    CatAdapter mCatAdapter;

    @Nullable
    @Override
    public View onCreateView
            (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_animal_list, container, false);

        mCatRecyclerView = (RecyclerView)
                view.findViewById(R.id.fragment_cat_RecyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        mCatRecyclerView.setLayoutManager(gridLayoutManager);

        mCatAdapter = new CatAdapter(getContext(), Collections.<GalleryItem>emptyList());
        mCatRecyclerView.setAdapter(mCatAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPhotoFetcher = PhotoFetcher.get();
        mPhotoFetcher.registerPhotoSearchListener(this);
        mPhotoFetcher.searchCats();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPhotoFetcher.unregisterPhotoSearchListener(this);
    }

    @Override
    public void onPhotoSearchFinished() {
        mGalleryItems = mPhotoFetcher.getGalleryItems();
        mCatAdapter.setGalleryItems(mGalleryItems);
    }

    private class CatAdapter extends RecyclerView.Adapter<CatHolder> {
        private Context mContext;
        private List<GalleryItem> mGalleryItems;

        public CatAdapter(Context context, List<GalleryItem> galleryItems) {
            mContext = context;
            mGalleryItems = galleryItems;
        }

        public void setGalleryItems(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
            notifyDataSetChanged();
        }

        @Override
        public CatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.list_item_gallery, parent, false);
            return new CatHolder(view);
        }

        @Override
        public void onBindViewHolder(CatHolder holder, int position) {
            holder.bindGalleryItem(mGalleryItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class CatHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;

        public CatHolder(View itemView) {
            super(itemView);

            mImageView = (ImageView)
                    itemView.findViewById(R.id.fragment_photo_gallery_image_view);
        }

        public void bindGalleryItem(GalleryItem galleryItem) {
            Glide.with(mImageView.getContext())
                    .load(galleryItem.getUrl())
                    .into(mImageView);
        }
    }
}
