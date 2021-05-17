package com.money.well.controller.home.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.money.well.R;
import com.money.well.common.Global;
import com.money.well.common.OnMultiClickListener;
import com.money.well.controller.adapter.GroupAdapter;
import com.money.well.controller.base.BaseFragment;
import com.money.well.controller.contacts.MemberDetailActivity;
import com.money.well.controller.groups.CreateGroupActivity;
import com.money.well.controller.groups.GroupDetailActivity;
import com.money.well.controller.groups.JoinGroupActivity;
import com.money.well.controller.groups.SearchGroupActivity;
import com.money.well.controller.model.GroupObject;
import com.money.well.utils.dialog.CustomProgress;
import com.money.well.utils.network.HttpCall;
import com.money.well.utils.network.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.money.well.common.Global.currentGroupAdminName;
import static com.money.well.common.Global.currentGroupId;
import static com.money.well.common.Global.currentGroupName;
import static com.money.well.common.Global.currentGroupPhotoUrl;
import static com.money.well.common.Global.groupObjectArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ImageView imgCreateGroup;
    private ListView lstGroup;
    private LinearLayout layEmptyResult;

    public GroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupFragment newInstance(String param1, String param2) {
        GroupFragment fragment = new GroupFragment();
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
    public void onResume() {
        super.onResume();
        getUserGroup();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        imgCreateGroup = view.findViewById(R.id.img_create_group);
        imgCreateGroup.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(getActivity(), CreateGroupActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().startActivity(intent);
            }
        });
        lstGroup = view.findViewById(R.id.lst_group);
        layEmptyResult =view.findViewById(R.id.empty_result);
//        getUserGroup();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

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

    public static GroupFragment newInstance() {
        GroupFragment fragment = new GroupFragment();
        return fragment;
    }

    private void getUserGroup(){
        showProgressDialog(getString(R.string.message_content_loading));
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_get_user_group);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("user_id", Global.userId + "");
        httpCallPost.setParams(paramsPost);
        new HttpRequest(){
            @Override
        public void onResponse(String str) {
                super.onResponse(str);
                try {
                    JSONObject response = new JSONObject(str);
                    int responseCode = (int) response.get("code");
                    if (responseCode == Global.RESULT_SUCCESS) {
                        CustomProgress.dismissDialog();
                        groupObjectArrayList = new ArrayList<>();
                        JSONArray responseData =  response.getJSONArray("data");
                        for (int i = 0; i < responseData.length(); i++){
                            JSONObject temp = responseData.getJSONObject(i);
                            GroupObject groupObject = new GroupObject();
                            groupObject.setId(temp.getInt("id"));
                            groupObject.setName(temp.getString("name"));
                            groupObject.setPhotoUrl(temp.getString("photo_url"));
                            groupObject.setAdminName(temp.getString("admin_name"));
                            groupObject.setNoticeCount(i);
                            groupObjectArrayList.add(groupObject);
                        }
                        showGroupList();
                    }
                    else if (responseCode == Global.RESULT_EMPTY) {
                        CustomProgress.dismissDialog();
                        showEmpty();
                    }
                    else if (responseCode == Global.RESULT_FAILED) {
                        CustomProgress.dismissDialog();
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

    private void showGroupList(){
        layEmptyResult.setVisibility(View.GONE);
        lstGroup.setVisibility(View.VISIBLE);
        if (groupObjectArrayList != null){
            GroupAdapter groupAdapter = new GroupAdapter(getContext(),
                    R.layout.item_group, groupObjectArrayList);
            lstGroup.setAdapter(groupAdapter);
            lstGroup.setOnItemClickListener(onItemListener);
        }
    }

    ListView.OnItemClickListener onItemListener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            currentGroupId = groupObjectArrayList.get(position).getId();
            currentGroupName = groupObjectArrayList.get(position).getName();
            currentGroupPhotoUrl = groupObjectArrayList.get(position).getPhotoUrl();
            currentGroupAdminName = groupObjectArrayList.get(position).getAdminName();

            Intent intent = new Intent(getContext(), GroupDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().startActivity(intent);
        }
    };

    private void showEmpty(){
        layEmptyResult.setVisibility(View.VISIBLE);
        lstGroup.setVisibility(View.GONE);
    }
}
