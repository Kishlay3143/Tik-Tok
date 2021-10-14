package com.example.videostreaming;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    TextInputEditText email,password;
    Button signUp;
    TextView alreadyRegistered;
    ProgressBar bar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();  // Hide Action Bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);   // Hide Status Bar
        email=findViewById(R.id.cuDate);
        password=findViewById(R.id.password);
        signUp=findViewById(R.id.signUp);
        bar=findViewById(R.id.bar);
        alreadyRegistered=findViewById(R.id.alreadyRegistered);

    }
    //goToSignIn

    public void goToSignIn(View view){
                startActivity(new Intent(MainActivity.this,Login.class));
    }


    public void signUpHere(View view){
        bar.setVisibility(View.VISIBLE);
        String Email=email.getText().toString();
        String Password=password.getText().toString();

        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            bar.setVisibility(View.INVISIBLE);
                            email.setText("");
                            password.setText("");
                            Toast.makeText(MainActivity.this, "Registered Successfully !!", Toast.LENGTH_SHORT).show();

                        } else {
                            bar.setVisibility(View.INVISIBLE);
                            email.setText("");
                            password.setText("");
                            String s = "Sign up Failed" + task.getException();
                            Toast.makeText(MainActivity.this, s,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


}