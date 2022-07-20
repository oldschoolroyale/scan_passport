package com.brm.machinereablezone.ui.nfc

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.brm.machinereablezone.BitiMRTD.Reader.BacInfo
import com.brm.machinereablezone.databinding.ActivityNfcBinding
import com.brm.machinereablezone.model.EntryData


class NfcActivity : AbstractNfcActivity() {

    private lateinit var binding: ActivityNfcBinding
    private val nfcViewModel by viewModels<NfcViewModel>()
    private var entryData: EntryData? = null

    private val workStatusObserver = Observer<ByteArray>{
        Toast.makeText(this, it.size.toString(), Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNfcBinding.inflate(layoutInflater)
        if (intent != null && intent.extras != null)
            entryData = intent.extras!!.getParcelable(ENTRY_DATA_KEY)
    }


//    override fun startProcess() {
//        val bacInfo = BacInfo()
//        bacInfo.dateOfBirth = entryData!!.birthDate
//        bacInfo.dateOfExpiry = entryData!!.issueDate
//        bacInfo.passportNbr = entryData!!.passportNumber
//        Log.d(TAG, "startProcess: $bacInfo")
//        nfcViewModel.work_status.observe(this, workStatusObserver)
//        nfcViewModel.doWork(bacInfo)
//    }

    companion object{
        const val ENTRY_DATA_KEY = "entry_data_key"
        private val TAG = this.javaClass.name
    }
}