package com.brm.machinereablezone;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.brm.machinereablezone.ui.nfc.AbstractNfcActivity;
import com.brm.machinereablezone.ui.nfc.ReadingPassportActivity;
import com.brm.machinereablezone.utils.TagProvider;

import java.io.Serializable;

public class WaitingForNfcActivity extends AbstractNfcActivity implements Serializable {
    private String dateOfBirth;
    private String dateOfExpiration;
    private String passportNumber;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_waiting_for_nfc);
        this.passportNumber = (String) getIntent().getSerializableExtra("passportNumber");
        this.dateOfBirth = (String) getIntent().getSerializableExtra("dateOfBirth");
        this.dateOfExpiration = (String) getIntent().getSerializableExtra("dateOfExpiration");
        if (TagProvider.isTagReady()) {
            readPassport();
        }
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        readPassport();
    }

    private void readPassport() {
        Intent intent = new Intent(this, ReadingPassportActivity2.class);
        intent.putExtra("passportNumber", this.passportNumber);
        intent.putExtra("dateOfBirth", this.dateOfBirth);
        intent.putExtra("dateOfExpiration", this.dateOfExpiration);
        startActivity(intent);
        finish();
    }
}
