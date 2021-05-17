package com.money.well.common;

import com.money.well.controller.model.GroupObject;
import com.money.well.controller.model.MemberObject;
import com.money.well.controller.model.NationObject;

import java.util.ArrayList;

public class Global {

     // region -- test --
    public static boolean _isTest = true;
    public static boolean _isCloud = false;
    public static boolean _isVerificationTest = true;
    public static boolean _isNetworkTest = true;
    // endregion

    // region --- sdk ---
    public static int sdkFacebook = 1;
    public static int sdkGoogle = 2;
    // endregion

    // region --- paypal ---
    public static double paypalFeeRate = 0.1;
    // endregion

    // region -- http communication [201, 300] --
    public static int RESULT_SUCCESS = 201;
    public static int RESULT_FAILED = 202;
    public static int RESULT_EMAIL_PASSWORD_INCORRECT = 203;
    public static int RESULT_ACCOUNT_DUPLICATE = 204;
    public static int RESULT_EMAIL_DUPLICATE = 205;
    public static int RESULT_EMPTY = 206;
    public static int VERIFICATION_ERROR_STATUS = 207;
    public static int RESULT_ALREADY_EXIST = 207;

    public static int RESULT_VERIFICATION_CODE_USED = 215;
    public static int RESULT_VERIFICATION_CODE_INCORRECT = 216;
    // endregion

    public static int HELP_FAQ = 1;
    public static int HELP_CONTACT_US = 2;
    public static int HELP_PRIVATE_POLICY = 3;
    public static int clickTabIndex = 0;
    public static int HOME_CLICKED = 0;
    public static int GROUP_CLICKED = 1;
    public static int CONTACTS_CLICKED = 2;
    public static int SETTING_CLICKED = 3;

    // region --common information --
    public static String helpFAQ = "";
    public static String helpContact = "";
    public static String helpPrivacy = "";
    // endregion

    // region --user information --
    public static int userId;
    public static String userName = "";
    public static String userEmail = "";
    public static String userPaypalEmail = "";
    public static String userPassword = "";
    public static String userPhotoUrl = "";
    public static String userBirthday = "";
    public static int userNationId = 0;
    public static int userGender;
    public static String userFacebookId = "";
    public static String userGoogleId = "";
    public static int userEmailBindingStatus;
    // endregion

    // region ---- array list ----
    public static ArrayList<NationObject> nationObjectArrayList;
    public static ArrayList<GroupObject> groupObjectArrayList;
    // endregion

    // region ---- image upload ----
    public static final String BASE_URL = "http://192.168.1.16:21000";
    public static final String IMAGE_SAVE_DIRECTORY = "/moneyWell/image";
    public static int IMAGE_UPLOAD_TYPE_USER = 1;
    public static int IMAGE_UPLOAD_TYPE_GROUP = 2;
    public static int IMAGE_UPLOAD_TYPE_GOAL = 3;
    // endregion

    // region ---- group ----
    public static int currentGroupId;
    public static String currentGroupName = "";
    public static String currentGroupPhotoUrl = "";
    public static String currentGroupAdminName = "";
    public static boolean currentGroupContributionStatus = true;
    public static int currentGroupMemberCount = 0;
    public static int currentGroupGoalCount = 0;
    public static boolean currentGroupJoinStatus = false;
    // endregion

    // region ---- member ----
    public static int currentMemberId;

    // endregion
    // region ---- contribution ----
    public static int contributionReceiverId;
    public static String contributionReceiverPhotoUrl;
    public static String contributionReceiverName;
    public static String contributionReceiverPayPalAccount = "";
    // endregion

    // region ---- donation ----
    public static int currentDonationId;
    public static String donationPhotoUrl = "";
    public static String donationTitle = "";
    public static String donationContent = "";
    public static int donationOnceAmount = 0;
    public static int userDonatedAmount = 0;
    // endregion
    // region ---- image file ----
    public static String imageDirectoryPath = "moneyWell/image/";
    // endregion
    // region ---- arrayList ----
    public static ArrayList<MemberObject> memberObjectArrayList;
    // endregion

    public static String strVerifyEmailActivity = "";

    // region ---- Paypal Information ----
    public static String paypalRequestUrl = "https://svcs.sandbox.paypal.com/AdaptivePayments/Pay";
    public static String paypalSecurityUseId = "moneywell_api1.business.com";
    public static String paypalSecurityPassword = "V4UKAFB2X6QALNLK";
    public static String paypalSecuritySignature = "A1.wZfLl880BnPvVsdm8nheciFuFA4ncgwrkiPo00x1TykchsXs5ug7J";
    public static String paypalApplicationId = "APP-80W284485P519543T";
    public static String paypalPageUrl = "https://sandbox.paypal.com/webscr?cmd=_ap-payment&paykey=";

    public static String paypalPayKey = "";
    // endregion

 }