package com.example.myscanner2.Files

import android.os.Build

inline fun <T> sdk29andup(onSdk29:()->T):T?{
    return if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
        onSdk29()
    }else null
}