package com.example.vimlendra.automobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.Calendar;
import java.util.HashMap;
import java.text.SimpleDateFormat;


public  class AdminAddNewProductActivity extends AppCompatActivity {

    private String categoryName, pDescription, pPrice, pName, saveCurrentDate, saveCurrentTime;
    private ImageView productImage;
    private Button AddNewProductBtn;
    private EditText productName, productDescription, productPrice;
    private static final int galleryPick = 1;
    private Uri imageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference ProductImageRef;
    private DatabaseReference ProductsRef;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);


        categoryName = getIntent().getExtras().get("category").toString();

        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Product Image");

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");


        AddNewProductBtn = findViewById(R.id.add_product_btn);
        productImage = findViewById(R.id.select_product_image);
        productName = findViewById(R.id.add_product_name);
        productDescription = findViewById(R.id.add_product_description);
        productPrice = findViewById(R.id.add_product_price);
        loadingBar = new ProgressDialog(this);




        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        AddNewProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateNewproductData();
            }
        });



    }

    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,galleryPick );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == galleryPick && resultCode==RESULT_OK && data != null)
        {
            imageUri = data.getData();
            productImage.setImageURI(imageUri);
        }

    }

    private void ValidateNewproductData()
    {
        pDescription = productDescription.getText().toString();
        pPrice = productPrice.getText().toString();
        pName = productName.getText().toString();

        if(imageUri == null)
        {
            Toast.makeText(this, "Please Select a Product Image", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pDescription))
        {
            Toast.makeText(this, "Please add description of the Product", Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(pPrice))
        {
            Toast.makeText(this, "Please add price of the Product", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pName))
        {
            Toast.makeText(this, "Please add name of the Product", Toast.LENGTH_SHORT).show();

        }

        else
            {
                StoreProductInformation();
            }

    }

    private void StoreProductInformation()
    {

        loadingBar.setTitle("Adding Product");
        loadingBar.setMessage("Please Wait while we add the product details");
        loadingBar.setCanceledOnTouchOutside(false  );
        loadingBar.show();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH: mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = ProductImageRef.child(imageUri.getLastPathSegment()+ productRandomKey+".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(AdminAddNewProductActivity.this, "Image uploaded Successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if(!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if(task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "got the image url Successfully", Toast.LENGTH_SHORT).show();
                            SaveProductInfoToDatabase();
                        }

                    }
                });

            }
        });

    }


        private void SaveProductInfoToDatabase()
        {
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", productRandomKey);
            productMap.put("date", saveCurrentDate);
            productMap.put("time", saveCurrentTime);
            productMap.put("description", pDescription);
            productMap.put("image", downloadImageUrl);
            productMap.put("category", categoryName);
            productMap.put("price", pPrice);
            productMap.put("pname", pName);

            ProductsRef.child(productRandomKey).updateChildren(productMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                Intent intent = new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);

                                loadingBar.dismiss();
                                Toast.makeText(AdminAddNewProductActivity.this, "Product is added successfully..", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                loadingBar.dismiss();
                                String message = task.getException().toString();
                                Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
    }


}
