package com.money.well.controller.signup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.money.well.R;
import com.money.well.common.Global;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.base.BaseActivity;
import com.money.well.controller.login.LoginActivity;
import com.money.well.controller.settings.EditPhotoActivity;
import com.money.well.utils.dialog.CustomProgress;
import com.money.well.utils.dialog.PhotoPopupWindow;
import com.money.well.utils.picutils.PictureUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.money.well.common.Global.BASE_URL;
import static com.money.well.common.Global.IMAGE_SAVE_DIRECTORY;
import static com.money.well.common.Global.IMAGE_UPLOAD_TYPE_USER;
import static com.money.well.common.Global.RESULT_SUCCESS;
import static com.money.well.common.Global._isTest;
import static com.money.well.common.Global.userId;
import static com.money.well.common.Global.userPhotoUrl;

public class UploadPhotoActivity extends BaseActivity {
    private TextView txtTopTitle;

    private Button btnUpload;
    private ImageView imgTopLeft;

    private ImageView imgProfile;
    PhotoPopupWindow mPhotoPopupWindow;
    public static String[] albumPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static String[] [] cameraPermissions = new String[] []{
            {Manifest.permission.CAMERA, "camera"},
            {Manifest.permission.WRITE_EXTERNAL_STORAGE, "storage"}
    };

