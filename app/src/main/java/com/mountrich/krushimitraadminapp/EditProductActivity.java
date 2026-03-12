package com.mountrich.krushimitraadminapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class EditProductActivity extends AppCompatActivity {

    EditText etName, etCategory, etPrice, etDescription, etStock;
    Button btnUpdate, btnSelectImage;
    ImageView imgProduct;

    FirebaseFirestore db;
    StorageReference storageReference;

    String productId;
    Uri imageUri;

    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        etName = findViewById(R.id.etName);
        etCategory = findViewById(R.id.etCategory);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        etStock = findViewById(R.id.etStock);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnSelectImage = findViewById(R.id.btnSelectImage);

        imgProduct = findViewById(R.id.imgProduct);

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("product_images");

        productId = getIntent().getStringExtra("id");

        String name = getIntent().getStringExtra("name");
        String category = getIntent().getStringExtra("category");
        String price = getIntent().getStringExtra("price");
        String description = getIntent().getStringExtra("description");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        int stock = getIntent().getIntExtra("stock", 0);

        etName.setText(name);
        etCategory.setText(category);
        etPrice.setText(price);
        etDescription.setText(description);
        etStock.setText(String.valueOf(stock));

        Glide.with(this).load(imageUrl).into(imgProduct);

        btnSelectImage.setOnClickListener(v -> openGallery());

        btnUpdate.setOnClickListener(v -> updateProduct());
    }

    private void openGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {

            imageUri = data.getData();
            imgProduct.setImageURI(imageUri);
        }
    }

    private void updateProduct() {

        String name = etName.getText().toString();
        String category = etCategory.getText().toString();
        String price = etPrice.getText().toString();
        String description = etDescription.getText().toString();
        String stock = etStock.getText().toString();

        if (imageUri != null) {

            String fileName = "product_" + System.currentTimeMillis() + ".jpg";

            StorageReference fileRef = storageReference.child(fileName);

            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot ->
                            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {

                                String imageUrl = uri.toString();

                                saveProductToFirestore(name, category, price, description, stock, imageUrl);

                            })
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Image Upload Failed", Toast.LENGTH_SHORT).show());

        } else {

            saveProductToFirestore(name, category, price, description, stock, null);
        }
    }

    private void saveProductToFirestore(String name, String category, String price,
                                        String description, String stock, String imageUrl) {

        Map<String, Object> product = new HashMap<>();

        product.put("name", name);
        product.put("category", category);
        product.put("price", Double.parseDouble(price));
        product.put("description", description);
        product.put("stock", Integer.parseInt(stock));

        if (imageUrl != null) {
            product.put("imageUrl", imageUrl);
        }

        db.collection("products")
                .document(productId)
                .update(product)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Product Updated Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show());
    }
}