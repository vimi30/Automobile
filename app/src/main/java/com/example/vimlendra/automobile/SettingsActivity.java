package com.example.vimlendra.automobile;

import android.app.ProgressDialog;
import android.app.UiAutomation;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vimlendra.automobile.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullNameEdit, userPhoneEdit, userGenderEdit;
    private TextView profileChangeTxtbtn, closebtn, updateBtn;

    private Uri imageUri;
    private String imageUrl = "";
    private StorageTask uploadTask;
    private StorageReference profileImageRef;
    private  String checker = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        profileImageView = findViewById(R.id.settings_profile_image);
        fullNameEdit = findViewById(R.id.setting_full_name);
        userPhoneEdit = findViewById(R.id.setting_phone_number);
        userGenderEdit = findViewById(R.id.setting_Gender);
        profileChangeTxtbtn =findViewById(R.id.change_profile_image);
        closebtn = findViewById(R.id.close_setting_btn);
        updateBtn = findViewById(R.id.update_setting_btn);

        userInfoDisplay(profileImageView,fullNameEdit, userPhoneEdit,userGenderEdit );

        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checker.equals("Clicked"))
                {
                    UserInfoSaved();

                }else
                    {
                        UpdateOnlyUserInfo();

                    }
            }
        });

        profileChangeTxtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             checker = "Clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });
    }

    private void UpdateOnlyUserInfo()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User");
        HashMap<String ,Object> userMap = new HashMap<>();
        userMap.put("Name", fullNameEdit.getText().toString());
        userMap.put("phoneNumber", userPhoneEdit.getText().toString());
        userMap.put("Gender", userGenderEdit.getText().toString());
        ref.child(Prevalent.Current_OnlineUser.getPhoneNumber()).updateChildren(userMap);

        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Account Info Updated Successfully", Toast.LENGTH_SHORT).show();
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode ==RESULT_OK && data !=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);


        }else
            {
                Toast.makeText(this, "Error, Try Again", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
                finish();
            }
    }

    private void UserInfoSaved()
    {
        if(TextUtils.isEmpty(fullNameEdit.getText().toString()))
        {
            Toast.makeText(this, "Name Is Mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userPhoneEdit.getText().toString()))
        {
            Toast.makeText(this, "please fill the phone number ", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(userGenderEdit.getText().toString()))
        {
            Toast.makeText(this, "please mention your gender", Toast.LENGTH_SHORT).show();
        }
        else if(checker.equals("Clicked"))
        {
            uploadImage();

        }
    }

    private void uploadImage()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Updating Profile");
        progressDialog.setMessage("Please wait while we update your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri != null)
        {
            final StorageReference fileref = profileImageRef.child(Prevalent.Current_OnlineUser.getPhoneNumber());
            uploadTask = fileref.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileref.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful()){

                        Uri downloadUrl = task.getResult();
                        imageUrl = downloadUrl.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User");
                        HashMap<String ,Object> userMap = new HashMap<>();
                        userMap.put("Name", fullNameEdit.getText().toString());
                        userMap.put("phoneNumber", userPhoneEdit.getText().toString());
                        userMap.put("Gender", userGenderEdit.getText().toString());
                        userMap.put("image",imageUrl);

                        ref.child(Prevalent.Current_OnlineUser.getPhoneNumber()).updateChildren(userMap);

                        progressDialog.dismiss();
                        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                        Toast.makeText(SettingsActivity.this, "Account Info Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "Erron: Please Try Again", Toast.LENGTH_SHORT).show();
                        }

                }
            });
        }
        else{
            Toast.makeText(this, "Image is not selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEdit, final EditText userPhoneEdit, final EditText userGenderEdit)
    {
        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("User").child(Prevalent.Current_OnlineUser.getPhoneNumber());

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.child("image").exists())
                    {
                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("Name").getValue().toString();
                        String phoneN = dataSnapshot.child("phoneNumber").getValue().toString();
                        String gender = dataSnapshot.child("Gender").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullNameEdit.setText(name);
                        userPhoneEdit.setText(phoneN);
                        userGenderEdit.setText(gender);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
