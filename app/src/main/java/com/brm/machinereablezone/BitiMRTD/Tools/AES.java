package com.brm.machinereablezone.BitiMRTD.Tools;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    public static byte[] encrypt(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(bArr);
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr2, "AES");
            Cipher instance = Cipher.getInstance("AES/CBC/NoPadding");
            instance.init(1, secretKeySpec, ivParameterSpec);
            return instance.doFinal(bArr3);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] decrypt(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(bArr);
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr2, "AES");
            Cipher instance = Cipher.getInstance("AES/CBC/NoPadding");
            instance.init(2, secretKeySpec, ivParameterSpec);
            return instance.doFinal(bArr3);
        } catch (Exception unused) {
            return null;
        }
    }

    public static byte[] decrypt(byte[] bArr, byte[] bArr2, byte[] bArr3, int i, int i2) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(bArr);
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr2, "AES");
            Cipher instance = Cipher.getInstance("AES/CBC/NoPadding");
            instance.init(2, secretKeySpec, ivParameterSpec);
            return instance.doFinal(bArr3, i, i2);
        } catch (Exception unused) {
            return null;
        }
    }
}
