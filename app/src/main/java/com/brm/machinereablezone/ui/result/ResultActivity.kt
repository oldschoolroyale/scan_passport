package com.brm.machinereablezone.ui.result

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.brm.machinereablezone.BitiMRTD.Parser.DG2Parser
import com.brm.machinereablezone.R

class ResultActivity : AppCompatActivity() {
    private lateinit var dg1: ByteArray
    private lateinit var dg2: ByteArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        dg1 = intent.extras!!.getByteArray("dg1")!!
        dg2 = intent.extras!!.getByteArray("dg2")!!

        val bitmap = DG2Parser(dg2).bitmap
        findViewById<ImageView>(R.id.img_avatar).setImageBitmap(bitmap)
    }
}