package com.mountrich.krushimitraadminapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mountrich.krushimitraadminapp.R;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddProductFragment extends Fragment {

    EditText etName, etPrice, etDescription, etStock;
    MaterialAutoCompleteTextView spCategory;
    Button btnSelectImage, btnSubmit;
    ImageView imgProduct;
    ShimmerFrameLayout shimmerLayout;
    ScrollView formLayout;

    Uri imageUri;

    FirebaseFirestore db;
    StorageReference storageRef;

    private static final int IMAGE_PICK_CODE = 101;

    public AddProductFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        etName = view.findViewById(R.id.etProductName);
        etPrice = view.findViewById(R.id.etPrice);
        etDescription = view.findViewById(R.id.etDescription);
        etStock = view.findViewById(R.id.etStock);

        spCategory = view.findViewById(R.id.spCategory);

        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        imgProduct = view.findViewById(R.id.imgProduct);

        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        formLayout = view.findViewById(R.id.formLayout);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("product_images");

        // Dropdown categories
        String[] categories = {"Seeds", "Fertilizers", "Pesticides", "Tools", "Irrigation", "Machinery"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categories
        );

        spCategory.setAdapter(adapter);

        btnSelectImage.setOnClickListener(v -> openGallery());
        btnSubmit.setOnClickListener(v -> uploadProduct());

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_PICK_CODE &&
                resultCode == Activity.RESULT_OK &&
                data != null) {

            imageUri = data.getData();
            imgProduct.setImageURI(imageUri);
        }
    }

    private void uploadProduct() {

        String name = etName.getText().toString().trim();
        String category = spCategory.getText().toString().trim(); // ✅ FIXED
        String price = etPrice.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String stock = etStock.getText().toString().trim();

        if (name.isEmpty() || category.isEmpty() || price.isEmpty()
                || description.isEmpty() || stock.isEmpty() || imageUri == null) {

            Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        formLayout.setVisibility(View.GONE);
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();

        String fileName = UUID.randomUUID().toString();

        storageRef.child(fileName)
                .putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.child(fileName).getDownloadUrl()
                                .addOnSuccessListener(uri -> {

                                    Map<String, Object> product = new HashMap<>();
                                    product.put("name", name);
                                    product.put("category", category);
                                    product.put("price", Double.parseDouble(price));
                                    product.put("description", description);
                                    product.put("stock", Integer.parseInt(stock));
                                    product.put("imageUrl", uri.toString());
                                    product.put("timestamp", System.currentTimeMillis());

                                    db.collection("products")
                                            .add(product)
                                            .addOnSuccessListener(documentReference -> {

                                                stopShimmer();
                                                Toast.makeText(getContext(),
                                                        "Product Added Successfully",
                                                        Toast.LENGTH_SHORT).show();
                                                clearFields();
                                            })
                                            .addOnFailureListener(e -> {
                                                stopShimmer();
                                                Toast.makeText(getContext(),
                                                        "Failed to Add Product",
                                                        Toast.LENGTH_SHORT).show();
                                            });

                                })
                                .addOnFailureListener(e -> stopShimmer())
                )
                .addOnFailureListener(e -> {
                    stopShimmer();
                    Toast.makeText(getContext(),
                            "Image Upload Failed",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void stopShimmer() {
        shimmerLayout.stopShimmer();
        shimmerLayout.setVisibility(View.GONE);
        formLayout.setVisibility(View.VISIBLE);
    }

    private void clearFields() {
        etName.setText("");
        etPrice.setText("");
        etDescription.setText("");
        etStock.setText("");
        spCategory.setText(""); // ✅ Important
        imgProduct.setImageResource(android.R.drawable.ic_menu_gallery);
        imageUri = null;
    }
}
