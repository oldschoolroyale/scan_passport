//package com.brm.machinereablezone.ui
//
//import android.app.PendingIntent
//import android.content.Intent
//import android.content.IntentFilter
//import android.nfc.NfcAdapter
//import android.nfc.Tag
//import android.nfc.tech.IsoDep
//import android.os.Bundle
//import android.os.Parcelable
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import com.brm.machinereablezone.R
//import com.brm.machinereablezone.utils.TagProvider
//import org.bouncycastle.asn1.cmp.PKIFailureInfo
//
//
///**
// * Created by Rakhimjonov Shokhsulton on 19,июль,2022
// * at Mayasoft LLC,
// * Tashkent, UZB.
// */
//abstract class AbstractNfcActivity : AppCompatActivity() {
//    private var mNfcAdapter: NfcAdapter? = null
//    private var pendingIntent: PendingIntent? = null
//
//    public override fun onCreate(bundle: Bundle?) {
//        super.onCreate(bundle)
//        val defaultAdapter = NfcAdapter.getDefaultAdapter(this)
//        mNfcAdapter = defaultAdapter
//        if (defaultAdapter == null || !defaultAdapter.isEnabled) {
//
//            AlertDialog.Builder(this).setTitle(getString(R.string.error_nfc))
//                .setMessage(resources.getString(R.string.error_nfc_is_disabled)).setCancelable(true)
//                .setPositiveButton("enable"
//                ) { _, _ ->
//                    this@AbstractNfcActivity.startActivity(
//                        Intent("android.settings.NFC_SETTINGS")
//                    )
//                }.create().show()
//            return
//        }
//        pendingIntent = PendingIntent.getActivity(
//            this, 0, Intent(this, javaClass).addFlags(
//                PKIFailureInfo.duplicateCertReq
//            ), 0
//        )
//    }
//
//    public override fun onPause() {
//        super.onPause()
//        mNfcAdapter?.disableForegroundDispatch(this)
//    }
//
//    public override fun onResume() {
//        super.onResume()
//        try {
//            if (mNfcAdapter != null && pendingIntent != null) {
//                mNfcAdapter!!.enableForegroundDispatch(
//                    this,
//                    pendingIntent, null as Array<IntentFilter?>?, null as Array<Array<String?>?>?
//                )
//            }
//        } catch (unused: Exception) {
//            println("onResume error")
//        }
//    }
//
//    public override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//        TagProvider.setTag(IsoDep.get(intent.getParcelableExtra<Parcelable>("android.nfc.extra.TAG") as Tag?))
//        if (TagProvider.getTag() != null)
//            startProcess()
//    }
//
//    abstract fun startProcess()
//}