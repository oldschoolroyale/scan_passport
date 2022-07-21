package com.brm.machinereablezone.ui.result

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.brm.machinereablezone.BitiMRTD.Parser.DG1Parser
import com.brm.machinereablezone.BitiMRTD.Parser.DG2Parser
import com.brm.machinereablezone.R
import com.brm.machinereablezone.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var dg1: ByteArray
    private lateinit var dg2: ByteArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        dg1 = intent.extras!!.getByteArray("dg1")!!
        dg2 = intent.extras!!.getByteArray("dg2")!!

        val bitmap = DG2Parser(dg2).bitmap
        findViewById<ImageView>(R.id.img_avatar).setImageBitmap(bitmap)

        val dg1Parser = DG1Parser(dg1)
        binding.nameTv.text = "${dg1Parser.givenNames} ${dg1Parser.surname}"
        binding.aboutTv.text = "Birthdate: ${dg1Parser.dateOfBirth}\nGender: ${dg1Parser.gender}"
        binding.countryTv.text = "Nationality: ${dg1Parser.nationalityCode}"


    }
}