package com.example.coronaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coronaapp.model.symptoms;
import com.example.coronaapp.viewholder.symptomsviewholder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CoronaSymptomsActivity extends AppCompatActivity {


    private DatabaseReference SymptomsRef;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    double clickcount=0;
    private FirebaseAuth mAuth;
    String currentUser;
    TextView Showpercentagetext;
    ProgressBar progressBar;
    private double initialdouble = 0;


    Button Locatebtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corona_symptoms);

        progressDialog =new ProgressDialog(CoronaSymptomsActivity.this);
        progressBar = findViewById(R.id.progress_horizontal);
        progressBar.setMax(100);

        mAuth= FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser().getUid();
        SymptomsRef = FirebaseDatabase.getInstance().getReference().child("symptom");



        recyclerView=  findViewById(R.id.symptoms);
        recyclerView.setLayoutManager(new LinearLayoutManager(CoronaSymptomsActivity.this));

        layoutManager = new LinearLayoutManager(CoronaSymptomsActivity.this,LinearLayoutManager.VERTICAL, false );
        recyclerView.setLayoutManager(layoutManager);




        Showpercentagetext =findViewById(R.id.showpercentage);
        Locatebtn =findViewById(R.id.locatebtn);

        Locatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoronaSymptomsActivity.this,DiagnosisCentersMapsActivity.class);
                startActivity(intent);
            }
        });


        LoadSymptoms();



    }

    private void LoadSymptoms() {

        progressDialog.setTitle("loading");
        progressDialog.setMessage("please wait..");
        progressDialog.show();


        FirebaseRecyclerOptions<symptoms> options =
                new FirebaseRecyclerOptions.Builder<symptoms>()
                        .setQuery(SymptomsRef,symptoms.class)
                        .build();



        FirebaseRecyclerAdapter<symptoms, symptomsviewholder> adapter =new FirebaseRecyclerAdapter<symptoms, symptomsviewholder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final symptomsviewholder holder, int position, @NonNull symptoms model)
            {

                holder.Symptomname.setText(model.getName());
                progressDialog.dismiss();


                holder.Confirmbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(CoronaSymptomsActivity.this, "Added to Symptoms", Toast.LENGTH_SHORT).show();
                        holder.Confirmbtn.setVisibility(View.GONE);
                        holder.Addedbuton.setVisibility(View.VISIBLE);


                        clickcount=clickcount+1;
                        if(clickcount>0)
                        {
                            double percentage = clickcount/6*100;
                            DatabaseReference ref =  FirebaseDatabase.getInstance().getReference().child("Users");

                            HashMap<String,Object> userMap = new HashMap<>();
                            userMap.put("symptomsnumber",percentage);
                            ref.child(currentUser).child("symptomscount").updateChildren(userMap);

                            GettheValueofsymptoms();

                        }

                    }
                });

            }

            @NonNull
            @Override
            public symptomsviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.symptoms_layout,viewGroup, false);
                symptomsviewholder holder = new symptomsviewholder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }





    private void GettheValueofsymptoms() {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Users").child(currentUser).child("symptomscount").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //This will loop through all items. Add variables to arrays or lists as required
                for (DataSnapshot snap : dataSnapshot.getChildren())
                {
                    String symptomscount = dataSnapshot.child("symptomsnumber").getValue().toString();


                    double x = Double.parseDouble(symptomscount);
                    int y = (int) x;


                    progressBar.setProgress(y);
                    Showpercentagetext.setText(y+"%");




                    double convertedcount;
                    try {
                        convertedcount = Double.parseDouble(symptomscount);

                        if (convertedcount >80)
                        {
                            Locatebtn.setVisibility(View.VISIBLE);
                            Locatebtn.setText("You may need urgent screening.Tap here to locate nearest center");
                        }



                    }
                    catch (NumberFormatException e)
                    {

                        Toast.makeText(CoronaSymptomsActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                    }




                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
