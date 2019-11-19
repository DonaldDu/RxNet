package com.dhy.retrofitrxutil

interface StyledProgressGenerator {
    fun generate(observer: IObserverX): StyledProgress?
}