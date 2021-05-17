package com.money.well.controller.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.money.well.R;
import com.money.well.controller.model.MemberObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class MemberAdapter extends ArrayAdapter<MemberObject> {
    private Context _context = null;
    private int _layoutResourceId = 0;
    private ArrayList<MemberObject> _mainStaffInfoView = null;


    public MemberAdapter(@NonNull Context context, int resource, ArrayList<MemberObject> data) {
        super(context, resource, data);

        this._layoutResourceId = resource;
        this._context = context;
        this._mainStaffInfoView = data;
    }

    static class staffInfoViewHolder
    {
        CircleImageView imgMemberLogo;
        TextView txtMemberName;
        TextView txtMemberRole;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        staffInfoViewHolder holder = null;

        if(true)
        {
            LayoutInflater inflater = ((Activity)_context).getLayoutInflater();
            row = inflater.inflate(_layoutResourceId, parent, false);

            holder = new staffInfoViewHolder();
            holder.imgMemberLogo = row.findViewById(R.id.img_member);
            holder.txtMemberName = row.findViewById(R.id.txt_member_name);
            holder.txtMemberRole = row.findViewById(R.id.txt_member_role);
            row.setTag(holder);
        }

        MemberObject resultItem = _mainStaffInfoView.get(position);

        holder.txtMemberName.setText(resultItem.getName());
        holder.txtMemberRole.setText(resultItem.getMemberRole() + "");
        Glide.with(_context)
                .load(resultItem.getPhotoUrl())
//                .placeholder(R.drawable.logo_image)
                .into(holder.imgMemberLogo);

        return row;
    }
}
