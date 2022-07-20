package com.brm.machinereablezone.BitiMRTD.Reader;


import com.brm.machinereablezone.BuildConfig;

public class BacInfo {
    private String dateOfBirth;
    private String dateOfExpiry;
    private String passportNbr;

    public void setPassportNbr(String str) {
        String replace = str.replace(" ", BuildConfig.FLAVOR);
        while (replace.length() < 9) {
            replace = replace.concat("<");
        }
        this.passportNbr = replace;
    }

    public String getPassportNbr() {
        return this.passportNbr;
    }

    public void setDateOfBirth(String str) {
        this.dateOfBirth = str;
    }

    public String getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfExpiry(String str) {
        this.dateOfExpiry = str;
    }

    public String getDateOfExpiry() {
        return this.dateOfExpiry;
    }

    @Override
    public String toString() {
        return "BacInfo{" +
                "dateOfBirth='" + dateOfBirth + '\'' +
                ", dateOfExpiry='" + dateOfExpiry + '\'' +
                ", passportNbr='" + passportNbr + '\'' +
                '}';
    }
}
