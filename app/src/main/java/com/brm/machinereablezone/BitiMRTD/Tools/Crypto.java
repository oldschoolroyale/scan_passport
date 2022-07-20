package com.brm.machinereablezone.BitiMRTD.Tools;

import com.brm.machinereablezone.BuildConfig;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {

    /* renamed from: C1 */
    protected byte[] f37C1 = {0, 0, 0, 1};

    /* renamed from: C2 */
    protected byte[] f38C2 = {0, 0, 0, 2};
    protected byte[] ivValue = {0, 0, 0, 0, 0, 0, 0, 0};
    protected C0464Tools tools = new C0464Tools();

    public byte[] generateRandomBytes(int i) {
        byte[] bArr = new byte[i];
        new SecureRandom().nextBytes(bArr);
        return bArr;
    }

    public byte[] calculateSeed(String str, String str2, String str3) {
        return Arrays.copyOfRange(sha1(BuildConfig.FLAVOR.concat(str).concat(String.valueOf(this.tools.calculateMrzCheckDigit(str))).concat(str2).concat(String.valueOf(this.tools.calculateMrzCheckDigit(str2))).concat(str3).concat(String.valueOf(this.tools.calculateMrzCheckDigit(str3))).getBytes()), 0, 16);
    }

    public byte[] calculateMacKey(byte[] bArr) {
        C0464Tools tools2 = this.tools;
        byte[] adjustParityBits = tools2.adjustParityBits(Arrays.copyOfRange(sha1(tools2.concatByteArrays(bArr, this.f38C2)), 0, 16));
        System.out.println("MacKey : ".concat(this.tools.bytesToString(adjustParityBits)));
        return adjustParityBits;
    }

    public byte[] sha1(byte[] bArr) {
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-1");
            instance.update(bArr);
            return instance.digest();
        } catch (Exception e) {
            if (e.getMessage() == null) {
                return null;
            }
            System.out.println(e.getMessage());
            return null;
        }
    }

    public byte[] padData(byte[] bArr, int i) {
        C0464Tools tools2 = this.tools;
        byte[] concatByteArrays = tools2.concatByteArrays(bArr, tools2.byteToBytes(Byte.MIN_VALUE));
        while (concatByteArrays.length % i != 0) {
            C0464Tools tools3 = this.tools;
            concatByteArrays = tools3.concatByteArrays(concatByteArrays, tools3.byteToBytes((byte) 0));
        }
        return concatByteArrays;
    }

    public byte[] calculate3DESEncryptionKey(byte[] bArr) {
        C0464Tools tools2 = this.tools;
        byte[] adjustParityBits = tools2.adjustParityBits(Arrays.copyOfRange(sha1(tools2.concatByteArrays(bArr, this.f37C1)), 0, 16));
        byte[] concatByteArrays = this.tools.concatByteArrays(adjustParityBits, Arrays.copyOfRange(adjustParityBits, 0, 8));
        System.out.println("EncKey 16 Bytes : ".concat(this.tools.bytesToString(adjustParityBits)));
        System.out.println("EncKey 24 Bytes : ".concat(this.tools.bytesToString(concatByteArrays)));
        return concatByteArrays;
    }

    public byte[] calculateAESEncryptionKey(byte[] bArr) {
        C0464Tools tools2 = this.tools;
        return tools2.adjustParityBits(Arrays.copyOfRange(sha1(tools2.concatByteArrays(bArr, this.f37C1)), 0, 16));
    }

    public byte[] encryptUsingDES(byte[] bArr, byte[] bArr2) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr, "DES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(this.ivValue);
            Cipher instance = Cipher.getInstance("DES/CBC/NoPadding");
            instance.init(1, secretKeySpec, ivParameterSpec);
            return instance.doFinal(bArr2);
        } catch (Exception e) {
            if (e.getMessage() == null) {
                return null;
            }
            System.out.println(e.getMessage());
            return null;
        }
    }

    public byte[] decryptUsingDES(byte[] bArr, byte[] bArr2) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr, "DES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(this.ivValue);
            Cipher instance = Cipher.getInstance("DES/CBC/NoPadding");
            instance.init(2, secretKeySpec, ivParameterSpec);
            return instance.doFinal(bArr2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public byte[] calculate3DESMac(byte[] bArr, byte[] bArr2, boolean z) {
        int i = 8;
        byte[] copyOfRange = Arrays.copyOfRange(bArr, 0, 8);
        byte[] copyOfRange2 = Arrays.copyOfRange(bArr, 8, 16);
        if (z) {
            bArr2 = padData(bArr2, 8);
        }
        byte[] encryptUsingDES = encryptUsingDES(copyOfRange, Arrays.copyOfRange(bArr2, 0, 8));
        while (true) {
            int i2 = i + 8;
            if (i2 > bArr2.length) {
                return encryptUsingDES(copyOfRange, decryptUsingDES(copyOfRange2, encryptUsingDES));
            }
            encryptUsingDES = encryptUsingDES(copyOfRange, this.tools.doXor(encryptUsingDES, Arrays.copyOfRange(bArr2, i, i2)));
            i = i2;
        }
    }

    public byte[] calculateAESMac(byte[] bArr, byte[] bArr2, boolean z) {
        return Arrays.copyOfRange(AESCMAC.get(bArr, bArr2), 0, 8);
    }

    public byte[] encrypt3DES(byte[] bArr, byte[] bArr2) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr, "DESede");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(this.ivValue);
            Cipher instance = Cipher.getInstance("DESede/CBC/NoPadding");
            instance.init(1, secretKeySpec, ivParameterSpec);
            return instance.doFinal(bArr2);
        } catch (Exception e) {
            if (e.getMessage() == null) {
                return null;
            }
            System.out.println(e.getMessage());
            return null;
        }
    }

    public byte[] encryptAES(byte[] bArr, byte[] bArr2) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(this.ivValue);
            Cipher instance = Cipher.getInstance("AES/CBC/NoPadding");
            instance.init(1, secretKeySpec, ivParameterSpec);
            return instance.doFinal(bArr2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public byte[] decrypt3DES(byte[] bArr, byte[] bArr2) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr, "DESede");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(this.ivValue);
            Cipher instance = Cipher.getInstance("DESede/CBC/NoPadding");
            instance.init(2, secretKeySpec, ivParameterSpec);
            return instance.doFinal(bArr2);
        } catch (Exception e) {
            if (e.getMessage() == null) {
                return null;
            }
            System.out.println(e.getMessage());
            return null;
        }
    }

    public byte[] decryptAES(byte[] bArr, byte[] bArr2) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(this.ivValue);
            Cipher instance = Cipher.getInstance("AES/CBC/NoPadding");
            instance.init(2, secretKeySpec, ivParameterSpec);
            return instance.doFinal(bArr2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
