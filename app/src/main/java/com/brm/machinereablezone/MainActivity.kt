package com.brm.machinereablezone

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.brm.machinereablezone.model.DocType
import com.brm.machinereablezone.ui.ReadingPassportActivity
import com.brm.machinereablezone.ui.camera.CaptureActivity
import com.brm.machinereablezone.utils.AppUtil
import com.brm.machinereablezone.utils.PermissionUtil
import org.jmrtd.lds.icao.MRZInfo


class MainActivity : AppCompatActivity() {

    companion object{
        private const val APP_CAMERA_ACTIVITY_REQUEST_CODE = 150
        private const val DOC_TYPE = "DOC_TYPE"
    }

    private  var docType: DocType? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBarWithNavController(findNavController(R.id.mainNavHost))
    }


    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp() || findNavController(R.id.mainNavHost).navigateUp()
    }

    fun startCamera(docType: DocType){
        this.docType = docType
        requestPermissionForCamera()
    }

    private fun requestPermissionForCamera() {
        val permissions = arrayOf(Manifest.permission.CAMERA)
        val isPermissionGranted = PermissionUtil.hasPermissions(this, *permissions)
        if (!isPermissionGranted) {
            AppUtil.showAlertDialog(
                this,
                getString(R.string.permission_title),
                getString(R.string.permission_description),
                getString(R.string.button_ok),
                false
            ) { _: DialogInterface?, i: Int ->
                ActivityCompat.requestPermissions(
                    this, permissions,
                    PermissionUtil.REQUEST_CODE_MULTIPLE_PERMISSIONS
                )
            }
        } else {
            openCameraActivity()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == APP_CAMERA_ACTIVITY_REQUEST_CODE) {
                val mrzInfo = data!!.getSerializableExtra(CaptureActivity.MRZ_RESULT) as MRZInfo?
                if (mrzInfo != null) {
                    val intent = Intent(this, ReadingPassportActivity::class.java)
                    intent.putExtra("passportNumber", mrzInfo.documentNumber)
                    intent.putExtra("dateOfBirth", mrzInfo.dateOfBirth)
                    intent.putExtra("dateOfExpiration", mrzInfo.dateOfExpiry)
                    startActivity(intent)
                } else AppUtil.showAlertDialog(
                    this,
                    getString(R.string.error),
                    getString(R.string.error_camera_result_description),
                    getString(R.string.button_ok),
                    false
                ) { dialogInterface: DialogInterface?, i: Int -> openCameraActivity() }
            }
        }
    }

    private fun openCameraActivity() {
        val intent = Intent(this, CaptureActivity::class.java)
        intent.putExtra(DOC_TYPE, docType)
        startActivityForResult(intent, APP_CAMERA_ACTIVITY_REQUEST_CODE)
    }


}