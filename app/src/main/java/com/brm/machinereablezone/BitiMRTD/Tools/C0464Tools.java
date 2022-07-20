package com.brm.machinereablezone.BitiMRTD.Tools;

import android.util.Log;

import com.brm.machinereablezone.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;

/* renamed from: bondi.nfcPassportReader.jan.mrtd2.BitiMRTD.Tools.Tools */
public class C0464Tools {
    public byte[] byteToBytes(byte b) {
        return new byte[]{b};
    }

    public byte[] unpadData(byte[] bArr) {
        int i = 1;
        while (bArr[bArr.length - i] != Byte.MIN_VALUE) {
            if (bArr.length <= i) {
                return null;
            }
            i++;
        }
        return Arrays.copyOfRange(bArr, 0, bArr.length - i);
    }

    public byte[] doXor(byte[] bArr, byte[] bArr2) {
        if (bArr.length != bArr2.length) {
            System.out.println("Can not do XOR: input1 and input2 length mismatch");
            System.out.println("Input1 length : ".concat(String.valueOf(bArr.length)));
            System.out.println("Input2 length : ".concat(String.valueOf(bArr2.length)));
            return null;
        }
        byte[] bArr3 = new byte[bArr.length];
        for (int i = 0; i < bArr.length; i++) {
            bArr3[i] = (byte) (bArr[i] ^ bArr2[i]);
        }
        return bArr3;
    }

    public byte[] adjustParityBits(byte[] bArr) {
        for (int i = 0; i < bArr.length; i++) {
            byte b = bArr[i];
            bArr[i] = (byte) (((((b >> 7) ^ ((((((b >> 1) ^ (b >> 2)) ^ (b >> 3)) ^ (b >> 4)) ^ (b >> 5)) ^ (b >> 6))) ^ 1) & 1) | (b & 254));
        }
        return bArr;
    }

    public String bytesToString(byte[] bArr) {
        String str = BuildConfig.FLAVOR;
        if (bArr == null) {
            System.out.println("bArr is null");
            return str;
        }
        int length = bArr.length;
        System.out.println("bArr length " + length);
        for (int i = 0; i < length; i++) {
            str = str.concat(String.format("%02x", new Object[]{Byte.valueOf(bArr[i])}));
        }
        return str;
    }

    public byte[] concatByteArrays(byte[] bArr, byte[] bArr2) {
        if (bArr == null) {
            return bArr2;
        }
        if (bArr2 == null) {
            return bArr;
        }
        byte[] bArr3 = new byte[(bArr.length + bArr2.length)];
        for (int i = 0; i < bArr.length + bArr2.length; i++) {
            if (i < bArr.length) {
                bArr3[i] = bArr[i];
            } else {
                bArr3[i] = bArr2[i - bArr.length];
            }
        }
        return bArr3;
    }

    public byte[] incrementBytesArray(byte[] bArr, int i) {
        if (i >= bArr.length) {
            return concatByteArrays(byteToBytes((byte) 1), bArr);
        }
        bArr[i] = (byte) (bArr[i] + 1);
        return bArr[i] == 0 ? incrementBytesArray(bArr, i + 1) : bArr;
    }

    public byte[] incrementBytesArray(byte[] bArr) {
        return incrementBytesArray(bArr, bArr.length - 1);
    }

    public byte[] calculateAsn1Length(byte[] bArr) {
        if (bArr.length <= 127) {
            return byteToBytes((byte) bArr.length);
        }
        if (bArr.length >= 127 && bArr.length <= 255) {
            return concatByteArrays(byteToBytes((byte) -127), byteToBytes((byte) bArr.length));
        }
        if (bArr.length < 256 || bArr.length > 65535) {
            System.out.println("Error: length is too big");
            return null;
        }
        return concatByteArrays(byteToBytes((byte) -126), concatByteArrays(byteToBytes((byte) (bArr.length >> 8)), byteToBytes((byte) bArr.length)));
    }

    public int getAsn1HeaderLength(byte[] bArr) {
        if (bArr == null) {
            System.out.println("asn1 is null");
            return 0;
        } else if (bArr[0] <= Byte.MAX_VALUE && bArr[0] >= 0) {
            return 1;
        } else {
            if (bArr[0] == -127) {
                return 2;
            }
            if (bArr[0] == -126) {
                return 3;
            }
            return 0;
        }
    }

    public int getIntFrom16bits(byte[] bArr) {
        return (bArr[1] & 255) | ((bArr[0] << 8) & 65280);
    }

    public int getLengthFromAsn1(byte[] bArr) {
        if (bArr == null) {
            System.out.println("asn1 is null");
            return 0;
        } else if (bArr[0] <= Byte.MAX_VALUE && bArr[0] >= 0) {
            return bArr[0] & 255;
        } else {
            if (bArr[0] == -127) {
                return bArr[1] & 255;
            }
            if (bArr[0] != -126) {
                return 0;
            }
            return (bArr[2] & 255) | ((bArr[1] << 8) & 65280);
        }
    }

    public int getLengthFromFileHeader(byte[] bArr) {
        if (bArr.length == 4) {
            return getLengthFromAsn1(Arrays.copyOfRange(bArr, 1, 4));
        }
        System.out.println("Expected file header to be 4 bytes long");
        return 0;
    }

    public byte[] calculate2bytesInt(int i) {
        if (i <= 255) {
            return concatByteArrays(byteToBytes((byte) 0), byteToBytes((byte) i));
        }
        if (i <= 65535) {
            return concatByteArrays(byteToBytes((byte) (i >> 8)), byteToBytes((byte) i));
        }
        System.out.println("Error: value is too big");
        return null;
    }

    public int calculateMrzCheckDigit(String str) {
        String upperCase = str.toUpperCase();
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < upperCase.length(); i3++) {
            char charAt = upperCase.charAt(i3);
            int i4 = (charAt <= '@' || charAt >= '[') ? 0 : (charAt - 'A') + 10;
            if (charAt > '/' && charAt < ':') {
                i4 = charAt - '0';
            }
            int i5 = i3 % 3;
            if (i5 == 0) {
                i2 = 7;
            } else if (i5 == 1) {
                i2 = 3;
            } else if (i5 == 2) {
                i2 = 1;
            }
            i += i4 * i2;
        }
        return i % 10;
    }

    public byte[] inputStreamToByteArray(InputStream inputStream) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bArr = new byte[16384];
            while (true) {
                int read = inputStream.read(bArr, 0, 16384);
                if (read == -1) {
                    return byteArrayOutputStream.toByteArray();
                }
                byteArrayOutputStream.write(bArr, 0, read);
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                System.out.println(e.getMessage());
            }
            System.out.println(Log.getStackTraceString(e));
            return null;
        }
    }

    public byte[] invertBytes(byte[] bArr) {
        byte[] bArr2 = new byte[bArr.length];
        for (int i = 0; i < bArr.length; i++) {
            bArr2[i] = bArr[(bArr.length - 1) - i];
        }
        return bArr2;
    }
}
