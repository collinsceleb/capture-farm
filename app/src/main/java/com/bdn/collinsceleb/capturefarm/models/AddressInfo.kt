package com.bdn.collinsceleb.capturefarm.models

class AddressInfo {
    var addressLine: String? = null
    var latitude = 0.0
    var longitude = 0.0

    override fun toString(): String {
        return "AddressInfo{" +
                "addressLine='" + addressLine + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}'
    }
}