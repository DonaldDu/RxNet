package com.dhy.retrofitrxutil

interface StyledProgressGenerator {
    /**
     * @return null for no Progress
     * */
    fun generate(observer: IObserverX): StyledProgress?
}