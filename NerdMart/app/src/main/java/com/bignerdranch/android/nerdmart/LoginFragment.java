package com.bignerdranch.android.nerdmart;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bignerdranch.android.nerdmart.databinding.FragmentLoginBinding;

public class LoginFragment extends NerdMartAbstractFragment {

    @Nullable
    @Override
    public View onCreateView(
            final @NonNull LayoutInflater inflater,
            final @Nullable ViewGroup container,
            final @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        final FragmentLoginBinding fragmentLoginBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_login,
                container,
                false);
        fragmentLoginBinding.setLoginButtonClickListener(v -> {
            String username = fragmentLoginBinding.fragmentLoginUsername
                    .getText().toString();
            String password = fragmentLoginBinding.fragmentLoginPassword
                    .getText().toString();
            addSubscription(mNerdMartServiceManager
                    .authenticate(username, password)
                    .compose(loadingTransformer())
                    .subscribe(authenticated -> {
                        if (!authenticated) {
                            Toast.makeText(getContext(),
                                    R.string.auth_failure_toast,
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(getContext(),
                                R.string.auth_success_toast,
                                Toast.LENGTH_SHORT).show();
                        Intent intent = ProductsActivity
                                .newIntent(getContext());
                        getActivity().finish();
                        startActivity(intent);
                    }));
        });
        return fragmentLoginBinding.getRoot();
    }
}
