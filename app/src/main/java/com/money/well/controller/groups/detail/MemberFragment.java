package com.money.well.controller.groups.detail;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.money.well.R;
import com.money.well.common.Global;
import com.money.well.controller.adapter.GroupAdapter;
import com.money.well.controller.adapter.MemberAdapter;
import com.money.well.controller.base.BaseFragment;
import com.money.well.controller.contacts.MemberDetailActivity;
import com.money.well.controller.groups.GroupDetailActivity;
import com.money.well.controller.model.MemberObject;
import com.money.well.utils.dialog.CustomProgress;
import com.money.well.utils.network.HttpCall;
import com.money.well.utils.network.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.money.well.common.Global.currentGroupId;
import static com.money.well.common.Global.memberObjectArrayList;
import static com.money.well.common.Global.currentMemberId;
import static com.money.well.common.Global.userId;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MemberFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MemberFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemberFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView txtMemberCount;
    private ListView lstMember;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MemberFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MemberFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MemberFragment newInstance(String param1, String param2) {
        MemberFragment fragment = new MemberFragment();
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
        View root = inflater.inflate(R.layout.fragment_member, container, false);

        txtMemberCount = root.findViewById(R.id.txt_member_count);
        lstMember = root.findViewById(R.id.lst_member);

        getGroupMemberList();

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

    private void getGroupMemberList(){
        showProgressDialog(getString(R.string.message_content_loading));
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_group_member);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("group_id", currentGroupId + "");
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
                        memberObjectArrayList = new ArrayList<>();
                        JSONArray responseData =  response.getJSONArray("data");
                        for (int i = 0; i < responseData.length(); i ++){
                            JSONObject temp = responseData.getJSONObject(i);
                            MemberObject memberObject = new MemberObject();
                            memberObject.setMemberId(temp.getInt("user_id"));
                            memberObject.setName(temp.getString("name"));
                            memberObject.setPhotoUrl(temp.getString("photo_url"));
                            if (temp.getInt("role") == 1){
                                memberObject.setMemberRole("Group Admin");
                            }
                            else{
                                memberObject.setMemberRole("Member");
                            }
                            memberObjectArrayList.add(memberObject);
                        }
                        showMemberList();
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

    private void showMemberList() {
        txtMemberCount.setText(memberObjectArrayList.size() + "");
        if (memberObjectArrayList != null){
            MemberAdapter memberAdapter = new MemberAdapter(getContext(),
                    R.layout.item_member, memberObjectArrayList);
            lstMember.setAdapter(memberAdapter);
            lstMember.setOnItemClickListener(onItemListener);
        }
    }

    ListView.OnItemClickListener onItemListener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            currentMemberId = memberObjectArrayList.get(position).getMemberId();

            Intent intent = new Intent(getContext(), MemberDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().startActivity(intent);
        }
    };
}
