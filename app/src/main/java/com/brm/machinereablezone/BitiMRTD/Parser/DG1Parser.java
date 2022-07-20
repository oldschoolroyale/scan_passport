package com.brm.machinereablezone.BitiMRTD.Parser;

import com.brm.machinereablezone.BitiMRTD.Tools.C0464Tools;
import com.brm.machinereablezone.BuildConfig;

import java.util.Arrays;

public class DG1Parser {
    private String dateOfBirth;
    private String dateOfBirthCheckDigit;
    private String dateOfExpiry;
    private String dateOfExpiryCheckDigit;
    private String documentCode;
    private String documentNumber;
    private String documentNumberCheckDigit;
    private String gender;
    private String givenNames;
    private String issuingStateCode;
    private String mrz;
    private String nationalityCode;
    private String surname;
    private C0464Tools tools = new C0464Tools();

    public DG1Parser(byte[] bArr) {
        this.mrz = Arrays.toString(bArr);
        clean();
        TagParser geTag = new TagParser(bArr).geTag("61").geTag("5F1F");
        buildTD1(geTag.getBytes());
        if (isCorrect()) {
            System.out.println("Detected TD1 format");
            return;
        }
        buildTD2(geTag.getBytes());
        if (isCorrect()) {
            System.out.println("Detected TD2 format");
            return;
        }
        clean();
        buildTD3(geTag.getBytes());
        if (isCorrect()) {
            System.out.println("Detected TD3 format");
        } else {
            System.out.println("Couldn't find TD format");
        }
    }

    public boolean isCorrect() {
        if (getDocumentCode() == null || getDocumentCode().equals(BuildConfig.FLAVOR)) {
            System.out.println("DG1 object verification failed at document code");
            return false;
        } else if (getIssuingStateCode() == null || getIssuingStateCode().equals(BuildConfig.FLAVOR)) {
            System.out.println("DG1 object verification failed at issuing state code");
            return false;
        } else if (getDocumentNumber() == null || getDocumentNumber().equals(BuildConfig.FLAVOR)) {
            System.out.println("DG1 object verification failed at document number");
            return false;
        } else if (getDocumentNumberCheckDigit() == null || getDocumentNumberCheckDigit().equals(BuildConfig.FLAVOR)) {
            System.out.println("DG1 object verification failed at document number check digit");
            return false;
        } else if (getGender() == null || getGender().equals(BuildConfig.FLAVOR)) {
            System.out.println("DG1 object verification failed at gender");
            return false;
        } else if (getGivenNames() == null || getGivenNames().equals(BuildConfig.FLAVOR)) {
            System.out.println("DG1 object verification failed at given names");
            return false;
        } else if (getSurname() == null || getSurname().equals(BuildConfig.FLAVOR)) {
            System.out.println("DG1 object verification failed at surname");
            return false;
        } else if (getNationalityCode() == null || getNationalityCode().equals(BuildConfig.FLAVOR)) {
            System.out.println("DG1 object verification failed at nationality code");
            return false;
        } else if (getDateOfBirth() == null || getDateOfBirth().equals(BuildConfig.FLAVOR)) {
            System.out.println("DG1 object verification failed at date of birth");
            return false;
        } else if (getDateOfBirthCheckDigit() == null || getDateOfBirthCheckDigit().equals(BuildConfig.FLAVOR)) {
            System.out.println("DG1 object verification failed at date of birth check digit");
            return false;
        } else if (getDateOfExpiry() == null || getDateOfExpiry().equals(BuildConfig.FLAVOR)) {
            System.out.println("DG1 object verification failed at date of expiry");
            return false;
        } else if (getDateOfExpiryCheckDigit() == null || getDateOfExpiryCheckDigit().equals(BuildConfig.FLAVOR)) {
            System.out.println("DG1 object verification failed at date of expiry check digit");
            return false;
        } else if (!String.valueOf(this.tools.calculateMrzCheckDigit(getDocumentNumber())).equals(getDocumentNumberCheckDigit())) {
            System.out.println("DG1 object verification failed to verify document number check digit");
            return false;
        } else if (!String.valueOf(this.tools.calculateMrzCheckDigit(getDateOfBirth())).equals(getDateOfBirthCheckDigit())) {
            System.out.println("DG1 object verification failed to verify date of birth check digit");
            return false;
        } else if (String.valueOf(this.tools.calculateMrzCheckDigit(getDateOfExpiry())).equals(getDateOfExpiryCheckDigit())) {
            return true;
        } else {
            System.out.println("DG1 object verification failed to verify date of expiry check digit");
            return false;
        }
    }

    public void clean() {
        setDocumentCode(BuildConfig.FLAVOR);
        setIssuingStateCode(BuildConfig.FLAVOR);
        setDocumentNumber(BuildConfig.FLAVOR);
        setGender(BuildConfig.FLAVOR);
        setGivenNames(BuildConfig.FLAVOR);
        setSurname(BuildConfig.FLAVOR);
        setNationalityCode(BuildConfig.FLAVOR);
        setDateOfBirth(BuildConfig.FLAVOR);
        setDateOfExpiry(BuildConfig.FLAVOR);
    }

    private String readSection(byte[] bArr, int i, int i2) {
        int i3 = i2 + i;
        if (bArr.length < i3) {
            System.out.println("Tried to read out of range section");
            return null;
        }
        String replace = new String(Arrays.copyOfRange(bArr, i, i3)).replace("<", BuildConfig.FLAVOR);
        System.out.println("result : ".concat(replace));
        return replace;
    }

