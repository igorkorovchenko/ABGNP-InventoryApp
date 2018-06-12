package com.example.android.inventoryapp.activities;

import android.Manifest;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.controllers.ProductCursorAdapter;
import com.example.android.inventoryapp.model.ProductContract.ProductEntry;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;

    private ProductCursorAdapter productCursorAdapter;
    private final int REQUEST_PERMISSION_CALL_PHONE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        setupCatalog();
        setupAddButton();
        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE
        };
        return new CursorLoader(this, ProductEntry.CONTENT_URI, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        productCursorAdapter.swapCursor(data);
        if (data.getCount() > 0) {
            findViewById(R.id.catalog_no_products).setVisibility(View.GONE);
            findViewById(R.id.catalog_list).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.catalog_no_products).setVisibility(View.VISIBLE);
            findViewById(R.id.catalog_list).setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productCursorAdapter.swapCursor(null);
    }

    private void setupCatalog() {
        ListView productListView = findViewById(R.id.catalog_list);
        productCursorAdapter = new ProductCursorAdapter(this, null, 0);
        productListView.setAdapter(productCursorAdapter);
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, (int) view.getTag());
                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });
    }

    private void setupAddButton() {
        FloatingActionButton addButton = findViewById(R.id.catalog_adding);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupIntentToAddingActivity();
            }
        });
    }

    private void setupIntentToAddingActivity() {
        Intent intent = new Intent(this, EditActivity.class);
        startActivity(intent);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        REQUEST_PERMISSION_CALL_PHONE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CALL_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, R.string.toast_permissions_granted, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
