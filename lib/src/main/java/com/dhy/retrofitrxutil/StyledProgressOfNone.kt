package com.dhy.retrofitrxutil

class StyledProgressOfNone : StyledProgress {
    override fun showProgress() {}
    override fun dismissProgress() {}

    companion object {
        @JvmStatic
        val instance: StyledProgress = StyledProgressOfNone()
    }
}