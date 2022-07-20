package com.brm.machinereablezone.utils;

import android.nfc.TagLostException;
import android.nfc.tech.IsoDep;
import android.util.Log;

public class TagProvider {
    private static IsoDep tag = null;
    private static boolean tagIsLost = false;
    private static int timeout = 1500;

    public static byte[] transceive(byte[] bArr) {
        if (getTag() != null) {
            try {
                System.out.println("try get tag");
                return getTag().transceive(bArr);
            } catch (Exception e) {
                System.out.println("catch getTag error: " + e.getMessage() + " class " + e.getClass().getName());
                if (e instanceof TagLostException) {
                    setTagIsLost();
                }
            }
        }
        System.out.println("getTag is null");
        return null;
    }

    public static IsoDep getTag() {
        if (tagIsLost) {
            return null;
        }
        connectTag();
        return tag;
    }

    public static void setTag(IsoDep isoDep) {
        tagIsLost = false;
        connectTag();
        if (isoDep != null) {
            try {
                isoDep.setTimeout(timeout);
            } catch (Exception unused) {
            }
        }
        tag = isoDep;
    }

    private static void connectTag() {
        IsoDep isoDep = tag;
        if (isoDep != null && !isoDep.isConnected()) {
            try {
                tag.connect();
            } catch (Exception e) {
                if (e.getMessage() != null) {
                    System.out.println(e.getMessage());
                }
                System.out.println(Log.getStackTraceString(e));
            }
        }
    }

    public static void closeTag() {
        IsoDep isoDep = tag;
        if (isoDep != null) {
            try {
                isoDep.close();
            } catch (Exception e) {
                if (e.getMessage() != null) {
                    System.out.println(e.getMessage());
                }
                System.out.println(Log.getStackTraceString(e));
            }
        }
    }

    public static boolean isTagReady() {
        if (tag == null) {
            return false;
        }
        connectTag();
        return tag.isConnected();
    }

    public static void setTimeout(int i) {
        timeout = i;
        if (getTag() != null) {
            getTag().setTimeout(i);
        }
    }

    public static int getTimeout() {
        if (getTag() != null) {
            return getTag().getTimeout();
        }
        return 0;
    }

    public static int getMaxTransceiveLength() {
        if (getTag() != null) {
            return getTag().getMaxTransceiveLength();
        }
        return 0;
    }

    public static void setTagIsLost() {
        tagIsLost = true;
    }

    public static boolean getTagIsLost() {
        return tagIsLost;
    }
}
