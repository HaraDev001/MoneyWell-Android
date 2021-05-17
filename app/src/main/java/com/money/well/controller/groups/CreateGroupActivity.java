package com.money.well.controller.groups;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.money.well.R;
import com.money.well.common.Global;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.base.BaseActivity;
import com.money.well.utils.dialog.CustomProgress;
import com.money.well.utils.dialog.PhotoPopupWindow;
import com.money.well.utils.network.HttpCall;
import com.money.well.utils.network.HttpRequest;
import com.money.well.utils.picutils.PictureUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.money.well.common.Global.BASE_URL;
import static com.money.well.common.Global.IMAGE_SAVE_DIRECTORY;
import static com.money.well.common.Global.IMAGE_UPLOAD_TYPE_GROUP;
import static com.money.well.common.Global.IMAGE_UPLOAD_TYPE_USER;
import static com.money.well.common.Global.RESULT_SUCCESS;
import static com.money.well.common.Global.imageDirectoryPath;
import static com.money.well.common.Global.userId;
import static com.money.well.common.Global.userPassword;
import static com.money.well.common.Global.userPhotoUrl;

public class CreateGroupActivity extends BaseActivity {

    private static final String TAG = "CreateGroupActivity";
    private ImageView imgTopLeft;
    private TextView txtTopTitle;

    private Button btnCreate;
    private TextView txtSuccess;
    private boolean createStatus = false;
    private ImageView imgProfile;
    private EditText edtGroupName;

    PhotoPopupWindow mPhotoPopupWindow;
    private static String[] albumPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static String[] [] cameraPermissions = new String[] []{
            {Manifest.permission.CAMERA, "camera"},
            {Manifest.permission.WRITE_EXTERNAL_STORAGE, "storage"}
    };
    private static final int REQUEST_IMAGE_GET = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_SMALL_IMAGE_CUTTING = 2;
    private static final int CAMERA_PERMISION_REQUEST = 300;
    private static final int STORAGE_PERMISION_REQUEST = 200;
    private static final String IMAGE_FILE_NAME = "group_logo.jpg";

