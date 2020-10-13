package com.wanggh8.mydrive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wanggh8.mydrive.R;
import com.wanggh8.mydrive.base.BaseAdapter;
import com.wanggh8.mydrive.base.BaseViewHolder;
import com.wanggh8.mydrive.bean.DriveNewBean;

import java.util.List;

/**
 * 新建云盘列表Adapter
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/10/10
 */
public class DriveNewAdapter extends BaseAdapter<DriveNewBean> {
    private int selected = -1;

    public DriveNewAdapter(Context context, List<DriveNewBean> list) {
        super(context, list);
    }

    public DriveNewAdapter(Context context) {
        super(context);
    }

    public void setSelected(int position) {
        selected = position;
        notifyDataSetChanged();
    }

    public int getSelected() {
        return selected;
    }

    @Override
    public BaseViewHolder<DriveNewBean> onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return new DriveItemHolder(inflater.inflate(R.layout.item_drive_new, parent, false));
    }

    class DriveItemHolder extends BaseViewHolder<DriveNewBean> {

        private ImageView ivDriveItem;
        private TextView tvDriveItem;
        private ImageView ivDriveArrow;

        public DriveItemHolder(View itemView) {
            super(itemView);
            ivDriveItem = (ImageView) findViewById(R.id.iv_drive_item);
            tvDriveItem = (TextView) findViewById(R.id.tv_drive_item);
            ivDriveArrow = (ImageView) findViewById(R.id.iv_drive_arrow);
        }

        @Override
        public void onBind(DriveNewBean bean, int position) {
            ivDriveItem.setImageResource(bean.getIconId());
            tvDriveItem.setText(bean.getName());
        }
    }
}
