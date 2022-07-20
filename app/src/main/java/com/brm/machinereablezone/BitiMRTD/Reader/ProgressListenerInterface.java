package com.brm.machinereablezone.BitiMRTD.Reader;

public interface ProgressListenerInterface {
    void cancel();

    boolean isCanceled();

    void updateProgress(int i);
}
