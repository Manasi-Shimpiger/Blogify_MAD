package com.example.blogify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    TextView login;
    EditText email, passwd, c_passwd, userName;
    Button register;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        login = findViewById(R.id.login);
        email = findViewById(R.id.email);
        passwd = findViewById(R.id.passwd);
        userName = findViewById(R.id.userName);
        c_passwd = findViewById(R.id.c_passwd);
        register = findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email_field=email.getText().toString();
                String passwd_field=passwd.getText().toString();
                String c_passwd_field=c_passwd.getText().toString();
                String u_name = userName.getText().toString().trim();

                //check if fields r not empty
                if(!TextUtils.isEmpty(email_field) && !TextUtils.isEmpty(passwd_field) && !TextUtils.isEmpty(c_passwd_field) && !TextUtils.isEmpty(u_name)){
                    //check if passwd and c_passwd are same
                    if(passwd_field.equals(c_passwd_field)){
                        //set progressbar visible
                        //create account
                        mAuth.createUserWithEmailAndPassword(email_field,passwd_field).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){


                                    storeData(email_field, u_name);


                                }else{
                                    String errorMessage=task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this,"Error: "+errorMessage,Toast.LENGTH_SHORT).show();
                                }
                                //progressbar invisible

                            }
                        });

                    }
                    else{
                        Toast.makeText(RegisterActivity.this,"Password and Confirm password fields doesn't match",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RegisterActivity.this, "All the input fields should be filled", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void storeData(String email, String uName) {

        reference = FirebaseDatabase.getInstance().getReference().child("User");
        user = FirebaseAuth.getInstance().getCurrentUser();

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", user.getUid());
        map.put("name", uName);
        map.put("email", email);

        reference.child(user.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent=new Intent(RegisterActivity.this,HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }
}