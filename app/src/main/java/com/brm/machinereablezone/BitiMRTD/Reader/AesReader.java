package com.brm.machinereablezone.BitiMRTD.Reader;

import java.util.Arrays;

public class AesReader extends AbstractReader {
    public byte[] calculateEncryptionKey(byte[] bArr) {
        return this.crypto.calculateAESEncryptionKey(bArr);
    }

    public byte[] encrypt(byte[] bArr, byte[] bArr2) {
        return this.crypto.encryptAES(bArr, bArr2);
    }

    public byte[] decrypt(byte[] bArr, byte[] bArr2) {
        return this.crypto.decryptAES(bArr, bArr2);
    }

    public byte[] calculateMac(byte[] bArr, byte[] bArr2) {
        return calculateMac(bArr, bArr2, true);
    }

    public byte[] calculateMac(byte[] bArr, byte[] bArr2, boolean z) {
        return this.crypto.calculateAESMac(bArr, bArr2, z);
    }

    public byte[] calculateSequenceCounter(byte[] bArr) {
        byte[] copyOfRange = Arrays.copyOfRange(bArr, 0, 16);
        System.out.println("Sequence counter: ".concat(this.tools.bytesToString(copyOfRange)));
        return copyOfRange;
    }

    public byte[] padData(byte[] bArr) {
        return this.crypto.padData(bArr, 16);
    }
}
