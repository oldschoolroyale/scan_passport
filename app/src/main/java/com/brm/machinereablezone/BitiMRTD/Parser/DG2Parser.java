package com.brm.machinereablezone.BitiMRTD.Parser;

import android.graphics.Bitmap;

import com.brm.machinereablezone.BitiMRTD.Tools.C0464Tools;

public class DG2Parser {
    private Iso19794Parser iso19794;
    private TagParser tagParser;
    private C0464Tools tools = new C0464Tools();

    public DG2Parser(byte[] bArr) {
        System.out.println("Length of DG2 : ");
        System.out.println(String.valueOf(bArr.length));
        TagParser geTag = new TagParser(bArr).geTag("75").geTag("7F61").geTag("7F60");
        this.iso19794 = new Iso19794Parser(geTag.geTag("7F2E").getBytes() != null ? geTag.geTag("7F2E").getBytes() : geTag.geTag("5F2E").getBytes() != null ? geTag.geTag("5F2E").getBytes() : null);
    }

    public Bitmap getBitmap() {
        return this.iso19794.getBitmap();
    }
}
