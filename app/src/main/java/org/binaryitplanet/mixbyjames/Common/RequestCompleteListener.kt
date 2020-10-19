package org.binaryitplanet.mixbyjames.Common

interface RequestCompleteListener<T> {
    fun onRequestSuccess(data: T)
    fun onRequestFailed(message: String)
}