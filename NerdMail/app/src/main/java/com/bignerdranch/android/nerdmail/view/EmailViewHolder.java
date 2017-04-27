package com.bignerdranch.android.nerdmail.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bignerdranch.android.nerdmailservice.Email;

class EmailViewHolder extends RecyclerView.ViewHolder {

    private EmailListItemView mEmailListItemView;

    EmailViewHolder(final @NonNull View itemView) {
        super(itemView);
        mEmailListItemView = (EmailListItemView) itemView;
    }

    void bindEmail(final @NonNull Email email) {
        mEmailListItemView.setEmail(email);
    }
}
