package com.brm.machinereablezone.BitiMRTD.Tools;


public class AESCMAC {
    private static final byte Rb128 = -121;
    private static final byte Rb64 = 27;

    public static byte[] get(byte[] bArr, byte[] bArr2) {
        return get(bArr, bArr2, new byte[16]);
    }

    public static byte[] get(byte[] bArr, byte[] bArr2, byte[] bArr3) {
        byte[] bArr4 = new byte[16];
        byte[] subK1 = getSubK1(AES.encrypt(bArr4, bArr, bArr4), 16, Rb128);
        return getCMAC(bArr, subK1, getSubK2(subK1, 16, Rb128), bArr2, bArr3, 16);
    }

    private static byte[] getCMAC(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5, int i) {
        byte[] bArr6;
        if (bArr4.length == 0) {
            bArr6 = new byte[i];
            bArr6[0] = Byte.MIN_VALUE;
        } else {
            bArr6 = bArr4;
        }
        if (bArr4.length % i != 0) {
            int length = bArr4.length;
            byte[] bArr7 = new byte[((bArr4.length - (bArr4.length % i)) + i)];
            System.arraycopy(bArr4, 0, bArr7, 0, bArr4.length);
            bArr7[length] = Byte.MIN_VALUE;
            bArr6 = bArr7;
        }
        if (bArr4.length == 0 || bArr4.length % i != 0) {
            for (int length2 = bArr6.length - i; length2 < bArr6.length; length2++) {
                bArr6[length2] = (byte) (bArr6[length2] ^ bArr3[(length2 - bArr6.length) + i]);
            }
        } else {
            for (int length3 = bArr6.length - i; length3 < bArr6.length; length3++) {
                bArr6[length3] = (byte) (bArr6[length3] ^ bArr2[(length3 - bArr6.length) + i]);
            }
        }
        byte[] encrypt = AES.encrypt(bArr5, bArr, bArr6);
        byte[] bArr8 = new byte[i];
        System.arraycopy(encrypt, encrypt.length - i, bArr8, 0, i);
        return bArr8;
    }

    private static byte[] getSubK2(byte[] bArr, int i, byte b) {
        byte[] bArr2 = new byte[i];
        bArr2[i - 1] = b;
        byte[] shiftLeft = shiftLeft(bArr);
        if ((bArr[0] & 128) != 0) {
            for (int i2 = 0; i2 < i; i2++) {
                shiftLeft[i2] = (byte) (shiftLeft[i2] ^ bArr2[i2]);
            }
        }
        return shiftLeft;
    }

    private static byte[] getSubK1(byte[] bArr, int i, byte b) {
        byte[] bArr2 = new byte[i];
        bArr2[i - 1] = b;
        byte[] shiftLeft = shiftLeft(bArr);
        if ((bArr[0] & 128) != 0) {
            for (int i2 = 0; i2 < i; i2++) {
                shiftLeft[i2] = (byte) (shiftLeft[i2] ^ bArr2[i2]);
            }
        }
        return shiftLeft;
    }

    private static byte[] shiftLeft(byte[] bArr) {
        return toByte(shiftLeft(toBit(bArr)));
    }

    private static byte[] toByte(String str) {
        byte[] bArr = new byte[(str.length() / 8)];
        int i = 0;
        int i2 = 0;
        while (i < str.length()) {
            int i3 = i + 8;
            bArr[i2] = (byte) Integer.parseInt(str.substring(i, i3), 2);
            i2++;
            i = i3;
        }
        return bArr;
    }

    private static String shiftLeft(String str) {
        return str.substring(1) + "0";
    }

    private static String toBit(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bArr) {
            String binaryString = Integer.toBinaryString(b + 256);
            sb.append(binaryString.subSequence(binaryString.length() - 8, binaryString.length()));
        }
        return sb.toString();
    }
}
