package com.brm.machinereablezone.ui.nfc

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brm.machinereablezone.BitiMRTD.Constants.MrtdConstants
import com.brm.machinereablezone.BitiMRTD.Parser.DG1Parser
import com.brm.machinereablezone.BitiMRTD.Reader.BacInfo
import com.brm.machinereablezone.BitiMRTD.Reader.DESedeReader
import com.brm.machinereablezone.BitiMRTD.Tools.C0464Tools
import com.brm.machinereablezone.utils.TagProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

/**
 * Created by Rakhimjonov Shokhsulton on 19,июль,2022
 * at Mayasoft LLC,
 * Tashkent, UZB.
 */
class NfcViewModel : ViewModel() {
    
    companion object{
        private val TAG = this.javaClass.name
    }

    private val _work_status = MutableLiveData<ByteArray>()
    val work_status : LiveData<ByteArray> get() = _work_status
    private var currentStep = 0
    private var dg1: ByteArray? = null
    private var dg2: ByteArray? = null
    private var sod: ByteArray? = null

    fun doWork(bacInfo: BacInfo) = viewModelScope.launch {
        val dESedeReader = DESedeReader()
        dESedeReader.setBacInfo(bacInfo)
        val result =
            withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                run(dESedeReader)
            }
        if (!result) {
            TagProvider.closeTag()
            Log.d(TAG, "doWork: work failed")
        }
        else
            _work_status.value = dg1

    }

    fun run(dESedeReader: DESedeReader): Boolean {
        if (dESedeReader.initSession()) {
            dESedeReader.setProgressListener(WeakReference(this))
            this.currentStep = 1
            Log.d(TAG, "run: step 1 passed")
            val readFile: ByteArray = dESedeReader.readFile(MrtdConstants.FID_DG1) ?: return false
            dg1 = readFile
            DG1Parser(readFile)
            C0464Tools()
            this.currentStep = 2
            Log.d(TAG, "run: step 2 passed")
            val readFile2: ByteArray = dESedeReader.readFile(MrtdConstants.FID_DG2) ?: return false
            dg2 = readFile2
            this.currentStep = 3
            Log.d(TAG, "run: step 3 passed")
            sod = dESedeReader.readFile(MrtdConstants.FID_EF_SOD)
            Log.d(TAG, "run: readfile $readFile && reafile2 $readFile2")
            return (!(readFile == null || readFile2 == null))
        }
        return false
    }

}