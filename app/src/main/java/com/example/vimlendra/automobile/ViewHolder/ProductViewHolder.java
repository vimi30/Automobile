package com.example.vimlendra.automobile.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vimlendra.automobile.Interface.ItemClickListner;
import com.example.vimlendra.automobile.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView txtproductname, txtproductdescription, txtproductprice;
    public ImageView productImage;
    public ItemClickListner listner;
    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        productImage =(ImageView) itemView.findViewById(R.id.product_image);
        txtproductname =(TextView) itemView.findViewById(R.id.product_name);
        txtproductdescription =(TextView) itemView.findViewById(R.id.product_description);
        txtproductprice =(TextView) itemView.findViewById(R.id.product_price);
    }

    public void setItemClickListner (ItemClickListner listner  )
    {
        this.listner = listner;
    }

    @Override
    public void onClick(View v) {

        listner.onClick(v, getAdapterPosition(), false);

    }
}
