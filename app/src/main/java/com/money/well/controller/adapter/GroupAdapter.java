package com.money.well.controller.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.money.well.R;
import com.money.well.controller.model.GroupObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class GroupAdapter extends ArrayAdapter<GroupObject> {
    private Context _context = null;
    private int _layoutResourceId = 0;
    private ArrayList<GroupObject> _mainStaffInfoView = null;


    public GroupAdapter(@NonNull Context context, int resource, ArrayList<GroupObject> data) {
        super(context, resource, data);

        this._layoutResourceId = resource;
        this._context = context;
        this._mainStaffInfoView = data;
    }

    static class staffInfoViewHolder
    {
        CircleImageView imgGroupLogo;
        TextView txtGroupName;
        TextView txtNoticeCount;
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
            holder.imgGroupLogo = row.findViewById(R.id.img_group);
            holder.txtGroupName = row.findViewById(R.id.txt_group_name);
            holder.txtNoticeCount = row.findViewById(R.id.txt_unread_count);
            row.setTag(holder);
        }

        GroupObject resultItem = _mainStaffInfoView.get(position);

        holder.txtGroupName.setText(resultItem.getName());
        holder.txtNoticeCount.setText(resultItem.getNoticeCount() + "");
        Glide.with(_context)
                .load(resultItem.getPhotoUrl())
//                .placeholder(R.drawable.logo_image)
                .into(holder.imgGroupLogo);

        return row;
    }
}
