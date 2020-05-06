package com.dhy.retrofitrxutil

class ThrowableBZ(val status: IResponseStatus) : Throwable(status.message)