package com.example.android.inventoryapp.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.activities.EditActivity;
import com.example.android.inventoryapp.activities.MainActivity;
import com.example.android.inventoryapp.model.ProductContract.ProductEntry;

/**
 * Class
 *
 * @package com.example.android.inventoryapp.controllers
 * (c) 2018, Igor Korovchenko.
 */
public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        Button buyButton = view.findViewById(R.id.product_buy);
        TextView nameTextView = view.findViewById(R.id.product_name);
        TextView priceTextView = view.findViewById(R.id.product_price);
        TextView quantityTextView = view.findViewById(R.id.product_quantity);
        final Integer indexColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
        Integer nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        Integer priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        Integer nameSupplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
        Integer phoneSupplierColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE);
        final Integer quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        final Integer productId = cursor.getInt(indexColumnIndex);
        final String productName = cursor.getString(nameColumnIndex);
        final Integer productPrice = cursor.getInt(priceColumnIndex);
        final Integer productQuantity = cursor.getInt(quantityColumnIndex);
        final String supplierName = cursor.getString(nameSupplierColumnIndex);
        final String supplierPhone = cursor.getString(phoneSupplierColumnIndex);
        view.setTag(productId);
        nameTextView.setText(productName);
        priceTextView.setText(String.format(context.getString(R.string.template_product_price), productPrice));
        quantityTextView.setText(String.format(context.getString(R.string.template_product_quantity), productQuantity));
        if (productQuantity == 0) buyButton.setEnabled(false);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer newQuantity = cursor.getInt(quantityColumnIndex) - 1;
                if (ProductEntry.isValidQuantity(newQuantity)) {
                    String where = ProductEntry._ID + "=" + String.valueOf(productId);
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_NAME, productName);
                    values.put(ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);
                    values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierName);
                    values.put(ProductEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);
                    context.getContentResolver().update(ProductEntry.CONTENT_URI, values, where, null);
                } else {
                    Toast.makeText(context, R.string.toast_product_is_over, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
