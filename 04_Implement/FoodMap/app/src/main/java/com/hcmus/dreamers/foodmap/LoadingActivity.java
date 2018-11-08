package com.hcmus.dreamers.foodmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.hcmus.dreamers.foodmap.AsyncTask.TaskCompleteCallBack;
import com.hcmus.dreamers.foodmap.common.FoodMapApiManager;
import com.hcmus.dreamers.foodmap.define.ConstantCODE;


// Example here: https://github.com/googlesamples/android-RuntimePermissions

public class LoadingActivity extends AppCompatActivity {

    private static final int PERMISSION_CODEREQUEST = 9001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_layout);

        FoodMapApiManager.getRestaurant(LoadingActivity.this, new TaskCompleteCallBack() {
            @Override
            public void OnTaskComplete(Object response) {
                int code = (int) response;
                if (code == FoodMapApiManager.SUCCESS){

                    // Kiểm tra đã cấp các quyền truy cập.
                    if (ActivityCompat.checkSelfPermission(LoadingActivity.this,
                                                            Manifest.permission.CALL_PHONE)
                            == PackageManager.PERMISSION_GRANTED &&

                            ActivityCompat.checkSelfPermission(LoadingActivity.this,
                                                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED &&

                            ActivityCompat.checkSelfPermission(LoadingActivity.this,
                                                            Manifest.permission.ACCESS_FINE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED &&

                            ActivityCompat.checkSelfPermission(LoadingActivity.this,
                                                            Manifest.permission.ACCESS_COARSE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {

                        // Nếu tất cả đều đã được cấp, mở bản đồ
                        openMap();
                    } else {

                        // Có ít nhất 1 quyền chưa được cấp: yêu cầu được cấp
                        requestMapPermission();
                    }
                }
                else if (code == FoodMapApiManager.FAIL_INFO){
                    Toast.makeText(LoadingActivity.this, "Error", Toast.LENGTH_LONG).show();
                }
                else if (code == FoodMapApiManager.PARSE_FAIL){
                    Toast.makeText(LoadingActivity.this, "Error", Toast.LENGTH_LONG).show();
                }
                else if (code == ConstantCODE.NOTINTERNET){
                    Toast.makeText(LoadingActivity.this, "Kiểm tra kết nối mạng", Toast.LENGTH_LONG).show();
                }

                //finish();
            }
        });
    }

    private void requestMapPermission() {
        String[] permissions = { Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CALL_PHONE};

        ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODEREQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_CODEREQUEST)
        {
            // Kiểm tra tất cả các quyền đều được cấp thành công từ dùng

            boolean allPermissionAreGranted = true;

            for (int i = 0; i <grantResults.length;i++)
            {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                {
                    allPermissionAreGranted = false;
                    break;
                }
            }

            if (allPermissionAreGranted){
                openMap();
            } else {
                // Nếu tồn tại 1 quyền truy cập chưa được cấp, hiện thông báo, yêu cầu cấp lại?

                Toast.makeText(LoadingActivity.this,
                        R.string.map_permission_denied,
                        Toast.LENGTH_LONG)
                        .show();



            }
        }
    }

    private void openMap() {

        // Chuyển tới bản đồ chính
        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
        startActivity(intent);
    }


}
