package com.example.coronaapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.coronaapp.CoronaSymptomsActivity;
import com.example.coronaapp.DiagnosisCentersMapsActivity;
import com.example.coronaapp.R;
import com.example.coronaapp.TrackInteractionsMapsActivity;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final LinearLayout Areasreportedlayout = root.findViewById(R.id.areareported);
        Areasreportedlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(getContext(), DiagnosisCentersMapsActivity.class);
                startActivity(intent);

            }
        });



        final LinearLayout Diagnosticcenters = root.findViewById(R.id.diagnosticcenters);
        Diagnosticcenters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(getContext(), DiagnosisCentersMapsActivity.class);
                startActivity(intent);

            }
        });



        final LinearLayout Symptomslayout = root.findViewById(R.id.symptomschecker);
        Symptomslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(getContext(), CoronaSymptomsActivity.class);
                startActivity(intent);

            }
        });


        final LinearLayout Trackme = root.findViewById(R.id.trackme);
        Trackme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(getContext(), TrackInteractionsMapsActivity.class);
                startActivity(intent);

            }
        });


        return root;
    }
}

