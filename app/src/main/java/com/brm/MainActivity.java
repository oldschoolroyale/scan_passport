package com.brm;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

import com.brm.machinereablezone.R;
import com.brm.machinereablezone.WaitingForNfcActivity;
import com.brm.machinereablezone.ui.nfc.AbstractNfcActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AbstractNfcActivity implements DatePickerDialog.OnDateSetListener {
    public String dateOfBirth = "000000";
    public int[] dateOfBirthIntArray = {15, 6, 1980};
    public String dateOfExpiration = "000000";
    public int[] dateOfExpirationIntArray = {15, 6, 2020};
    public String passportNumber;
    public int selectedDateField;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_main_2);
        PassportStore passportStore = new PassportStore(getApplicationContext());
        this.passportNumber = passportStore.getPassportNumber();
        this.dateOfBirth = passportStore.getDateOfBirth();
        this.dateOfExpiration = passportStore.getDateOfExpiry();
        if (!this.passportNumber.isEmpty() && !this.dateOfBirth.isEmpty() && !this.dateOfExpiration.isEmpty()) {
            ((EditText) findViewById(R.id.DateOfBirth)).setText(this.dateOfBirth.substring(0, 2).concat(".").concat(this.dateOfBirth.substring(2, 4)).concat(".").concat(this.dateOfBirth.substring(4, 6)));
            ((EditText) findViewById(R.id.DateOfExpiration)).setText(this.dateOfExpiration.substring(0, 2).concat(".").concat(this.dateOfExpiration.substring(2, 4).concat(".").concat(this.dateOfExpiration.substring(4, 6))));
            ((EditText) findViewById(R.id.PassportNbr)).setText(this.passportNumber);
        }
        ((EditText) findViewById(R.id.PassportNbr)).setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        findViewById(R.id.MainLayout).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ((InputMethodManager) MainActivity.this.getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
                MainActivity.this.findViewById(R.id.PassportNbr).clearFocus();
                MainActivity.this.findViewById(R.id.DateOfBirth).clearFocus();
                MainActivity.this.findViewById(R.id.DateOfExpiration).clearFocus();
            }
        });
        ((EditText) findViewById(R.id.DateOfBirth)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View view, boolean z) {
                if (z) {
                    MainActivity.this.selectedDateField = 1;
                    ((InputMethodManager) MainActivity.this.getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
                    MainActivity.this.displayDatePickerDialog();
                }
            }
        });
        ((EditText) findViewById(R.id.DateOfExpiration)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View view, boolean z) {
                if (z) {
                    MainActivity.this.selectedDateField = 2;
                    ((InputMethodManager) MainActivity.this.getSystemService("input_method")).hideSoftInputFromWindow(view.getWindowToken(), 0);
                    MainActivity.this.displayDatePickerDialog();
                }
            }
        });
        findViewById(R.id.ReadNfcBtn).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    MainActivity.this.passportNumber = ((EditText) MainActivity.this.findViewById(R.id.PassportNbr)).getText().toString();
                    PassportStore passportStore = new PassportStore(MainActivity.this.getApplicationContext());
                    passportStore.setDateOfBirth(MainActivity.this.dateOfBirth);
                    passportStore.setDateOfExpiry(MainActivity.this.dateOfExpiration);
                    passportStore.setPassportNumber(MainActivity.this.passportNumber);
                    passportStore.persist();
                    Intent intent = new Intent(MainActivity.this, WaitingForNfcActivity.class);
                    intent.putExtra("passportNumber", ((EditText) MainActivity.this.findViewById(R.id.PassportNbr)).getText().toString());
                    intent.putExtra("dateOfBirth", MainActivity.this.dateOfBirth);
                    intent.putExtra("dateOfExpiration", MainActivity.this.dateOfExpiration);
                    MainActivity.this.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onResume() {
        super.onResume();
        int[] iArr = this.dateOfBirthIntArray;
        if (!(iArr[0] == 15 && iArr[1] == 6 && iArr[2] == 1980)) {
            int[] iArr2 = this.dateOfBirthIntArray;
            setDateToTextView("dob", iArr2[2], iArr2[1], iArr2[0]);
        }
        int[] iArr3 = this.dateOfExpirationIntArray;
        if (iArr3[0] != 15 || iArr3[1] != 6 || iArr3[2] != 2020) {
            int[] iArr4 = this.dateOfExpirationIntArray;
            setDateToTextView("doe", iArr4[2], iArr4[1], iArr4[0]);
        }
    }



    public void displayDatePickerDialog() {
        findViewById(R.id.DateOfBirth).clearFocus();
        findViewById(R.id.DateOfExpiration).clearFocus();
        findViewById(R.id.PassportNbr).clearFocus();
        Calendar.getInstance();
        int[] iArr = this.dateOfExpirationIntArray;
        int i = iArr[2];
        int i2 = iArr[1];
        int i3 = iArr[0];
        if (this.selectedDateField == 1) {
            int[] iArr2 = this.dateOfBirthIntArray;
            i = iArr2[2];
            i2 = iArr2[1];
            i3 = iArr2[0];
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, i, i2, i3);
        datePickerDialog.getDatePicker().setCalendarViewShown(false);
        datePickerDialog.getDatePicker().setSpinnersShown(true);
        datePickerDialog.show();
    }

    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
        if (this.selectedDateField == 1) {
            setDateToTextView("dob", i, i2, i3);
        }
        if (this.selectedDateField == 2) {
            setDateToTextView("doe", i, i2, i3);
        }
    }

    public void setDateToTextView(String str, int i, int i2, int i3) {
        int i4 = i2 + 1;
        try {
            Date parse = new SimpleDateFormat("dd/MM/yyyy").parse(String.valueOf(i3).concat("/").concat(String.valueOf(i4)).concat("/").concat(String.valueOf(i)));
            String format = new SimpleDateFormat("yyMMdd").format(parse);
            String format2 = SimpleDateFormat.getDateInstance().format(parse);
            if (str.equals("dob")) {
                ((EditText) findViewById(R.id.DateOfBirth)).setText(format2);
                this.dateOfBirth = format;
                this.dateOfBirthIntArray[2] = i;
                this.dateOfBirthIntArray[1] = i4 - 1;
                this.dateOfBirthIntArray[0] = i3;
            }
            if (str.equals("doe")) {
                ((EditText) findViewById(R.id.DateOfExpiration)).setText(format2);
                this.dateOfExpiration = format;
                this.dateOfExpirationIntArray[2] = i;
                this.dateOfExpirationIntArray[1] = i4 - 1;
                this.dateOfExpirationIntArray[0] = i3;
            }
        } catch (Exception unused) {
        }
    }
}
