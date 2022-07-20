package com.brm.machinereablezone.BitiMRTD.Reader;

import android.util.Log;

import com.brm.machinereablezone.BitiMRTD.NFC.Apdu;
import com.brm.machinereablezone.BitiMRTD.NFC.Doc9303Apdu;
import com.brm.machinereablezone.BitiMRTD.Tools.C0464Tools;
import com.brm.machinereablezone.BitiMRTD.Tools.Crypto;
import com.brm.machinereablezone.utils.TagProvider;
import java.lang.ref.WeakReference;
import java.util.Arrays;

public abstract class AbstractReader {
    protected Apdu apdu = new Apdu();
    protected BacInfo bacInfo = null;
    protected Crypto crypto = new Crypto();
    protected Doc9303Apdu doc9303Apdu;
    protected int maxBlockSize = 215;
    protected byte mutualAuthLe = 40;
    protected WeakReference<Object> progressListener;
    protected int readingAttempsFails = 0;
    protected byte[] selectBacChallenge = {0, -124, 0, 0, 8};
    protected byte[] sequenceCounter = null;
    protected byte[] sessionEncKey = null;
    protected byte[] sessionMacKey = null;
    protected C0464Tools tools = new C0464Tools();

    public abstract byte[] calculateEncryptionKey(byte[] bArr);

    public abstract byte[] calculateMac(byte[] bArr, byte[] bArr2);

    public abstract byte[] calculateMac(byte[] bArr, byte[] bArr2, boolean z);

    public abstract byte[] calculateSequenceCounter(byte[] bArr);

    public abstract byte[] decrypt(byte[] bArr, byte[] bArr2);

    public abstract byte[] encrypt(byte[] bArr, byte[] bArr2);

    public abstract byte[] padData(byte[] bArr);

    public void incrementSequenceCounter() {
        this.sequenceCounter = this.tools.incrementBytesArray(this.sequenceCounter);
    }

