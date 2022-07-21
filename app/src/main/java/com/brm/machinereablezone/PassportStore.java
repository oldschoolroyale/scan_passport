package com.brm.machinereablezone;

import android.content.Context;
import android.content.SharedPreferences;

public class PassportStore {
    private String challenge;
    private Context context;
    private String dateOfBirth;
    private String dateOfExpiry;
    private String passportNumber;
    private SharedPreferences sharedPassportPref;

    public PassportStore(Context context2) {
        this.context = context2;
        SharedPreferences sharedPreferences = context2.getSharedPreferences(context2.getResources().getString(R.string.passport_info_shared_pref), 0);
        this.sharedPassportPref = sharedPreferences;
        this.passportNumber = sharedPreferences.getString(context2.getResources().getString(R.string.passport_number_shared_pref), BuildConfig.FLAVOR);
        this.dateOfBirth = this.sharedPassportPref.getString(context2.getResources().getString(R.string.passport_date_of_birth_shared_pref), BuildConfig.FLAVOR);
        this.dateOfExpiry = this.sharedPassportPref.getString(context2.getResources().getString(R.string.passport_date_of_expiry_shared_pref), BuildConfig.FLAVOR);
    }

    public void persist() {
        SharedPreferences.Editor edit = this.sharedPassportPref.edit();
        edit.putString(this.context.getResources().getString(R.string.passport_number_shared_pref), this.passportNumber);
        edit.putString(this.context.getResources().getString(R.string.passport_date_of_birth_shared_pref), this.dateOfBirth);
        edit.putString(this.context.getResources().getString(R.string.passport_date_of_expiry_shared_pref), this.dateOfExpiry);
        edit.commit();
    }

    public String getPassportNumber() {
        return this.passportNumber;
    }

    public void setPassportNumber(String str) {
        this.passportNumber = str;
    }

    public void setChallenge(String str) {
        this.challenge = str;
    }

    public String getChallenge() {
        return this.challenge;
    }

    public String getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(String str) {
        this.dateOfBirth = str;
    }

    public String getDateOfExpiry() {
        return this.dateOfExpiry;
    }

    public void setDateOfExpiry(String str) {
        this.dateOfExpiry = str;
    }
}
