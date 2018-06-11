package com.example.android.inventoryapp.activities;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.model.ProductContract;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PET_LOADER = 2001;
    Uri currentProductUri;

    private TextView productName;
    private TextView productPrice;
    private TextView productQuantity;
    private TextView supplierName;
    private TextView supplierPhone;

    private Cursor cursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        currentProductUri = intent.getData();
        setupActionBar();
        setupTextViews();
        setupButtons();
        getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_detail_edit:
                Intent intent = new Intent(this, EditActivity.class);
                intent.setData(currentProductUri);
                startActivity(intent);
                break;
            case R.id.menu_detail_delete:
                String where = ProductContract.ProductEntry._ID + "=" + currentProductUri.getLastPathSegment();
                getContentResolver().delete(currentProductUri, where, null);
                finish();
                break;
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return true;
    }

    private void setupActionBar() {
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.detail_activity_title);
    }

    private void setupTextViews() {
        productName = findViewById(R.id.detail_product_name);
        productPrice = findViewById(R.id.detail_product_price);
        productQuantity = findViewById(R.id.detail_product_quantity);
        supplierName = findViewById(R.id.detail_supplier_name);
        supplierPhone = findViewById(R.id.detail_supplier_phone);
    }

    private void setupButtons() {
        findViewById(R.id.increase_quantity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeQuantity(1);
            }
        });
        findViewById(R.id.decrease_quantity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeQuantity(-1);
            }
        });
        findViewById(R.id.call_to_supplier).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_CALL,
                        Uri.parse("tel:" + supplierPhone.getText().toString())
                );
                if (ActivityCompat.checkSelfPermission(getBaseContext(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);
            }
        });
    }

    private void changeQuantity(Integer amount) {
        if (cursor != null) {
            Integer nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            Integer indexColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry._ID);
            Integer priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            Integer nameSupplierColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME);
            Integer phoneSupplierColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE);
            Integer quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            Integer productId = cursor.getInt(indexColumnIndex);
            String productName = cursor.getString(nameColumnIndex);
            Integer productPrice = cursor.getInt(priceColumnIndex);
            String supplierName = cursor.getString(nameSupplierColumnIndex);
            String supplierPhone = cursor.getString(phoneSupplierColumnIndex);
            Integer newQuantity = cursor.getInt(quantityColumnIndex) + amount;
            if (ProductContract.ProductEntry.isValidQuantity(newQuantity)) {
                findViewById(R.id.decrease_quantity).setEnabled(true);
                String where = ProductContract.ProductEntry._ID + "=" + String.valueOf(productId);
                ContentValues values = new ContentValues();
                values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, productName);
                values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);
                values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME, supplierName);
                values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);
                getBaseContext().getContentResolver().update(ProductContract.ProductEntry.CONTENT_URI, values, where, null);
            } else {
                findViewById(R.id.decrease_quantity).setEnabled(false);
                Toast.makeText(getBaseContext(), R.string.toast_product_is_over, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE };
        return new CursorLoader(this, currentProductUri, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }
        cursor = data;
        if (data.moveToFirst()) {
            int productNameColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int productPriceColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            int productQuantityColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE);
            productName.setText(data.getString(productNameColumnIndex));
            productPrice.setText(String.format(getString(R.string.template_product_price), data.getInt(productPriceColumnIndex)));
            productQuantity.setText(String.format(getString(R.string.template_product_quantity), data.getInt(productQuantityColumnIndex)));
            supplierName.setText(data.getString(supplierNameColumnIndex));
            supplierPhone.setText(data.getString(supplierPhoneColumnIndex));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productName.setText("");
        productPrice.setText("");
        productQuantity.setText("");
        supplierName.setText("");
        supplierPhone.setText("");
    }
}
