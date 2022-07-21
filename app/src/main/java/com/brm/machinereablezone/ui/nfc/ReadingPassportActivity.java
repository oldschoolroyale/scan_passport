package com.brm.machinereablezone.ui.nfc;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.brm.machinereablezone.BitiMRTD.Constants.MrtdConstants;
import com.brm.machinereablezone.BitiMRTD.Parser.DG1Parser;
import com.brm.machinereablezone.BitiMRTD.Reader.BacInfo;
import com.brm.machinereablezone.BitiMRTD.Reader.DESedeReader;
import com.brm.machinereablezone.BitiMRTD.Reader.ProgressListenerInterface;
import com.brm.machinereablezone.BitiMRTD.Tools.C0464Tools;
import com.brm.machinereablezone.R;
import com.brm.machinereablezone.ui.result.ResultActivity;
import com.brm.machinereablezone.utils.TagProvider;

import java.io.Serializable;
import java.lang.ref.WeakReference;

public class ReadingPassportActivity extends AbstractNfcActivity implements Serializable {
    private AsyncReader asyncReader;
    private String dateOfBirth;
    private String dateOfExpiration;
    private byte[] dg1;
    private byte[] dg2;
    /* access modifiers changed from: private */
    public boolean isActivityRunning;
    private ProgressBar mrtdProgressBar;
    private String passportNumber;
    private byte[] sod;
    private LottieAnimationView lottieAnim;
    private TextView helperTv;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_reading_passport);
        this.isActivityRunning = true;
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (Exception unused) {
        }
        this.passportNumber = (String) getIntent().getSerializableExtra("passportNumber");
        this.dateOfBirth = (String) getIntent().getSerializableExtra("dateOfBirth");
        this.dateOfExpiration = (String) getIntent().getSerializableExtra("dateOfExpiration");
        this.mrtdProgressBar = findViewById(R.id.mrtdProgressBar);
        this.lottieAnim = findViewById(R.id.lottieAnim);
        this.helperTv = findViewById(R.id.helper_tv);
    }

    /* access modifiers changed from: protected */
    public void readNfc() {
        System.out.println("Read nfc");
        lottieAnim.setAnimation(R.raw.scan_passport);
        helperTv.setText("Working...\nDon't move");
        setMrtdProgressBarPercentage(2);
        AsyncReader asyncReader2 = new AsyncReader(this, this.passportNumber, this.dateOfBirth, this.dateOfExpiration);
        this.asyncReader = asyncReader2;
        asyncReader2.execute(new Void[0]);
    }



    public void showResult() {
        if (this.dg1 == null || this.dg2 == null) {
            System.out.println("dg1 or/and dg2 is/are null");
            return;
        }
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("dg1", this.dg1);
        intent.putExtra("dg2", this.dg2);
        intent.putExtra("sod", this.sod);
        setMrtdProgressBarPercentage(96);
        startActivity(intent);
        setMrtdProgressBarPercentage(100);
    }

    public void showError(final String str) {
        runOnUiThread(() -> {
            if (ReadingPassportActivity.this.isActivityRunning) {
                new AlertDialog.Builder(ReadingPassportActivity.this)
                        .setTitle(ReadingPassportActivity.this.getResources().getString(R.string.error_nfc))
                        .setMessage(str).setCancelable(false).setPositiveButton("ok",
                                (dialogInterface, i) -> ReadingPassportActivity.this.finish()).create().show();
            }
        });
    }

    public void setMrtdProgressBarPercentage(int i) {
        this.mrtdProgressBar.setProgress(i);
        ObjectAnimator animation = ObjectAnimator.ofInt(this.mrtdProgressBar, "progress", 0, 100);
        animation.setDuration(2000); // in milliseconds
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    public void setDg1(byte[] bArr) {
        this.dg1 = bArr;
    }

    public void setDg2(byte[] bArr) {
        this.dg2 = bArr;
    }

    public void setSOD(byte[] bArr) {
        this.sod = bArr;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        this.isActivityRunning = false;
        this.asyncReader.cancel();
        finish();
        return true;
    }

    public void onPause() {
        super.onPause();
        if (isFinishing()) {
            this.isActivityRunning = false;
            this.asyncReader.cancel();
        }
    }

    @Override
    public void startProcess() {
        readNfc();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        this.isActivityRunning = false;
        this.asyncReader.cancel();
    }


    private class AsyncReader extends AsyncTask<Void, Integer, Boolean> implements ProgressListenerInterface {
        private int currentStep = 0;
        private String dateOfBirth;
        private String dateOfExpiration;
        private boolean isCanceled = false;
        private String passportNumber;
        private WeakReference<ReadingPassportActivity> readingPassportActivity;
        private boolean success = false;

        public AsyncReader(ReadingPassportActivity readingPassportActivity2, String str, String str2, String str3) {
            this.passportNumber = str;
            this.dateOfBirth = str2;
            this.dateOfExpiration = str3;
            this.isCanceled = false;
            link(readingPassportActivity2);
        }

        /* access modifiers changed from: protected */
        public Boolean doInBackground(Void... voidArr) {
            try {
                if (TagProvider.getTag() != null) {
                    System.out.println("GOT TAG");
                    BacInfo bacInfo = new BacInfo();
                    bacInfo.setPassportNbr(this.passportNumber);
                    bacInfo.setDateOfBirth(this.dateOfBirth);
                    bacInfo.setDateOfExpiry(this.dateOfExpiration);
                    DESedeReader dESedeReader = new DESedeReader();
                    dESedeReader.setBacInfo(bacInfo);
                    if (dESedeReader.initSession()) {
                        dESedeReader.setProgressListener(new WeakReference(this));
                        this.readingPassportActivity.get().setMrtdProgressBarPercentage(5);
                        this.currentStep = 1;
                        byte[] readFile = dESedeReader.readFile(MrtdConstants.FID_DG1);
                        if (readFile == null) {
                            this.readingPassportActivity.get().showError(ReadingPassportActivity.this.getResources().getString(R.string.error_dg1_is_null));
                        }
                        this.readingPassportActivity.get().setMrtdProgressBarPercentage(10);
                        this.readingPassportActivity.get().setDg1(readFile);
                        new DG1Parser(readFile);
                        new C0464Tools();
                        this.currentStep = 2;
                        byte[] readFile2 = dESedeReader.readFile(MrtdConstants.FID_DG2);
                        if (readFile2 == null) {
                            ((ReadingPassportActivity) this.readingPassportActivity.get()).showError(ReadingPassportActivity.this.getResources().getString(R.string.error_dg2_is_null));
                        }
                        this.readingPassportActivity.get().setDg2(readFile2);
                        this.currentStep = 3;
                        this.readingPassportActivity.get().setSOD(dESedeReader.readFile(MrtdConstants.FID_EF_SOD));
                        if (!(readFile == null || readFile2 == null)) {
                            this.success = true;
                        }
                        if (!this.success) {
                            this.readingPassportActivity.get().showError(ReadingPassportActivity.this.getResources().getString(R.string.error_nfc_exception));
                        }
                        this.readingPassportActivity.get().setMrtdProgressBarPercentage(95);
                        return true;
                    }
                    System.out.println("Failed to init session");
                    this.readingPassportActivity.get().showError(ReadingPassportActivity.this.getResources().getString(R.string.error_mutual_authentication_failed));
                    TagProvider.closeTag();
                    return false;
                }
                System.out.println("Couldn't get Tag from intent");
                this.readingPassportActivity.get().showError(ReadingPassportActivity.this.getResources().getString(R.string.error_lost_connexion));
                return false;
            } catch (Exception unused) {
                System.out.println("Exception");
                this.readingPassportActivity.get().showError(ReadingPassportActivity.this.getResources().getString(R.string.error_nfc_exception));
                return false;
            }
        }

        public void link(ReadingPassportActivity readingPassportActivity2) {
            this.readingPassportActivity = new WeakReference<>(readingPassportActivity2);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Boolean bool) {
            if (this.success) {
                this.readingPassportActivity.get().showResult();
                this.readingPassportActivity.get().finish();
            }
        }

        public void updateProgress(int i) {
            int i2 = this.currentStep;
            if (i2 == 1) {
                this.readingPassportActivity.get().setMrtdProgressBarPercentage(Math.round((float) ((i * 10) / 100)));
            } else if (i2 == 2) {
                this.readingPassportActivity.get().setMrtdProgressBarPercentage(Math.round((float) ((i * 75) / 100)) + 10);
            } else if (i2 == 3) {
                this.readingPassportActivity.get().setMrtdProgressBarPercentage(Math.round((float) ((i * 10) / 100)) + 85);
            }
        }

        public void cancel() {
            this.isCanceled = true;
        }

        /* access modifiers changed from: protected */
        public void onCancelled() {
            this.isCanceled = true;
            super.onCancelled();
        }

        public boolean isCanceled() {
            return this.isCanceled;
        }
    }
}