    private String imagePath = "";
    private String imageUploadingPath = "";
    private int newGroupId;
    private String newGroupName;
    private String newGroupPhotoUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_create_group);

        initBasicUI();
    }

    private void initBasicUI(){
        imgTopLeft = findViewById(R.id.img_top_left);
        imgTopLeft.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                finish();
            }
        });
        txtTopTitle = findViewById(R.id.txt_top_title);
        txtTopTitle.setText(R.string.title_create_group);

        btnCreate = findViewById(R.id.btn_create);
        btnCreate.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                if (!createStatus){
                    String strGroupName = edtGroupName.getText().toString();
                    createGroup(imagePath, strGroupName);
                }
                else{
//                    Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
//                    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//                    List<ResolveInfo> pkgAppsList = thisContext.getPackageManager().queryIntentActivities( mainIntent, 0);
//                    showToast(pkgAppsList.size() + "");
                    sendingShareInformation();
                }
            }
        });
        txtSuccess = findViewById(R.id.txt_create_success);
        imgProfile = findViewById(R.id.img_profile);
        imgProfile.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                takeGroupImage();
            }
        });
        edtGroupName = findViewById(R.id.edt_group_name);
    }

    private void takeGroupImage(){
        PictureUtil.mkdirImageRootDirectory();
        mPhotoPopupWindow = new PhotoPopupWindow(CreateGroupActivity.this, new View.OnClickListener() {
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
                        Toast.makeText(thisContext, "Can not find picture", Toast.LENGTH_SHORT).show();
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
        View rootView = LayoutInflater.from(CreateGroupActivity.this).inflate(R.layout.activity_main, null);
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
                            String fileName = Global.userId + "group_new.jpg";
                            imagePath = saveFile(CreateGroupActivity.this, photo, fileName);
                            if (imagePath == null) {
                                showToast("Image upload failed");
                                return;
                            }
                            final String imagePath = Uri.decode(fileName);
                            imageUploadingPath = imagePath;
                            showImageFromFile(imageDirectoryPath + imagePath);
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
        }else{
            Log.e(TAG,"result = "+resultCode+",request = "+requestCode);
        }
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
                Log.i(TAG, "图片地址 " + BASE_URL + result);
                //todo ma display image
                try {
                    JSONObject subresponse = new JSONObject(result);
                    int responseCode = (int) subresponse.get("code");

                    if (responseCode == RESULT_SUCCESS){
                        JSONObject responseData =  subresponse.getJSONObject("data");
                        newGroupPhotoUrl = responseData.getString("photo_url");
                        showImage();
                        CustomProgress.dismissDialog();
                    }
                    else{
                        CustomProgress.dismissDialog();
                        showToast("Group creating failed");
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

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("upload_type", IMAGE_UPLOAD_TYPE_GROUP + "")
                .addFormDataPart("upload_id", newGroupId + "")
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

            showToast("Group creating failed");
        }
        return result;
    }

    private void startPhotoZoom(Uri uri) {
        Log.d(TAG,"Uri = "+uri.toString());
        File cropFile=new File(PictureUtil.getImageRootDirectory(),"crop.jpg");
        try{
            if(cropFile.exists()){
                cropFile.delete();
                Log.e(TAG,"delete");
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

        Log.e(TAG,"cropUri = "+cropUri.toString());

        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, REQUEST_SMALL_IMAGE_CUTTING);
    }

    public void showImage(){
        Glide.with(thisContext)
                .load(newGroupPhotoUrl + "?" + System.currentTimeMillis())
                .into(imgProfile);

        btnCreate.setText("Share");
        txtSuccess.setVisibility(View.VISIBLE);
        createStatus = true;
    }

    private void showImageFromFile(String filePath){
        String completePath = Environment.getExternalStorageDirectory() + "/" + filePath;
        File file = new File(completePath);
        Uri imageUri = Uri.fromFile(file);

        Glide.with(thisActivity)
                .load(imageUri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imgProfile);
    }

    private static String saveFile(Context context, Bitmap bm, String fileName) throws IOException {

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

    private void sendingShareInformation(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "I created new group named " + newGroupName + " in moneyWell");
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void createGroup(String imageUrl, String groupName){
        if (checkValidation(imageUrl, groupName)){
            showProgressDialog(getString(R.string.message_content_loading));
            HttpCall httpCallPost = new HttpCall();
            httpCallPost.setMethodtype(HttpCall.POST);
            String url = "\n" + getServerUrl() + getString(R.string.str_url_create_group);
            httpCallPost.setUrl(url);
            HashMap<String,String> paramsPost = new HashMap<>();
            paramsPost.put("user_id", Global.userId + "");
            paramsPost.put("group_name", groupName);
            httpCallPost.setParams(paramsPost);
            new HttpRequest(){
                @Override
                public void onResponse(String str) {
                    super.onResponse(str);
                    try {
                        CustomProgress.dismissDialog();
                        JSONObject response = new JSONObject(str);
                        int responseCode = (int) response.get("code");
                        if (responseCode == Global.RESULT_SUCCESS) {
                            JSONObject responseData =  response.getJSONObject("data");
                            newGroupId = responseData.getInt("id");
                            newGroupName = responseData.getString("name");
                            uploadImage(imagePath);
                        }
                        else if (responseCode == Global.RESULT_ALREADY_EXIST) {
                            showToast("This group name already exist");
                        }
                        else if (responseCode == Global.RESULT_FAILED) {
                            showToast("Create group failed");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomProgress.dismissDialog();
                        showToast("Network error");
                    }

                }
            }.execute(httpCallPost);
        }
    }

    private boolean checkValidation(String imgUrl, String groupName){
        boolean isValue = true;
        if (imgUrl.equals("")){
            showToast(getString(R.string.notice_select_image));
            isValue = false;
        }
        else if (groupName.equals("")){
            showToast(getString(R.string.notice_input_group_name));
            isValue = false;
        }

        return isValue;
    }

}
