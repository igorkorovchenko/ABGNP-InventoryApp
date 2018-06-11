package com.example.android.inventoryapp.activities;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.R;

import static com.example.android.inventoryapp.model.ProductContract.ProductEntry;

public class AddInventoryActivity extends AppCompatActivity {

    private static final String PRODUCT_NAME_KEY = "product_name";
    private static final String PRODUCT_PRICE_KEY = "product_price";
    private static final String PRODUCT_QUANTITY_KEY = "product_quantity";
    private static final String SUPPLIER_NAME_KEY = "supplier_name";
    private static final String SUPPLIER_PHONE_KEY = "supplier_phone";

    private EditText productName;
    private EditText productPrice;
    private EditText productQuantity;
    private EditText supplierName;
    private EditText supplierPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inventory);
        setupFields(savedInstanceState);
        setupAddButton();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PRODUCT_NAME_KEY, productName.getText().toString());
        outState.putString(PRODUCT_PRICE_KEY, productPrice.getText().toString());
        outState.putString(PRODUCT_QUANTITY_KEY, productQuantity.getText().toString());
        outState.putString(SUPPLIER_NAME_KEY, supplierName.getText().toString());
        outState.putString(SUPPLIER_PHONE_KEY, supplierPhone.getText().toString());
    }

    private void setupFields(Bundle savedInstanceState) {
        productName = findViewById(R.id.new_product_name);
        productPrice = findViewById(R.id.new_product_price);
        productQuantity = findViewById(R.id.new_product_quantity);
        supplierName = findViewById(R.id.new_supplier_name);
        supplierPhone = findViewById(R.id.new_supplier_phone);

        if (savedInstanceState != null) {
            productName.setText(savedInstanceState.getString(PRODUCT_NAME_KEY));
            productPrice.setText(savedInstanceState.getString(PRODUCT_PRICE_KEY));
            productQuantity.setText(savedInstanceState.getString(PRODUCT_QUANTITY_KEY));
            supplierName.setText(savedInstanceState.getString(SUPPLIER_NAME_KEY));
            supplierPhone.setText(savedInstanceState.getString(SUPPLIER_PHONE_KEY));
        }
    }

    private void setupAddButton() {
        Button addProduct = findViewById(R.id.new_product_add);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveProduct()) {
                    finish();
                } else {
                    Toast.makeText(getBaseContext(),
                            getString(R.string.toast_insert_product_failed),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean saveProduct() {
        String productNameString = productName.getText().toString();
        String productPriceString = productPrice.getText().toString();
        String productQuantityString = productQuantity.getText().toString();
        String supplierNameString = supplierName.getText().toString();
        String supplierPhoneString = supplierPhone.getText().toString();

        ContentValues values = new ContentValues();
        if (TextUtils.isEmpty(productNameString)) {
            return false;
        }
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, productNameString);

        int price = 0;
        if (!TextUtils.isEmpty(productPriceString)) {
            price = Integer.parseInt(productPriceString);
        }
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);

        int quantity = 0;
        if (!TextUtils.isEmpty(productPriceString)) {
            quantity = Integer.parseInt(productQuantityString);
        }
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);

        if (TextUtils.isEmpty(supplierNameString)) {
            return false;
        }
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierNameString);

        if (TextUtils.isEmpty(supplierPhoneString)) {
            return false;
        }
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);

        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        if (newUri == null) {
            Toast.makeText(this, getString(R.string.toast_insert_product_failed),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(this, getString(R.string.toast_insert_product_successful),
                    Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
