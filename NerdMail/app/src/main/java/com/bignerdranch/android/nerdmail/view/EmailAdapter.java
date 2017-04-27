package com.bignerdranch.android.nerdmail.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.android.nerdmail.R;
import com.bignerdranch.android.nerdmailservice.Email;

import java.util.List;

public class EmailAdapter extends RecyclerView.Adapter<EmailViewHolder> {

    private List<Email> mEmails;
    private Context mContext;

    public EmailAdapter(final @NonNull Context context, final @NonNull List<Email> emails) {
        mContext = context;
        mEmails = emails;
    }

    @Override
    public EmailViewHolder onCreateViewHolder(final @NonNull ViewGroup parent, final int viewType) {
        View view = new EmailListItemView(mContext);
        return new EmailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NonNull EmailViewHolder holder, final int position) {
        holder.bindEmail(mEmails.get(position));
    }

    @Override
    public int getItemCount() {
        return mEmails.size();
    }
}
