package com.example.coronaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    TextView RegisterLink;
    private Button Customerloginbtn;
    private EditText Emailcustomer,Passwordcustomer;
    private ProgressDialog loadingbar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        RegisterLink = findViewById(R.id.registerlink);
        Emailcustomer=findViewById(R.id.emailcustomerlogin);
        Passwordcustomer=findViewById(R.id.passwordcustomerlogin);
        Customerloginbtn = findViewById(R.id.loginbutton);
        loadingbar=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();


        Customerloginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String email=Emailcustomer.getText().toString();
                String password=Passwordcustomer.getText().toString();

                SignInCustomer(email,password);

            }
        });


        RegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this,RegisterUserActivity.class);
                startActivity(intent);

            }
        });





    }

    private void SignInCustomer(String email, String password) {

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(MainActivity.this,"Please enter your email",Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(MainActivity.this,"Please enter your password",Toast.LENGTH_SHORT).show();
        }else
        {
            loadingbar.setTitle("SIGNING IN USER");
            loadingbar.setMessage("please wait..");
            loadingbar.show();


            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {

                        Intent customerIntent=new Intent(MainActivity.this,HomeActivity.class);
                        startActivity(customerIntent);
                        Toast.makeText(MainActivity.this,"signed in successfully ",Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();


                    }

                    else
                    {
                        Toast.makeText(MainActivity.this,"Sign in Unsuccessful..please check your details and  try again",Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();

                    }

                }
            });
        }
    }
}
