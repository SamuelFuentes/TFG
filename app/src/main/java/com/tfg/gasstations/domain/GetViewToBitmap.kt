package com.tfg.gasstations.domain

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

class GetViewToBitmap {
    fun viewTextToBitmap(view: View) : Bitmap {
        view.measure(136, 136)
        val bitmap = Bitmap.createBitmap(136, 136, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0,0,view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }

    //Crear custom markers
    fun viewToBitmap(view: View) : Bitmap{
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0,0,view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }
}