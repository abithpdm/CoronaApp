package com.example.coronaapp.viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.coronaapp.R;

public class symptomsviewholder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView Symptomname;
    public Button Confirmbtn,Addedbuton;


    public symptomsviewholder(@NonNull View itemView) {
        super(itemView);


        Symptomname = itemView.findViewById(R.id.symptomtext);
        Confirmbtn = itemView.findViewById(R.id.confirmbutton);
        Addedbuton = itemView.findViewById(R.id.added);


    }

    @Override
    public void onClick(View v) {

    }
}