    private static final int REQUEST_IMAGE_GET = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SMALL_IMAGE_CUTTING = 2;
    private static final int CAMERA_PERMISION_REQUEST = 300;
    private static final int STORAGE_PERMISION_REQUEST = 200;
    private static final String IMAGE_FILE_NAME = "user_head_icon.jpg";
    private String touxiangpath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_upload_photo);

        initBasicUI();
    }

    private void initBasicUI(){
        txtTopTitle = findViewById(R.id.txt_top_title);
        txtTopTitle.setText(getString(R.string.title_upload_photo));
        btnUpload = findViewById(R.id.btn_upload);
        btnUpload.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                String strButton = btnUpload.getText().toString();
                if (strButton.equals("Upload")){
                    uploadUserImage();
                }
                else if (strButton.equals("Finish")){
                    gotoLoginActivity();
                }
            }
        });

        imgTopLeft = findViewById(R.id.img_top_left);
        imgTopLeft.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                finish();
            }
        });

        imgProfile = findViewById(R.id.img_profile);
    }

    private void gotoLoginActivity(){
        Intent intent = new Intent(UploadPhotoActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        thisActivity.startActivity(intent);

        finish();
    }

    private void uploadUserImage(){
        PictureUtil.mkdirImageRootDirectory();
        mPhotoPopupWindow = new PhotoPopupWindow(UploadPhotoActivity.this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> mPermissionList = new ArrayList<>();
                mPermissionList.clear();

                for (int i = 0; i < albumPermissions.length; i++)
                {
                    if (ContextCompat.checkSelfPermission(thisContext, albumPermissions[i]) != PackageManager.PERMISSION_GRANTED)
                    {
                        mPermissionList.add(albumPermissions[i]);
                    }
                }

                if (mPermissionList.isEmpty())
                {
                    mPhotoPopupWindow.dismiss();
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_IMAGE_GET);
                    } else {
                        Toast.makeText(UploadPhotoActivity.this, "Can not find picture", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    String[] requestPermissions = mPermissionList.toArray(new String[mPermissionList.size()]);

                    ActivityCompat.requestPermissions(thisActivity, requestPermissions, STORAGE_PERMISION_REQUEST);
                }
            }
        }, new View.OnClickListener()
        {
            @Override
            public void onClick (View v){
                List<String> mPermissionList = new ArrayList<>();
                mPermissionList.clear();

                for (int i = 0; i < cameraPermissions.length; i++)
                {
                    if (ContextCompat.checkSelfPermission(thisContext, cameraPermissions[i][0]) != PackageManager.PERMISSION_GRANTED)
                    {
                        mPermissionList.add(cameraPermissions[i][0]);
                    }
                }

                if (mPermissionList.isEmpty())
                {
                    mPhotoPopupWindow.dismiss();
                    imageCapture();
                }
                else
                {
                    String[] requestPermissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                    ActivityCompat.requestPermissions(thisActivity, requestPermissions, CAMERA_PERMISION_REQUEST);
                }
            }
        });
        View rootView = LayoutInflater.from(UploadPhotoActivity.this).inflate(R.layout.activity_main, null);
        mPhotoPopupWindow.showAtLocation(rootView,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void imageCapture() {
        final Intent intent;
        Uri pictureUri;
        File pictureFile = new File(PictureUtil.getImageRootDirectory(), IMAGE_FILE_NAME);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pictureUri = FileProvider.getUriForFile(this,
                    thisActivity.getPackageName() + ".fileprovider", pictureFile);
        } else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            pictureUri = Uri.fromFile(pictureFile);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);

        try {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
        catch (Exception e){
            showToast("Camera permission denied");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case STORAGE_PERMISION_REQUEST:
                boolean RequestSettingPermission = false;
                for (int i = 0; i < grantResults.length; i++)
                {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    {
                        RequestSettingPermission = true;
                    }
                }

                if (RequestSettingPermission)
                {
                    mPhotoPopupWindow.dismiss();
                    showToast(getString(R.string.notice_open_storage_permission));
                }
                break;

            case CAMERA_PERMISION_REQUEST:
                boolean RequestSettingCameraPermission = false;
                for (int i = 0; i < grantResults.length; i++)
                {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    {
                        checkExistInPermissionArray(permissions[i]);
                        RequestSettingCameraPermission = true;
                    }
                }

                if (RequestSettingCameraPermission)
                {
                    showToast(getString(R.string.notice_permission_before) + dialogString + getString(R.string.notice_permission));
                    dialogString = "";
                }
                else{
                    mPhotoPopupWindow.dismiss();
                    imageCapture();
                }

                break;
        }
    }

    private String dialogString = "";
    private void checkExistInPermissionArray(String permissionStr) {
        for (int i = 0; i < cameraPermissions.length; i++) {
            if (cameraPermissions[i][0].contains(permissionStr)) {
                if (!dialogString.equals(""))
                    dialogString += ", ";
                dialogString += cameraPermissions[i][1];
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 回调成功
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_SMALL_IMAGE_CUTTING:
                    File cropFile= new File(PictureUtil.getImageRootDirectory(),"crop.jpg");
                    Uri cropUri = Uri.fromFile(cropFile);
                    Bitmap photoFirst = null;
                    Bundle extras = data.getExtras();
                    if (cropUri != null) {
                        try {
                            photoFirst = BitmapFactory.decodeStream(getContentResolver().openInputStream(cropUri));
                        }catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        final Bitmap photo = photoFirst;

                        try {
                            String fileName = Global.userId + "touxiang.jpg";
                            touxiangpath = saveFile(UploadPhotoActivity.this, photo, fileName);
                            if (touxiangpath == null) {
                                showToast("Image upload failed");
                                return;
                            }
                            final String imagePath = Uri.decode(fileName);
//                            showImageFromFile(touxiangpath);
                            uploadImage(touxiangpath);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REQUEST_IMAGE_GET:
                    Uri uri= PictureUtil.getImageUri(this,data);
                    startPhotoZoom(uri);
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    File pictureFile = new File(PictureUtil.getImageRootDirectory(), IMAGE_FILE_NAME);
                    Uri pictureUri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        pictureUri = FileProvider.getUriForFile(this,
                                thisActivity.getPackageName()+".fileprovider", pictureFile);
                    } else {
                        pictureUri = Uri.fromFile(pictureFile);
                    }
                    startPhotoZoom(pictureUri);
                    break;
                default:
            }
        }
    }


    private void startPhotoZoom(Uri uri) {
        File cropFile=new File(PictureUtil.getImageRootDirectory(),"crop.jpg");
        try{
            if(cropFile.exists()){
                cropFile.delete();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        Uri cropUri;
        cropUri = Uri.fromFile(cropFile);

        final Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1); // 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300); // 输出图片大小
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, REQUEST_SMALL_IMAGE_CUTTING);
    }
    private void uploadImage(String imagePath) {
        new NetworkTask().execute(imagePath);
    }

    class NetworkTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return doPost(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if(!"error".equals(result)) {
                //todo ma display image
                try {
                    JSONObject subresponse = new JSONObject(result);
                    int responseCode = (int) subresponse.get("code");

                    if (responseCode == RESULT_SUCCESS){
                        JSONObject responseData =  subresponse.getJSONObject("data");
                        Global.userPhotoUrl = responseData.getString("photo_url");
                        showImage();
                        CustomProgress.dismissDialog();
                    }
                    else{
                        CustomProgress.dismissDialog();
                        showToast("Image uploading failed");
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                    CustomProgress.dismissDialog();
                }
            }
        }
    }

    private String doPost(String imagePath) {
        OkHttpClient mOkHttpClient = new OkHttpClient();

        String result = "error";
        String strId = "";
        if (_isTest){
            strId = 1 + "";
        }
        else {
            strId = userId + "";
        }

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("upload_type", IMAGE_UPLOAD_TYPE_USER + "")
                .addFormDataPart("upload_id", strId)
                .addFormDataPart("image", imagePath, RequestBody.create(MediaType.parse("image/png"), new File(imagePath)))
                .build();
        Request.Builder reqBuilder = new Request.Builder();
        Request request = reqBuilder
                .url(getServerUrl() + getString(R.string.str_url_image_upload))
                .post(requestBody)
                .build();

        try{
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String resultValue = response.body().string();
                return resultValue;
            }
        } catch (Exception e) {
            e.printStackTrace();

            showToast("Image upload failed");
        }
        return result;
    }

    public static String saveFile(Context context, Bitmap bm, String fileName) throws IOException {

        File path = new File(getExternalStorePath() + IMAGE_SAVE_DIRECTORY);
        if (!path.exists()) {
            path.mkdirs();
        }

        File file = new File(path, fileName);

        if (!file.exists()) {
            file.createNewFile();
        } else {
            file.delete();
            file.createNewFile();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
        return file.getPath();
    }

    public void showImage(){
        showToast("Register success");
        Glide.with(thisContext)
                .load(userPhotoUrl + "?" + System.currentTimeMillis())
                .into(imgProfile);
        btnUpload.setText("Finish");
    }

}
