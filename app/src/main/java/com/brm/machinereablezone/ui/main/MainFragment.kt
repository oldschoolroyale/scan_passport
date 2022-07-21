package com.brm.machinereablezone.ui.main

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.brm.machinereablezone.MainActivity
import com.brm.machinereablezone.R
import com.brm.machinereablezone.databinding.FragmentMainBinding
import com.brm.machinereablezone.model.DocType
import com.brm.machinereablezone.model.EntryData
import com.brm.machinereablezone.ui.nfc.ReadingPassportActivity
import com.brm.machinereablezone.utils.ViewAnimation.collapse
import com.brm.machinereablezone.utils.ViewAnimation.expand


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val view_list: ArrayList<View> = ArrayList()
    private val step_view_list: ArrayList<RelativeLayout> = ArrayList()

    private var successStep = 0
    private var currentStep = 0

    private val entryData = EntryData()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater)
        view_list.add(binding.lytTitle)
        view_list.add(binding.lytDescription)
        view_list.add(binding.lytTime)
        view_list.add(binding.lytDate)

        // populate view step (circle in left)
        step_view_list.add(binding.stepTitle)
        step_view_list.add(binding.stepDescription)
        step_view_list.add(binding.stepTime)
        step_view_list.add(binding.stepDate)

        for (v in view_list) {
            v.visibility = View.GONE
        }
        view_list[0].visibility = View.VISIBLE

        binding.btContinueTitle.setOnClickListener {
            entryData.passportNumber = binding.etTitle.text.toString()
            collapseAndContinue(0)
        }
        binding.btContinueDescription.setOnClickListener {
            entryData.birthDate = binding.etDescription.text.toString()
            collapseAndContinue(1)
        }
        binding.btContinueTime.setOnClickListener {
            entryData.issueDate = binding.etLogin.text.toString()
            collapseAndContinue(2)
        }
        binding.btContinueDate.setOnClickListener {
            val intent = Intent(context, ReadingPassportActivity::class.java)
            intent.putExtra("passportNumber", entryData.passportNumber)
            intent.putExtra("dateOfBirth", entryData.birthDate)
            intent.putExtra("dateOfExpiration", entryData.issueDate)
            startActivity(intent)
        }

        //Label
        binding.tvLabelTitle.setOnClickListener {
            if (successStep >= 0 && currentStep !== 0) {
                currentStep = 0
                collapseAll()
                expand(view_list[0])
            }
        }

        binding.tvLabelDescription.setOnClickListener {
            if (successStep >= 1 && currentStep !== 1) {
                currentStep = 1
                collapseAll()
                expand(view_list[1])
            }
        }

        binding.tvLabelTime.setOnClickListener {
            if (successStep >= 2 && currentStep !== 2) {
                currentStep = 2
                collapseAll()
                expand(view_list[2])
            }
        }

        binding.scanPassBtn.setOnClickListener {
            (activity as MainActivity).startCamera(DocType.PASSPORT)
        }

        binding.scanIdBtn.setOnClickListener {
            (activity as MainActivity).startCamera(DocType.ID_CARD)
        }

        return binding.root
    }


    private fun collapseAll() {
        for (v in view_list) {
            collapse(v)
        }
    }

    private fun collapseAndContinue(index: Int) {
        var index = index
        collapse(view_list.get(index))
        setCheckedStep(index)
        index++
        currentStep = index
        successStep = if (index > successStep) index else successStep
        expand(view_list.get(index))
    }

    private fun setCheckedStep(index: Int) {
        val relative: RelativeLayout = step_view_list.get(index)
        relative.removeAllViews()
        val img = ImageButton(context)
        img.setImageResource(R.drawable.ic_done)
        img.setBackgroundColor(Color.TRANSPARENT)
        img.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        relative.addView(img)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}