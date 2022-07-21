package com.brm.machinereablezone.ui.nfc;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.brm.machinereablezone.R;
import com.brm.machinereablezone.utils.TagProvider;

import org.spongycastle.asn1.cmp.PKIFailureInfo;

public abstract class AbstractNfcActivity extends AppCompatActivity {
    protected NfcAdapter mNfcAdapter;
    protected PendingIntent pendingIntent;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        NfcAdapter defaultAdapter = NfcAdapter.getDefaultAdapter(this);
        this.mNfcAdapter = defaultAdapter;
        if (defaultAdapter == null || !defaultAdapter.isEnabled()) {
            System.out.println("failed to get NFC adapter, NFC disabled?");
            new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.error_nfc))
                    .setMessage(getResources().getString(R.string.error_nfc_is_disabled)).setCancelable(true)
                    .setPositiveButton("enable", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    AbstractNfcActivity.this.startActivity(new Intent("android.settings.NFC_SETTINGS"));
                }
            }).create().show();
            return;
        }
        this.pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
                .addFlags(PKIFailureInfo.duplicateCertReq), 0);
    }

    public void onPause() {
        super.onPause();
        this.mNfcAdapter.disableForegroundDispatch(this);
    }

    public void onResume() {
        super.onResume();
        try {
            if (this.mNfcAdapter != null && this.pendingIntent != null) {
                this.mNfcAdapter.enableForegroundDispatch(this, this.pendingIntent, (IntentFilter[]) null, (String[][]) null);
            }
        } catch (Exception unused) {
            System.out.println("onResume error");
        }
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        TagProvider.setTag(IsoDep.get((Tag) intent.getParcelableExtra("android.nfc.extra.TAG")));
        System.out.println("Got new intent!");
        startProcess();
    }

    public abstract void startProcess();
}