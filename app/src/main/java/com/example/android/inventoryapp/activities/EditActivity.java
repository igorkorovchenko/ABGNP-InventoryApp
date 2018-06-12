package com.example.android.inventoryapp.activities;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.R;

import static com.example.android.inventoryapp.model.ProductContract.ProductEntry;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PET_LOADER = 2000;
    private Uri currentProductUri;

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

    private Boolean isFieldsAreNotEmpty = false;
    private View.OnFocusChangeListener focusChanged = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!areFieldsEmpty()) {
                isFieldsAreNotEmpty = true;
            }
        }
    };
    private View.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (!((EditText) v).getText().toString().isEmpty()) {
                isFieldsAreNotEmpty = true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        currentProductUri = intent.getData();
        setupInterface();
        setupActionBar();
        setupFields(savedInstanceState);
        setupSaveButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        productName.clearFocus();
        findViewById(R.id.edit_product_layout).requestFocus();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!isFieldsAreNotEmpty) {
                super.onBackPressed();
                return true;
            }
            setupDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!isFieldsAreNotEmpty) {
            super.onBackPressed();
            return;
        }
        setupDialog();
    }

    private void setupActionBar() {
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setupInterface() {
        if (currentProductUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            getLoaderManager().initLoader(EXISTING_PET_LOADER, null, this);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupFields(Bundle savedInstanceState) {
        productName = findViewById(R.id.edit_product_name);
        productPrice = findViewById(R.id.edit_product_price);
        productQuantity = findViewById(R.id.edit_product_quantity);
        supplierName = findViewById(R.id.edit_supplier_name);
        supplierPhone = findViewById(R.id.edit_supplier_phone);

        if (savedInstanceState != null) {
            productName.setText(savedInstanceState.getString(PRODUCT_NAME_KEY));
            productPrice.setText(savedInstanceState.getString(PRODUCT_PRICE_KEY));
            productQuantity.setText(savedInstanceState.getString(PRODUCT_QUANTITY_KEY));
            supplierName.setText(savedInstanceState.getString(SUPPLIER_NAME_KEY));
            supplierPhone.setText(savedInstanceState.getString(SUPPLIER_PHONE_KEY));
        }

        productName.setOnFocusChangeListener(focusChanged);
        productPrice.setOnFocusChangeListener(focusChanged);
        productQuantity.setOnFocusChangeListener(focusChanged);
        supplierName.setOnFocusChangeListener(focusChanged);
        supplierPhone.setOnFocusChangeListener(focusChanged);

        productName.setOnKeyListener(keyListener);
        productPrice.setOnKeyListener(keyListener);
        productQuantity.setOnKeyListener(keyListener);
        supplierName.setOnKeyListener(keyListener);
        supplierPhone.setOnKeyListener(keyListener);
    }

    private Boolean areFieldsEmpty() {
        return productName.getText().toString().isEmpty()
            && productPrice.getText().toString().isEmpty()
            && productQuantity.getText().toString().isEmpty()
            && supplierName.getText().toString().isEmpty()
            && supplierPhone.getText().toString().isEmpty();
    }

    private void setupSaveButton() {
        Button addProduct = findViewById(R.id.edit_product_save);
        if (currentProductUri != null) {
            addProduct.setText(R.string.button_edit_product);
        }
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveProduct()) {
                    finish();
                } else {
                    Toast.makeText(getBaseContext(),
                            getString(R.string.toast_saving_product_failed),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setupDialog() {
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.unsaved_changes_dialog_msg);
            builder.setPositiveButton(R.string.unsaved_changes_dialog_discard, discardButtonClickListener);
            builder.setNegativeButton(R.string.unsaved_changes_dialog_keep_editing, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean saveProduct() {
        String productNameString = productName.getText().toString();
        String productPriceString = productPrice.getText().toString();
        String productQuantityString = productQuantity.getText().toString();
        String supplierNameString = supplierName.getText().toString();
        String supplierPhoneString = supplierPhone.getText().toString();

        ContentValues values = new ContentValues();
        if (!TextUtils.isEmpty(productNameString)) {
            values.put(ProductEntry.COLUMN_PRODUCT_NAME, productNameString);
        }

        int price = 0;
        if (!TextUtils.isEmpty(productPriceString)) {
            price = Integer.parseInt(productPriceString);
            values.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
        }

        int quantity = 0;
        if (!TextUtils.isEmpty(productQuantityString)) {
            quantity = Integer.parseInt(productQuantityString);
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        }

        if (!TextUtils.isEmpty(supplierNameString)) {
            values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        }

        if (!TextUtils.isEmpty(supplierPhoneString)) {
            values.put(ProductEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);
        }

        if (currentProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this,
                        getString(R.string.toast_saving_product_failed),
                        Toast.LENGTH_SHORT).show();
                return false;
            } else {
                Toast.makeText(this,
                        getString(R.string.toast_saving_product_successful),
                        Toast.LENGTH_LONG).show();
                return true;
            }
        } else {
            int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this,
                        getString(R.string.toast_saving_product_failed),
                        Toast.LENGTH_SHORT).show();
                return false;
            } else {
                Toast.makeText(this,
                        getString(R.string.toast_saving_product_successful),
                        Toast.LENGTH_LONG).show();
                return true;
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE };
        return new CursorLoader(this, currentProductUri, projection,
                               null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }
        if (data.moveToFirst()) {
            int productNameColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int productPriceColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int productQuantityColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE);
            productName.setText(data.getString(productNameColumnIndex));
            productPrice.setText(data.getString(productPriceColumnIndex));
            productQuantity.setText(data.getString(productQuantityColumnIndex));
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
