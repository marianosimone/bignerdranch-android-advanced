package com.bignerdranch.android.animals.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bignerdranch.android.animals.R;

public class AnimalFragment extends Fragment {

    private Button mViewAnimalButton;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_animal, container, false);

        mViewAnimalButton = (Button)
                view.findViewById(R.id.fragment_animal_view_button);
        mViewAnimalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AnimalListActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
