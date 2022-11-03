package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class signUp extends AppCompatActivity {
    EditText sign_up_mail;
    TextView text;
    EditText sign_up_pass;
    Button sign_up_bt;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);
        sign_up_mail = findViewById(R.id.su_mail);
        sign_up_pass = findViewById(R.id.su_pass);
        sign_up_bt = findViewById(R.id.su_button);
        text = findViewById(R.id.su_text);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signUp.this, MainActivity.class));
            }
        });
        sign_up_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = sign_up_mail.getText().toString();
                String pass = sign_up_pass.getText().toString();
//                String com_pass = Confirm_pass.getText().toString();

                if(email.isEmpty()){
                    sign_up_mail.setError("Email is required!");
                    sign_up_mail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    sign_up_mail.setError("Enter a correct mail");
                    sign_up_mail.requestFocus();
                    return;
                }
                if (pass.length()<6){
                    sign_up_pass.setError("Min password lenght should be 6 characters");
                    sign_up_pass.requestFocus();
                    return;
                }
                if (!email.isEmpty() || !pass.isEmpty()){
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(signUp.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(signUp.this,setup_activity.class));
                                finish();
                            }
                            else{
                                Toast.makeText(signUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(signUp.this, "Please Enter Email and Password", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }
}