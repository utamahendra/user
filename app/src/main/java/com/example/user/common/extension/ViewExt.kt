package com.example.user.common.extension

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.Toolbar


fun View.visible() {
    if (visibility != View.VISIBLE) visibility = View.VISIBLE
}

fun View.invisible() {
    if (visibility != View.INVISIBLE) visibility = View.INVISIBLE
}

fun View.gone() {
    if (visibility != View.GONE) visibility = View.GONE
}

fun ViewGroup?.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(this?.context).inflate(layoutRes, this, attachToRoot)
}

fun View.setSingleClickListener(onSafeClick: (View) -> Unit) {
    val defaultInterval = 2000
    var lastTimeClicked: Long = 0
    val safeClickListener = object : View.OnClickListener {
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
                return
            }
            lastTimeClicked = SystemClock.elapsedRealtime()
            onSafeClick(v)
        }
    }
    setOnClickListener(safeClickListener)
}

fun View.asToolbarOrNull(): Toolbar? {
    return try {
        (this as Toolbar)
    } catch (e: Exception) {
        null
    }
}