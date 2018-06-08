package com.example.android.inventoryapp.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.model.ProductContract.ProductEntry;
import com.example.android.inventoryapp.model.ProductDbHelper;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private ProductDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new ProductDbHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        insertProduct();
        getProducts();
    }

    private void insertProduct() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Long currentTime = System.currentTimeMillis();
        Random r = new Random();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME,
                getString(R.string.default_product_name) + String.valueOf(currentTime));
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, getPositiveNumber(r));
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, getPositiveNumber(r));
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME,
                getString(R.string.default_supplier_name) + String.valueOf(currentTime));
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE,
                getString(R.string.default_supplier_phone_prefix) + String.valueOf(getPositiveNumber(r)));
        Long rowId = db.insert(ProductEntry.TABLE_NAME, null, values);
        Log.d(TAG, getString(R.string.dbg_raw_id) + String.valueOf(rowId));
    }

    private void getProducts() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY };
        Cursor cursor = null;
        try {
            cursor = db.query(
                    ProductEntry.TABLE_NAME,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            loggingQuery(cursor);
        } finally {
            assert cursor != null;
            cursor.close();
        }
    }

    private void loggingQuery(Cursor cursor) {
        int productId = cursor.getColumnIndex(ProductEntry._ID);
        int productNameColumn = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int productPriceColumn = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int productQuantityColumn = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        String delimiter = "\t";
        StringBuilder output = new StringBuilder();
        output.append("\n").append("\n");
        output.append(cursor.getColumnName(productId)).append(delimiter);
        output.append(cursor.getColumnName(productNameColumn)).append(delimiter);
        output.append(cursor.getColumnName(productPriceColumn)).append(delimiter);
        output.append(cursor.getColumnName(productQuantityColumn)).append("\n");
        while (cursor.moveToNext()) {
            output.append(cursor.getInt(productId)).append(delimiter);
            output.append(cursor.getString(productNameColumn)).append(delimiter);
            output.append(cursor.getInt(productPriceColumn)).append(delimiter);
            output.append(cursor.getInt(productQuantityColumn)).append("\n");
        }
        Log.d(TAG, String.valueOf(output));
    }

    private Integer getPositiveNumber(Random r) {
        Integer random = r.nextInt();
        while (random <= 0) {
            random = r.nextInt();
        }
        return random;
    }
}
