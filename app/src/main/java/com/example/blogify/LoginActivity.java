package com.example.blogify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText login_email,login_passwd;
    Button login_btn;
    TextView register_btn;
    ProgressBar login_progressbar;
    FirebaseAuth mAuth;
    LinearLayout tempView;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tempView = findViewById(R.id.tempView);
        login_email=findViewById(R.id.email_login);
        login_passwd=findViewById(R.id.passwd_login);
        login_btn=findViewById(R.id.login);
        register_btn=findViewById(R.id.register);
        login_progressbar =findViewById(R.id.login_progressbar);
        mAuth=FirebaseAuth.getInstance();



        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String loginemail=login_email.getText().toString();
                String loginpasswd=login_passwd.getText().toString();

                //checking if email and passwd is not empty
                if (!TextUtils.isEmpty(loginemail) && !TextUtils.isEmpty(loginpasswd)){
                    // making progressbar visible
                    login_progressbar.setVisibility(View.VISIBLE);
                    tempView.setVisibility(View.INVISIBLE);
                    //login process                                         //checks whether login process is complete or not
                    mAuth.signInWithEmailAndPassword(loginemail,loginpasswd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            //if crendtials are correct
                            if(task.isSuccessful()){
                                // send to home
                                Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                //error message from the server is stored
                                String errorMessage=task.getException().getMessage();
                                Toast.makeText(LoginActivity.this,"Error: "+errorMessage,Toast.LENGTH_SHORT).show();
                            }
                            login_progressbar.setVisibility(View.INVISIBLE);
                            tempView.setVisibility(View.VISIBLE);
                        }
                    });
                }
                else{
                    Toast.makeText(LoginActivity.this,"Fill all the crenditials",Toast.LENGTH_SHORT).show();
                }
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });
    }


}