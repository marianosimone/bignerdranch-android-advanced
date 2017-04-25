package com.bignerdranch.android.nerdmart;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends NerdMartAbstractFragment {

    @Bind(R.id.fragment_login_username)
    EditText mUsernameEditText;

    @Bind(R.id.fragment_login_password)
    EditText mPasswordEditText;

    @Nullable
    @Override
    public View onCreateView(
            final @NonNull LayoutInflater inflater,
            final @Nullable ViewGroup container,
            final @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.fragment_login_login_button)
    public void handleLoginClick() {
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        addSubscription(
                mNerdMartServiceManager
                        .authenticate(username, password)
                        .compose(loadingTransformer())
                        .subscribe(authenticated -> {
                            Toast.makeText(getContext(),
                                    R.string.auth_success_toast,
                                    Toast.LENGTH_SHORT).show();
                            final Intent intent = ProductsActivity.newIntent(getContext());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            getActivity().finish();
                        }));
    }
}