    private String[] parseName(byte[] bArr) {
        String[] split = new String(bArr).split("<<");
        if (split.length < 2) {
            return null;
        }
        return split;
    }

    private String parseGivenNames(byte[] bArr) {
        String[] parseName = parseName(bArr);
        if (parseName == null) {
            return null;
        }
        return parseName[1].replace("<", " ");
    }

    private String parseSurname(byte[] bArr) {
        String[] parseName = parseName(bArr);
        if (parseName == null) {
            return null;
        }
        return parseName[0].replace("<", " ");
    }

    private void buildTD1(byte[] bArr) {
        if (bArr.length < 93) {
            System.out.println("Skip TD1 build, mrz is too short");
            return;
        }
        this.documentCode = readSection(bArr, 0, 2);
        this.issuingStateCode = readSection(bArr, 2, 3);
        this.documentNumber = readSection(bArr, 5, 9);
        this.documentNumberCheckDigit = readSection(bArr, 14, 1);
        this.dateOfBirth = readSection(bArr, 30, 6);
        this.dateOfBirthCheckDigit = readSection(bArr, 36, 1);
        this.gender = readSection(bArr, 37, 1);
        this.dateOfExpiry = readSection(bArr, 38, 6);
        this.dateOfExpiryCheckDigit = readSection(bArr, 44, 1);
        this.nationalityCode = readSection(bArr, 45, 3);
        this.surname = parseSurname(Arrays.copyOfRange(bArr, 60, 90));
        this.givenNames = parseGivenNames(Arrays.copyOfRange(bArr, 60, 90));
    }

    private void buildTD2(byte[] bArr) {
        if (bArr.length < 67) {
            System.out.println("Skip TD2 build, mrz is too short");
            return;
        }
        this.documentCode = readSection(bArr, 0, 2);
        this.issuingStateCode = readSection(bArr, 2, 3);
        this.surname = parseSurname(Arrays.copyOfRange(bArr, 5, 36));
        this.givenNames = parseGivenNames(Arrays.copyOfRange(bArr, 5, 36));
        this.documentNumber = readSection(bArr, 36, 9);
        this.documentNumberCheckDigit = readSection(bArr, 45, 1);
        this.nationalityCode = readSection(bArr, 46, 3);
        this.dateOfBirth = readSection(bArr, 49, 6);
        this.dateOfBirthCheckDigit = readSection(bArr, 55, 1);
        this.gender = readSection(bArr, 56, 1);
        this.dateOfExpiry = readSection(bArr, 57, 6);
        this.dateOfExpiryCheckDigit = readSection(bArr, 63, 1);
    }

    private void buildTD3(byte[] bArr) {
        if (bArr.length < 75) {
            System.out.println("Skip TD3 build, mrz is too short");
            return;
        }
        this.documentCode = readSection(bArr, 0, 2);
        this.issuingStateCode = readSection(bArr, 2, 3);
        this.surname = parseSurname(Arrays.copyOfRange(bArr, 5, 44));
        this.givenNames = parseGivenNames(Arrays.copyOfRange(bArr, 5, 44));
        this.documentNumber = readSection(bArr, 44, 9);
        this.documentNumberCheckDigit = readSection(bArr, 53, 1);
        this.nationalityCode = readSection(bArr, 54, 3);
        this.dateOfBirth = readSection(bArr, 57, 6);
        this.dateOfBirthCheckDigit = readSection(bArr, 63, 1);
        this.gender = readSection(bArr, 64, 1);
        this.dateOfExpiry = readSection(bArr, 65, 6);
        this.dateOfExpiryCheckDigit = readSection(bArr, 71, 1);
    }

    public String getMRZ() {
        return new String(this.mrz);
    }

    public void setMRZ(String str) {
        this.mrz = str;
    }

    public String getDocumentCode() {
        return this.documentCode;
    }

    public void setDocumentCode(String str) {
        this.documentCode = str;
    }

    public String getIssuingStateCode() {
        return this.issuingStateCode;
    }

    public void setIssuingStateCode(String str) {
        this.issuingStateCode = str;
    }

    public String getDocumentNumber() {
        return this.documentNumber;
    }

    public void setDocumentNumber(String str) {
        this.documentNumber = str;
    }

    public String getDocumentNumberCheckDigit() {
        return this.documentNumberCheckDigit;
    }

    public void setDocumentNumberCheckDigit(String str) {
        this.documentNumberCheckDigit = str;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String str) {
        this.gender = str;
    }

    public String getGivenNames() {
        return this.givenNames;
    }

    public void setGivenNames(String str) {
        this.givenNames = str;
    }

    public String getSurname() {
        return this.surname;
    }

    public void setSurname(String str) {
        this.surname = str;
    }

    public String getNationalityCode() {
        return this.nationalityCode;
    }

    public void setNationalityCode(String str) {
        this.nationalityCode = str;
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

    public String getDateOfBirthCheckDigit() {
        return this.dateOfBirthCheckDigit;
    }

    public void setDateOfBirthCheckDigit(String str) {
        this.dateOfBirthCheckDigit = str;
    }

    public String getDateOfExpiryCheckDigit() {
        return this.dateOfExpiryCheckDigit;
    }

    public void setDateOfExpiryCheckDigit(String str) {
        this.dateOfExpiryCheckDigit = str;
    }
}
