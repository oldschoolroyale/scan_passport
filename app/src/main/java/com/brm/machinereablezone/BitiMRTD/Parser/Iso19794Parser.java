package com.brm.machinereablezone.BitiMRTD.Parser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.brm.machinereablezone.BitiMRTD.Tools.C0464Tools;


import java.io.ByteArrayInputStream;
import java.util.Arrays;

public class Iso19794Parser {
    private Bitmap bitmap = null;
    private byte[] rawData;
    private C0464Tools tools;

    public Iso19794Parser(byte[] bArr) {
        this.rawData = bArr;
        this.tools = new C0464Tools();
        parse();
    }

    private void parse() {
        int intFrom16bits = this.tools.getIntFrom16bits(Arrays.copyOfRange(this.rawData, 18, 20));
        System.out.println("Number of feature points: ".concat(String.valueOf(intFrom16bits)));
        int i = 34;
        for (int i2 = 0; i2 < intFrom16bits; i2++) {
            i += 8;
        }
        byte[] bArr = this.rawData;
        byte[] copyOfRange = Arrays.copyOfRange(bArr, i + 1 + 1 + 2 + 2 + 1 + 1 + 2 + 2, bArr.length);
        try {
            this.bitmap = BitmapFactory.decodeByteArray(copyOfRange, 0, copyOfRange.length);
        } catch (Exception e) {
            if (e.getLocalizedMessage() != null) {
                System.out.println(e.getLocalizedMessage());
            }
            System.out.println(Log.getStackTraceString(e));
        }
        if (this.bitmap == null) {
            try {
//                org.jmrtd.jj2000.Bitmap decode = JJ2000Decoder.decode(new ByteArrayInputStream(copyOfRange));
//                this.bitmap = Bitmap.createBitmap(decode.getPixels(), 0, decode.getWidth(), decode.getWidth(), decode.getHeight(), Bitmap.Config.ARGB_8888);
            } catch (Exception e2) {
                if (e2.getLocalizedMessage() != null) {
                    System.out.println(e2.getLocalizedMessage());
                }
            }
        }
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }
}
