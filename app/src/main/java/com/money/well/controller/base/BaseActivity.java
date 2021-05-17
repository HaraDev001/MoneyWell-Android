package com.money.well.controller.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.LoginManager;
import com.money.well.R;
import com.money.well.common.Global;
import com.money.well.utils.dialog.CustomProgress;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseActivity extends AppCompatActivity {
    public AppCompatActivity thisActivity;
    public Context thisContext;
    public View thisView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public String getServerUrl() {
        String httpUrl;
        if (Global._isCloud)
            httpUrl = getString(R.string.str_cloud_http_server);
        else
            httpUrl = getString(R.string.str_local_http_server);
        return httpUrl;
    }

    public void showToast(String content){
        Toast.makeText(thisContext, content, Toast.LENGTH_SHORT).show();
    }


    public String getMD5(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }

            return strBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public void facebookLogout(){
        LoginManager.getInstance().logOut();
    }

    public void showProgressDialog(String message){
        CustomProgress.dismissDialog();
        if(!((Activity) thisContext).isFinishing())
        {
            CustomProgress.show(thisContext, message, false, null, false);
        }
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     *getting external storage directory
     */
    public static String getExternalStorePath() {
        if (isExistExternalStore()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    public static boolean isExistExternalStore() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public String standardDecimalFormat(String inputAmount){
        String strAmount = "";
        if (!inputAmount.equals("")){
            DecimalFormat df = new DecimalFormat("#,###.00");
            Double amount = Double.parseDouble(inputAmount);
            strAmount = df.format(amount);
        }
        if (strAmount.equals(".00")){
            strAmount = "0.00";
        }
        return strAmount;
    }

}
