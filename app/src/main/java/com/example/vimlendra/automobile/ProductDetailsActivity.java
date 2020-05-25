package com.example.vimlendra.automobile;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vimlendra.automobile.Model.Products;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView productImageDe;
    private TextView productNameDe, productDescriptionDe, productPriceDe;
    private String productId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productId = getIntent().getStringExtra("pid");

        productImageDe = findViewById(R.id.product_image_details);
        productNameDe = findViewById(R.id.product_name_details);
        productPriceDe = findViewById(R.id.product_price_details);
        productDescriptionDe = findViewById(R.id.product_description_details);
        
        getProductDetails(productId);
    }

    private void getProductDetails(String productId)
    {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Products products = dataSnapshot.getValue(Products.class);

                productNameDe.setText(products.getPname());
                productPriceDe.setText(products.getPrice());
                productDescriptionDe.setText(products.getDescription());
                Picasso.get().load(products.getImage()).into(productImageDe);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
