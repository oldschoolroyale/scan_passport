package com.brm.machinereablezone.ui.entry;

import static com.brm.machinereablezone.ui.camera.CaptureActivity.MRZ_RESULT;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.brm.MainActivity;
import com.brm.machinereablezone.R;
import com.brm.machinereablezone.WaitingForNfcActivity;
import com.brm.machinereablezone.model.DocType;
import com.brm.machinereablezone.ui.camera.CaptureActivity;
import com.brm.machinereablezone.utils.AppUtil;
import com.brm.machinereablezone.utils.PermissionUtil;
import com.google.android.material.snackbar.Snackbar;

import org.jmrtd.lds.icao.MRZInfo;

public class EntryActivity extends AppCompatActivity {

    private static final int APP_CAMERA_ACTIVITY_REQUEST_CODE = 150;
    private static final String DOC_TYPE = "DOC_TYPE";
    private DocType docType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        docType = DocType.PASSPORT;
        requestPermissionForCamera();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == APP_CAMERA_ACTIVITY_REQUEST_CODE) {
                MRZInfo mrzInfo = (MRZInfo) data.getSerializableExtra(MRZ_RESULT);
                if (mrzInfo != null) {
                    Intent intent = new Intent(this, WaitingForNfcActivity.class);
                    intent.putExtra("passportNumber",mrzInfo.getDocumentNumber());
                    intent.putExtra("dateOfBirth", mrzInfo.getDateOfBirth());
                    intent.putExtra("dateOfExpiration", mrzInfo.getDateOfExpiry());
                    startActivity(intent);
                }
                else
                    AppUtil.showAlertDialog(this, getString(R.string.error), getString(R.string.error_camera_result_description),
                            getString(R.string.button_ok), false, (dialogInterface, i) -> openCameraActivity());
            }
        }
    }

    private void requestPermissionForCamera() {
        String[] permissions = { Manifest.permission.CAMERA };
        boolean isPermissionGranted = PermissionUtil.hasPermissions(this, permissions);

        if (!isPermissionGranted) {
            AppUtil.showAlertDialog(this, getString(R.string.permission_title), getString(R.string.permission_description),
                    getString(R.string.button_ok), false, (dialogInterface, i) -> ActivityCompat.requestPermissions(this, permissions,
                            PermissionUtil.REQUEST_CODE_MULTIPLE_PERMISSIONS));
        } else {
            openCameraActivity();
        }
    }

    private void openCameraActivity() {
        Intent intent = new Intent(this, CaptureActivity.class);
        intent.putExtra(DOC_TYPE, docType);
        startActivityForResult(intent, APP_CAMERA_ACTIVITY_REQUEST_CODE);
    }
}