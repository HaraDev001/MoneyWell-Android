package com.money.well.controller.groups.detail;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.money.well.R;
import com.money.well.common.Global;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.base.BaseFragment;
import com.money.well.controller.groups.SendMoneyActivity;
import com.money.well.utils.dialog.CustomProgress;
import com.money.well.utils.network.HttpCall;
import com.money.well.utils.network.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.money.well.common.Global.contributionReceiverId;
import static com.money.well.common.Global.currentGroupId;
import static com.money.well.common.Global.userId;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContributionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContributionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContributionFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView txtNextMember;
    private TextView txtNextDate;
    private TextView txtAmount;
    private LinearLayout layActive;
    private LinearLayout layDeactive;
    private LinearLayout laySendMoney;
    private ImageView imgActive;
    private ImageView imgDeactive;
    private ImageView imgSendMoney;

    private String contributionNextMember = "";
    private String contributionNextDate = "";
    private String contributionAmount = "";
    private boolean contributionActiveStatus = false;

    private OnFragmentInteractionListener mListener;

    public ContributionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContributionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContributionFragment newInstance(String param1, String param2) {
        ContributionFragment fragment = new ContributionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_contribution, container, false);
        txtNextMember = root.findViewById(R.id.txt_next_member_name);
        txtNextDate = root.findViewById(R.id.txt_next_date);
        txtAmount = root.findViewById(R.id.txt_amount);

        laySendMoney = root.findViewById(R.id.lay_send_money);
        imgSendMoney = root.findViewById(R.id.img_send_money);

        imgSendMoney.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                gotoSendMoneyActivity();
            }
        });
        layActive = root.findViewById(R.id.lay_active);
        imgActive = root.findViewById(R.id.img_active);
        imgActive.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setMessage(getContext().getString(R.string.alert_active_contribution))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                showToast("ok");
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
        layDeactive = root.findViewById(R.id.lay_deactive);
        imgDeactive = root.findViewById(R.id.img_deactive);

        getGroupContribution();
        return root;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void getGroupContribution(){
        showProgressDialog(getString(R.string.message_content_loading));
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_group_contribution);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("group_id", currentGroupId + "");
        paramsPost.put("user_id", userId + "");
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
                        JSONObject responseData = response.getJSONObject("data");
                        contributionReceiverId = responseData.getInt("receiver_user_id");
                        contributionNextMember = responseData.getString("receiver_name");
                        contributionNextDate = responseData.getString("receive_date");
                        contributionAmount = responseData.getString("once_amount");
                        int status = responseData.getInt("contribution_active_status");
                        if(status == 1){
                            contributionActiveStatus = true;
                        }
                        else {
                            contributionActiveStatus = false;
                        }
                        showContributionInformation();
                    }
                    else if (responseCode == Global.RESULT_FAILED) {

                        showToast("Failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomProgress.dismissDialog();
                    showToast("Network error");
                }

            }
        }.execute(httpCallPost);
    }

    private void showContributionInformation(){
        txtNextMember.setText(contributionNextMember);
        txtNextDate.setText(standardDateFormat(contributionNextDate));
        txtAmount.setText(contributionAmount + "£");
        if (contributionActiveStatus){
            showActiveStatus();
        }
        else
            showDeactiveStatus();
    }

    private void showActiveStatus(){
        layActive.setVisibility(View.GONE);
        layDeactive.setVisibility(View.VISIBLE);
        laySendMoney.setVisibility(View.VISIBLE);
    }

    private void showDeactiveStatus(){
        layActive.setVisibility(View.VISIBLE);
        layDeactive.setVisibility(View.GONE);
        laySendMoney.setVisibility(View.GONE);
    }

    private void gotoSendMoneyActivity(){
        Intent intent = new Intent(getContext(), SendMoneyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(intent);
    }
}
