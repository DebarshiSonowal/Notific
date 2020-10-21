package com.deb.notific;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.deb.notific.helper.user;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.skydoves.elasticviews.ElasticButton;

public class signup extends AppCompatActivity {
    EditText  txtUserName,txtEmail, txtPassword, txtConfirmPassword;
    ElasticButton btn_register,btn_login;
    private FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        txtUserName =  findViewById(R.id.txt_username);
        txtEmail =  findViewById(R.id.txt_email);
        txtPassword =  findViewById(R.id.txt_password);
        txtConfirmPassword =  findViewById(R.id.txt_confirm_password);
        btn_register = findViewById(R.id.signupbtn3);
        btn_login = findViewById(R.id.loginbtn3);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("user");

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signup.this,login.class));
                Animatoo.animateZoom(signup.this);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = txtUserName.getText().toString().trim();
                final String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();
                String confirmPassword = txtConfirmPassword.getText().toString().trim();

                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(signup.this, "Please Enter User Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(signup.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(signup.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(signup.this, "Please Enter Confirm Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(signup.this, "Password too short", Toast.LENGTH_SHORT).show();
                }




                if (password.equals(confirmPassword)) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(signup.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {



                                    if (task.isSuccessful()) {
                                        user information = new user(
                                                userName,
                                                email
                                        );

                                        FirebaseDatabase.getInstance().getReference("user")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(information).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(signup.this, "Registration Complete", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(), login.class));
                                                Animatoo.animateZoom(signup.this);
                                            }
                                        });

                                    } else {
                                        Toast.makeText(signup.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(signup.this, "Password did not match", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateZoom(signup.this);
    }

}
