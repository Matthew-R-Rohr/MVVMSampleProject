package com.fct.mvvm.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.text.SimpleDateFormat
import java.util.*

// For larger project we would want to split these extensions into individual extension relevant files

/**
 * Show a short toast message
 */
@SuppressLint("ShowToast")
fun Context?.toastShort(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

/**
 * Show a long toast message
 */
@SuppressLint("ShowToast")
fun Context?.toastLong(text: String) = Toast.makeText(this, text, Toast.LENGTH_LONG).show()

/**
 * RxJava: Add disposable to this composite
 */
fun Disposable.addToComposite(disposable: CompositeDisposable) = disposable.add(this)

/**
 * Converts [Long] UNIX to [Date]
 */
fun Long.toDateFromUnix(): Date = Date(this * 1000)

/**
 * Converts [Date] into a string formatted MM/dd/yyyy
 */
fun Date.toMonthDayYearString(): String =
    SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(this)

/**
 * Return an empty string if original string is empty
 */
fun String?.returnEmptyStringIfNull() = if (this.isNullOrBlank()) "" else this