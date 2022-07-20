package com.brm.machinereablezone.BitiMRTD.NFC;


import com.brm.machinereablezone.BitiMRTD.Tools.C0464Tools;

public class Doc9303Apdu {
    public static final byte POW_2_WIDTH = 16;
    protected byte[] AID = {0, -92, 4, 12, 7, -96, 0, 0, 2, 71, POW_2_WIDTH, 1};
    protected C0464Tools tools = new C0464Tools();

    public byte[] getAID() {
        return this.AID;
    }

    public byte[] buildDO87(byte[] bArr) {
        C0464Tools tools2 = this.tools;
        byte[] concatByteArrays = tools2.concatByteArrays(tools2.byteToBytes((byte) 1), bArr);
        byte[] concatByteArrays2 = this.tools.concatByteArrays(this.tools.calculateAsn1Length(concatByteArrays), concatByteArrays);
        C0464Tools tools3 = this.tools;
        return tools3.concatByteArrays(tools3.byteToBytes((byte) -121), concatByteArrays2);
    }

    public byte[] buildDO97(int i) {
        C0464Tools tools2 = this.tools;
        return tools2.concatByteArrays(new byte[]{-105, 1}, tools2.byteToBytes((byte) i));
    }

    public byte[] buildDO8E(byte[] bArr) {
        byte[] concatByteArrays = this.tools.concatByteArrays(this.tools.calculateAsn1Length(bArr), bArr);
        C0464Tools tools2 = this.tools;
        return tools2.concatByteArrays(tools2.byteToBytes((byte) -114), concatByteArrays);
    }
}
