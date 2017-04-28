package com.bignerdranch.android.animals.controller;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.android.animals.R;
import com.bignerdranch.android.animals.listener.PhotoSearchListener;
import com.bignerdranch.android.animals.model.GalleryItem;
import com.bignerdranch.android.animals.web.PhotoFetcher;
import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

public class AnimalListFragment extends Fragment implements PhotoSearchListener {
    RecyclerView mDogRecyclerView;
    PhotoFetcher mPhotoFetcher;
    List<GalleryItem> mGalleryItems;
    DogAdapter mDogAdapter;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_animal_list, container, false);

        mDogRecyclerView = (RecyclerView) view
                .findViewById(R.id.fragment_dog_RecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false);
        mDogRecyclerView.setLayoutManager(linearLayoutManager);

        mDogAdapter = new DogAdapter(getContext(), Collections.<GalleryItem>emptyList());
        mDogRecyclerView.setAdapter(mDogAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPhotoFetcher = PhotoFetcher.get();
        mPhotoFetcher.registerPhotoSearchListener(this);
        mPhotoFetcher.searchDogs();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPhotoFetcher.unregisterPhotoSearchListener(this);
    }

    @Override
    public void onPhotoSearchFinished() {
        mGalleryItems = mPhotoFetcher.getGalleryItems();
        mDogAdapter.setGalleryItems(mGalleryItems);
    }

    private class DogAdapter extends RecyclerView.Adapter<DogHolder> {
        private Context mContext;
        private List<GalleryItem> mGalleryItems;

        public DogAdapter(Context context, List<GalleryItem> galleryItems) {
            mContext = context;
            mGalleryItems = galleryItems;
        }

        public void setGalleryItems(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
            notifyDataSetChanged();
        }

        @Override
        public DogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.list_item_gallery, parent, false);
            return new DogHolder(view);
        }

        @Override
        public void onBindViewHolder(DogHolder holder, int position) {
            holder.bindGalleryItem(mGalleryItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    private class DogHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;
        TextView mTextView;
        GalleryItem mGalleryItem;

        public DogHolder(View itemView) {
            super(itemView);

            mImageView = (ImageView) itemView
                    .findViewById(R.id.list_item_gallery_image_view);
            mTextView = (TextView) itemView
                    .findViewById(R.id.list_item_gallery_text_view);
        }

        public void bindGalleryItem(GalleryItem galleryItem) {
            mGalleryItem = galleryItem;
            mTextView.setText(mGalleryItem.getCaption());
            Glide.with(mImageView.getContext())
                    .load(mGalleryItem.getUrl())
                    .fitCenter()
                    .into(mImageView);
        }
    }
}
