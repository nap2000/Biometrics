package uk.ac.lshtm.keppel.android.core

fun ByteArray.toHexString() = asUByteArray().joinToString("") { it.toString(16).padStart(2, '0') }