    public AbstractReader() {
        Doc9303Apdu doc9303Apdu2 = new Doc9303Apdu();
        this.doc9303Apdu = doc9303Apdu2;
        try {
            System.out.println("Select AID : ".concat(this.tools.bytesToString(TagProvider.transceive(doc9303Apdu2.getAID()))));
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public void setProgressListener(WeakReference<Object> weakReference) {
        this.progressListener = weakReference;
    }

    public void setMaxBlockSize(int i) {
        this.maxBlockSize = i;
    }

    public void setApduWithLe(boolean z) {
        this.apdu.setApduWithLe(z);
    }

    public void setMutualAuthLe(byte b) {
        this.mutualAuthLe = b;
    }

    public byte[] getBacChallenge() {
        try {
            byte[] transceive = TagProvider.transceive(this.selectBacChallenge);
            if (transceive != null) {
                if (transceive.length >= 10) {
                    if (!(transceive[8] == -112 && transceive[9] == 0)) {
                        System.out.println("Expected SWR 1-2 to be 0x9000");
                    }
                    System.out.println("Got challenge : ".concat(this.tools.bytesToString(transceive)));
                    return Arrays.copyOfRange(transceive, 0, 8);
                }
            }
            System.out.println("Expected at least 10 bytes response, got : ".concat(this.tools.bytesToString(transceive)));
            return null;
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }

    public boolean initSession() {
        return initSession(20000);
    }

    public boolean initSession(int i) {
        if (this.bacInfo == null) {
            System.out.println("bac info needs to be set");
            return false;
        }
        byte[] bacChallenge = getBacChallenge();
        byte[] generateRandomBytes = this.crypto.generateRandomBytes(8);
        byte[] generateRandomBytes2 = this.crypto.generateRandomBytes(16);
        byte[] concatByteArrays = this.tools.concatByteArrays(this.tools.concatByteArrays(generateRandomBytes, bacChallenge), generateRandomBytes2);
        System.out.println("S : ".concat(this.tools.bytesToString(concatByteArrays)));
        byte[] calculateSeed = this.crypto.calculateSeed(this.bacInfo.getPassportNbr(), this.bacInfo.getDateOfBirth(), this.bacInfo.getDateOfExpiry());
        byte[] calculateEncryptionKey = calculateEncryptionKey(calculateSeed);
        byte[] calculateMacKey = this.crypto.calculateMacKey(calculateSeed);
        byte[] encrypt = encrypt(calculateEncryptionKey, concatByteArrays);
        System.out.println("eifd : ".concat(this.tools.bytesToString(encrypt)));
        byte[] buildApduCommand = this.apdu.buildApduCommand((byte) 0, (byte) -126, (byte) 0, (byte) 0, this.tools.concatByteArrays(encrypt, calculateMac(calculateMacKey, encrypt)), this.mutualAuthLe);
        long currentTimeMillis = System.currentTimeMillis();
        int timeout = TagProvider.getTimeout();
        TagProvider.setTimeout(i + timeout);
        try {
            System.out.println("Max tranceivalable length: ".concat(String.valueOf(TagProvider.getMaxTransceiveLength())));
            byte[] transceive = TagProvider.transceive(buildApduCommand);
            System.out.println("Succeeded in: ".concat(String.valueOf(System.currentTimeMillis() - currentTimeMillis)));
            System.out.println("SESSION RESULT: ".concat(this.tools.bytesToString(transceive)));
            TagProvider.setTimeout(timeout);
            if (transceive != null) {
                System.out.println("transcieve is not null");
                if (transceive.length >= 42) {
                    byte[] decrypt = decrypt(calculateEncryptionKey, Arrays.copyOfRange(transceive, 0, 32));
                    System.out.println("Decrypted payload: ".concat(this.tools.bytesToString(decrypt)));
                    byte[] doXor = this.tools.doXor(generateRandomBytes2, Arrays.copyOfRange(decrypt, 16, 32));
                    System.out.println("Session seed: ".concat(this.tools.bytesToString(doXor)));
                    this.sessionEncKey = calculateEncryptionKey(doXor);
                    this.sessionMacKey = this.crypto.calculateMacKey(doXor);
                    this.sequenceCounter = calculateSequenceCounter(decrypt);
                    return true;
                }
                System.out.println("transcieve is less then 42, length" + transceive.length);
            }
            System.out.println("BAC FAILED!");
            return false;
        } catch (Exception e) {
            System.out.println("Failed in: ".concat(String.valueOf(System.currentTimeMillis() - currentTimeMillis)));
            if (e.getMessage() != null) {
                System.out.println(e.getMessage());
            }
            System.out.println(Log.getStackTraceString(e));
            return false;
        }
    }

    public void setBacInfo(BacInfo bacInfo2) {
        this.bacInfo = bacInfo2;
    }

    private boolean selectFile(byte[] bArr) {
        if (this.sessionEncKey == null || this.sessionMacKey == null) {
            System.out.println("Session key is empty");
            return false;
        }
        System.out.println("Step 1, select FILE");
        byte[] padData = padData(new byte[]{12, -92, 2, 12});
        byte[] padData2 = padData(bArr);
        System.out.println("paddedQuery : ".concat(this.tools.bytesToString(padData2)));
        byte[] buildDO87 = this.doc9303Apdu.buildDO87(encrypt(this.sessionEncKey, padData2));
        System.out.println("do87 : ".concat(this.tools.bytesToString(buildDO87)));
        byte[] concatByteArrays = this.tools.concatByteArrays(padData, buildDO87);
        incrementSequenceCounter();
        try {
            System.out.println("rapdu : ".concat(this.tools.bytesToString(TagProvider.transceive(this.apdu.buildApduCommand((byte) 12, (byte) -92, (byte) 2, (byte) 12, this.tools.concatByteArrays(buildDO87, this.doc9303Apdu.buildDO8E(calculateMac(this.sessionMacKey, padData(this.tools.concatByteArrays(this.sequenceCounter, concatByteArrays)), false))), (byte) 0)))));
            incrementSequenceCounter();
            return true;
        } catch (Exception e) {
            if (e.getMessage() != null) {
                System.out.println(e.getMessage());
            }
            System.out.println(Log.getStackTraceString(e));
            return false;
        }
    }

    public byte[] readFile(byte[] bArr) {
        WeakReference<Object> weakReference = this.progressListener;
        if (weakReference == null || !(weakReference.get() instanceof ProgressListenerInterface) || !((ProgressListenerInterface) this.progressListener.get()).isCanceled()) {
            return readFileStep1(bArr);
        }
        return null;
    }

    private byte[] readFileStep1(byte[] bArr) {
        if (this.sessionEncKey == null || this.sessionMacKey == null) {
            System.out.println("Session key is empty");
            return null;
        } else if (selectFile(bArr)) {
            return readFileStep2(bArr);
        } else {
            return null;
        }
    }

    private byte[] readFileStep2(byte[] bArr) {
        System.out.println("Step 2, read first 4 bytes of FILE");
        byte[] padData = padData(new byte[]{12, -80, 0, 0});
        byte[] buildDO97 = this.doc9303Apdu.buildDO97(4);
        System.out.println("do97 : ".concat(this.tools.bytesToString(buildDO97)));
        byte[] concatByteArrays = this.tools.concatByteArrays(padData, buildDO97);
        incrementSequenceCounter();
        try {
            byte[] transceive = TagProvider.transceive(this.apdu.buildApduCommand((byte) 12, (byte) -80, (byte) 0, (byte) 0, this.tools.concatByteArrays(buildDO97, this.doc9303Apdu.buildDO8E(calculateMac(this.sessionMacKey, padData(this.tools.concatByteArrays(this.sequenceCounter, concatByteArrays)), false))), (byte) 0));
            incrementSequenceCounter();
            if (transceive == null || transceive.length < 5) {
                System.out.println("Response size too small");
                return null;
            }
            if (transceive[0] == -121) {
                if (transceive[2] == 1) {
                    byte b = transceive[1];
                    System.out.println("Response length : ".concat(String.valueOf(b)));
                    byte[] unpadData = this.tools.unpadData(decrypt(this.sessionEncKey, Arrays.copyOfRange(transceive, 3, b + 2)));
                    byte[] readFileStep3 = readFileStep3(bArr, unpadData);
                    if (readFileStep3 == null) {
                        return null;
                    }
                    return this.tools.concatByteArrays(unpadData, readFileStep3);
                }
            }
            System.out.println("Expected DO87 response");
            return null;
        } catch (Exception e) {
            if (e.getMessage() != null) {
                System.out.println(e.getMessage());
            }
            System.out.println(Log.getStackTraceString(e));
            return null;
        }
    }

    private byte[] readFileStep3(byte[] bArr, byte[] bArr2) {
        System.out.println("Step 3, read entire FILE");
        int lengthFromFileHeader = this.tools.getLengthFromFileHeader(bArr2);
        System.out.println("File length : ".concat(String.valueOf(lengthFromFileHeader)));
        byte[] bArr3 = new byte[0];
        int i = 4;
        if (lengthFromFileHeader < 128) {
            lengthFromFileHeader += 2;
        } else if (lengthFromFileHeader < 256) {
            lengthFromFileHeader += 3;
        } else if (lengthFromFileHeader < 65536) {
            lengthFromFileHeader += 4;
        }
        while (i < lengthFromFileHeader) {
            int i2 = this.maxBlockSize;
            if (i + i2 > lengthFromFileHeader) {
                i2 = lengthFromFileHeader - i;
            }
            byte[] readFileStep3 = readFileStep3(bArr, i, i2);
            if (readFileStep3 == null) {
                WeakReference<Object> weakReference = this.progressListener;
                if ((weakReference != null && (weakReference.get() instanceof ProgressListenerInterface) && ((ProgressListenerInterface) this.progressListener.get()).isCanceled()) || this.readingAttempsFails >= 1) {
                    return null;
                }
                System.out.println("Seems that we lost the connection, trying to reinitialize the session");
                initSession(TagProvider.getTimeout());
                selectFile(bArr);
                this.readingAttempsFails++;
            } else {
                i += i2;
                bArr3 = this.tools.concatByteArrays(bArr3, readFileStep3);
                this.readingAttempsFails = 0;
            }
            WeakReference<Object> weakReference2 = this.progressListener;
            if (weakReference2 != null && (weakReference2.get() instanceof ProgressListenerInterface)) {
                if (((ProgressListenerInterface) this.progressListener.get()).isCanceled()) {
                    return null;
                }
                ((ProgressListenerInterface) this.progressListener.get()).updateProgress(Math.round((float) ((i * 100) / lengthFromFileHeader)));
            }
        }
        return bArr3;
    }

    private byte[] readFileStep3(byte[] bArr, int i, int i2) {
        WeakReference<Object> weakReference;
        System.out.println("Step 3, read entire FILE, cursor at : ".concat(String.valueOf(i)));
        System.out.println("le : ".concat(String.valueOf(i2)));
        byte[] calculate2bytesInt = this.tools.calculate2bytesInt(i);
        int i3 = 4;
        byte[] padData = padData(new byte[]{12, -80, calculate2bytesInt[0], calculate2bytesInt[1]});
        byte[] buildDO97 = this.doc9303Apdu.buildDO97(i2);
        byte[] concatByteArrays = this.tools.concatByteArrays(padData, buildDO97);
        incrementSequenceCounter();
        try {
            byte[] transceive = TagProvider.transceive(this.apdu.buildApduCommand((byte) 12, (byte) -80, calculate2bytesInt[0], calculate2bytesInt[1], this.tools.concatByteArrays(buildDO97, this.doc9303Apdu.buildDO8E(calculateMac(this.sessionMacKey, padData(this.tools.concatByteArrays(this.sequenceCounter, concatByteArrays)), false))), (byte) 0));
            incrementSequenceCounter();
            if (transceive != null) {
                if (transceive.length >= 4) {
                    int lengthFromFileHeader = this.tools.getLengthFromFileHeader(Arrays.copyOfRange(transceive, 0, 4));
                    System.out.println("Response length : ".concat(String.valueOf(lengthFromFileHeader)));
                    if (lengthFromFileHeader <= 127) {
                        i3 = 3;
                    } else if (lengthFromFileHeader > 255) {
                        if (lengthFromFileHeader <= 65535) {
                            i3 = 5;
                        } else {
                            System.out.println("Too big");
                            return null;
                        }
                    }
                    return this.tools.unpadData(decrypt(this.sessionEncKey, Arrays.copyOfRange(transceive, i3, (i3 - 1) + lengthFromFileHeader)));
                }
            }
            System.out.println("Error, expected at least 4 bytes, got : ".concat(this.tools.bytesToString(transceive)));
            return null;
        } catch (Exception e) {
            if (e.getMessage() != null) {
                System.out.println(e.getMessage());
            }
            System.out.println(Log.getStackTraceString(e));
            if (TagProvider.getTagIsLost() && (weakReference = this.progressListener) != null && (weakReference.get() instanceof ProgressListenerInterface)) {
                ((ProgressListenerInterface) this.progressListener.get()).cancel();
            }
            return null;
        }
    }
}
