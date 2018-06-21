package com.dhy.retrofitrxutil;

public class StyledProgressOfNone implements StyledProgress {
    private static StyledProgress instance;

    public static StyledProgress getInstance() {
        if (instance == null) instance = new StyledProgressOfNone();
        return instance;
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void dismissProgress() {

    }
}
