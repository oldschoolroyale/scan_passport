package com.brm.machinereablezone.BitiMRTD.Parser;

import com.brm.machinereablezone.BitiMRTD.Tools.C0464Tools;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TagParser {
    private byte[] data;
    private Map<String, byte[]> tags = new HashMap();
    private C0464Tools tools;

    public TagParser(byte[] bArr) {
        if (bArr != null) {
            this.tools = new C0464Tools();
            this.data = bArr;
        }
    }

    public void parseElement(byte[] bArr) {
        int i = 0;
        while (i < bArr.length) {
            try {
                byte[] tagFromElement = getTagFromElement(Arrays.copyOfRange(bArr, i, i + 2));
                String lowerCase = this.tools.bytesToString(tagFromElement).toLowerCase();
                int length = i + tagFromElement.length;
                byte[] copyOfRange = Arrays.copyOfRange(bArr, length, bArr.length);
                int asn1HeaderLength = this.tools.getAsn1HeaderLength(copyOfRange);
                int lengthFromAsn1 = this.tools.getLengthFromAsn1(copyOfRange);
                System.out.println("Found tag : ".concat(lowerCase).concat(", length : ").concat(String.valueOf(lengthFromAsn1)));
                int i2 = length + asn1HeaderLength;
                int i3 = lengthFromAsn1 + i2;
                this.tags.put(lowerCase, Arrays.copyOfRange(bArr, i2, i3));
                i = i3;
            } catch (Exception e) {
                if (e.getMessage() != null) {
                    System.out.println(e.getMessage());
                    return;
                }
                return;
            }
        }
    }

    private byte[] getTagFromElement(byte[] bArr) {
        if (bArr[0] == Byte.MAX_VALUE || bArr[0] == 95) {
            return Arrays.copyOfRange(bArr, 0, 2);
        }
        return this.tools.byteToBytes(bArr[0]);
    }

    public TagParser geTag(String str) {
        parseElement(this.data);
        System.out.println("Getting tag : ".concat(str));
        return new TagParser(this.tags.get(str.toLowerCase()));
    }

    public byte[] getBytes() {
        return this.data;
    }
}
