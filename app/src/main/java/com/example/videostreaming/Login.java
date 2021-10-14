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
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    TextInputEditText emailLog,passwordLog;
    Button login;
    TextView createAccount;
    ProgressBar barLog;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);   // Hide Status Bar
        emailLog=findViewById(R.id.emailLog);
        passwordLog=findViewById(R.id.passwordLog);
        login=findViewById(R.id.login);
        barLog=findViewById(R.id.barLog);
        createAccount=findViewById(R.id.createAccount);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();  // Hide Action Bar
        }

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            Intent intent=new Intent(Login.this,Dashboard.class);
            startActivity(intent);
            finish();
        }

    }


    public void signInHere(View view) {
        barLog.setVisibility(View.VISIBLE);
        String Email=emailLog.getText().toString();
        String Password=passwordLog.getText().toString();

        mAuth.signInWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            barLog.setVisibility(View.INVISIBLE);
                            startActivity(new Intent(Login.this,Dashboard.class));
                        } else {
                            barLog.setVisibility(View.INVISIBLE);
                            emailLog.setText("");
                            passwordLog.setText("");
                            Toast.makeText(Login.this, "Invalid email/password..!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void goToSignUp(View view){
        startActivity(new Intent(Login.this,MainActivity.class));
    }
}