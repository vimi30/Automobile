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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    private Button CreateAccountBtn;
    private EditText Name, PhoneNumber, Password, Password2;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        CreateAccountBtn = (Button)findViewById(R.id.sign_up_btn);
        Name = (EditText)findViewById(R.id.SignPage_name_input);
        PhoneNumber = (EditText)findViewById(R.id.SignPage_login_id_input);
        Password = (EditText)findViewById(R.id.Signpage_login_pass_input);
        Password2 = (EditText)findViewById(R.id.Signpage_login_pass2_input);
        loadingBar = new ProgressDialog(this);

        CreateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });

    }

    private void CreateAccount() {

        String name = Name.getText().toString();
        String PhoneNum = PhoneNumber.getText().toString();
        String Pass = Password.getText().toString();
        String Pass2 = Password2.getText().toString();

        if(TextUtils.isEmpty(name)){

            Toast.makeText(this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(PhoneNum)){

            Toast.makeText(this, "Please Enter Your PhoneNumber", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Pass)){

            Toast.makeText(this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Pass2)){

            Toast.makeText(this, "Please Confirm Your Password", Toast.LENGTH_SHORT).show();
        }
        else if( !(Password.getText().toString().equals(Password2.getText().toString()))){
            
            Toast.makeText(this, "Password Does Not Match! Please Enter Again", Toast.LENGTH_SHORT).show();
            
        }
        else  {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please Wait while we check your credentials");
            loadingBar.setCanceledOnTouchOutside(false  );
            loadingBar.show();

            validarePhoneNumber(name,PhoneNum, Pass,Pass2);
        }
    }

    private void validarePhoneNumber(final String name, final String phoneNum, final String pass, String pass2) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("User").child(phoneNum).exists())){
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("phoneNumber",phoneNum);
                    userDataMap.put("Password",pass);
                    userDataMap.put("Name",name);
                    RootRef.child("User").child(phoneNum).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(SignupActivity.this, "Your Account Has Been Created", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent( SignupActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }else {
                                        loadingBar.dismiss();
                                        Toast.makeText(SignupActivity.this, "Network Error: Please Try Again!", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });


                }
                else {
                    Toast.makeText(SignupActivity.this, "User Already Exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(SignupActivity.this, "Please Try Agian Using Another Number", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
