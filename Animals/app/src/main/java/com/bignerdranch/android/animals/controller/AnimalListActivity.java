package com.bignerdranch.android.animals.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bignerdranch.android.animals.R;

public class AnimalListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_list);

        if (savedInstanceState == null) {
            AnimalListFragment animalListFragment = new AnimalListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, animalListFragment)
                    .commit();
        }
    }
}
