package com.brm.machinereablezone.BitiMRTD.NFC;

import com.brm.machinereablezone.BitiMRTD.Tools.C0464Tools;

public class Apdu {
    protected boolean apduWithLe = true;
    protected C0464Tools tools = new C0464Tools();

    public byte[] buildApduCommand(byte b, byte b2, byte b3, byte b4, byte[] bArr, byte b5) {
        byte[] bArr2 = {b};
        byte[] bArr3 = {b2};
        byte[] bArr4 = {b3};
        byte[] bArr5 = {b4};
        byte[] bArr6 = {(byte) bArr.length};
        byte[] bArr7 = {b5};
        byte[] concatByteArrays = this.tools.concatByteArrays(this.tools.concatByteArrays(this.tools.concatByteArrays(this.tools.concatByteArrays(this.tools.concatByteArrays(bArr2, bArr3), bArr4), bArr5), bArr6), bArr);
        if (this.apduWithLe) {
            concatByteArrays = this.tools.concatByteArrays(concatByteArrays, bArr7);
        }
        System.out.println("APDU command : ".concat(this.tools.bytesToString(concatByteArrays)));
        return concatByteArrays;
    }

    public byte[] buildApduCommand(byte b, byte b2, byte b3, byte b4, byte[] bArr) {
        byte[] bArr2 = {b};
        byte[] bArr3 = {b2};
        byte[] bArr4 = {b3};
        byte[] bArr5 = {b4};
        byte[] bArr6 = {(byte) bArr.length};
        byte[] concatByteArrays = this.tools.concatByteArrays(this.tools.concatByteArrays(this.tools.concatByteArrays(this.tools.concatByteArrays(this.tools.concatByteArrays(bArr2, bArr3), bArr4), bArr5), bArr6), bArr);
        System.out.println("APDU command : ".concat(this.tools.bytesToString(concatByteArrays)));
        return concatByteArrays;
    }

    public void setApduWithLe(boolean z) {
        this.apduWithLe = z;
    }
}
