package com.tfg.gasstations.domain

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

class GetViewToBitmap {
    //Genera la imagen Bitmap a partir de los textView para utilizarla de icono para los markers
    fun viewTextToBitmap(view: View) : Bitmap {
        view.measure(136, 136)
        val bitmap = Bitmap.createBitmap(136, 136, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0,0,view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }
}