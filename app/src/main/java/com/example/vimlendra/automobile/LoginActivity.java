package com.example.vimlendra.automobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vimlendra.automobile.Model.User;
import com.example.vimlendra.automobile.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;


public class LoginActivity extends AppCompatActivity {

    private EditText user_id_input, user_pass;
    private Button Login_btn;
    private ProgressDialog Login_loadingBar;
    private String parentdbName = "User";
    private CheckBox chboxrememberme;
    private TextView Admin_link, notAdmin_link;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Login_btn = findViewById(R.id.login_btn);
        user_id_input = findViewById(R.id.login_id_input);
        user_pass = findViewById(R.id.login_pass_input);
        Login_loadingBar = new ProgressDialog(this);
        Admin_link = findViewById(R.id.admin_panel_link);
        notAdmin_link = findViewById(R.id.not_admin_panel_link);

        chboxrememberme =  findViewById(R.id.remember_me_chbox);
        Paper.init(this);

        Login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

        Admin_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Login_btn.setText("Login Admin");
                Admin_link.setVisibility(View.INVISIBLE);
                notAdmin_link.setVisibility(View.VISIBLE);
                parentdbName  = "Admins";
                chboxrememberme.setVisibility(View.INVISIBLE);
            }
        });

        notAdmin_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login_btn.setText("Login");
                Admin_link.setVisibility(View.VISIBLE);
                notAdmin_link.setVisibility(View.INVISIBLE);
                parentdbName  = "User";
                chboxrememberme.setVisibility(View.VISIBLE);
            }
        });
    }

    private void LoginUser() {

        String PhoneNum = user_id_input.getText().toString();
        String Pass = user_pass.getText().toString();

        if(TextUtils.isEmpty(PhoneNum)){

            Toast.makeText(this, "Please Enter Your Login Id", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Pass)){

            Toast.makeText(this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();
        }
        else{
            Login_loadingBar.setTitle("Logging In");
            Login_loadingBar.setMessage("Please Wait while we check your credentials");
            Login_loadingBar.setCanceledOnTouchOutside(false  );
            Login_loadingBar.show();

            AllowAccessToAccount(PhoneNum,Pass);
        }
    }

    private void AllowAccessToAccount(final String PhoneNum, final String Pass) {


        if(chboxrememberme.isChecked())
        {
            Paper.book().write(Prevalent.UserPhonekey, PhoneNum);
            Paper.book().write(Prevalent.UserPasswordkey, Pass);

        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(parentdbName).child(PhoneNum).exists()){
                    User userdata = dataSnapshot.child(parentdbName).child(PhoneNum).getValue(User.class);

                    if(userdata.getPhoneNumber().equals(PhoneNum)){

                        if (userdata.getPassword().equals(Pass))
                        {
                            if(parentdbName.equals("Admins"))
                            {
                                Toast.makeText(LoginActivity.this, "Welcome Admin! Login Successful", Toast.LENGTH_SHORT).show();
                                Login_loadingBar.dismiss();
                                Intent intent = new Intent( LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);

                            }else if(parentdbName.equals("User"))
                            {
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                Login_loadingBar.dismiss();
                                Intent intent = new Intent( LoginActivity.this, HomeActivity.class);
                                Prevalent.Current_OnlineUser = userdata;
                                startActivity(intent);
                            }


                        }
                        else
                            {
                                Login_loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                            }
                    }


                }
                else {

                    Toast.makeText(LoginActivity.this, "Please Enter Valid UserId/Password ", Toast.LENGTH_SHORT).show();
                    Login_loadingBar.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
