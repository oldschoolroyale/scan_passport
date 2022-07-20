package com.brm.machinereablezone.BitiMRTD.Reader;

import java.util.Arrays;

public class DESedeReader extends AbstractReader {
    public byte[] calculateEncryptionKey(byte[] bArr) {
        return this.crypto.calculate3DESEncryptionKey(bArr);
    }

    public byte[] encrypt(byte[] bArr, byte[] bArr2) {
        return this.crypto.encrypt3DES(bArr, bArr2);
    }

    public byte[] decrypt(byte[] bArr, byte[] bArr2) {
        return this.crypto.decrypt3DES(bArr, bArr2);
    }

    public byte[] calculateMac(byte[] bArr, byte[] bArr2) {
        return calculateMac(bArr, bArr2, true);
    }

    public byte[] calculateMac(byte[] bArr, byte[] bArr2, boolean z) {
        return this.crypto.calculate3DESMac(bArr, bArr2, z);
    }

    public byte[] calculateSequenceCounter(byte[] bArr) {
        byte[] concatByteArrays = this.tools.concatByteArrays(Arrays.copyOfRange(bArr, 4, 8), Arrays.copyOfRange(bArr, 12, 16));
        System.out.println("Sequence counter: ".concat(this.tools.bytesToString(concatByteArrays)));
        return concatByteArrays;
    }

    public byte[] padData(byte[] bArr) {
        return this.crypto.padData(bArr, 8);
    }
}
