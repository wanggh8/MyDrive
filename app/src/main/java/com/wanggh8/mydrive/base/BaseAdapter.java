package com.wanggh8.mydrive.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl;
import com.daimajia.swipe.interfaces.SwipeAdapterInterface;
import com.daimajia.swipe.interfaces.SwipeItemMangerInterface;
import com.daimajia.swipe.util.Attributes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Adapter基类
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/9/21
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseViewHolder<T>> implements SwipeItemMangerInterface, SwipeAdapterInterface {

    public SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);
    protected Context context;
    // 数据集
    protected List<T> list;
    // 上次点击时间
    protected long lastClickTime = 0;

    public BaseAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;
    }

    public BaseAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
    }

    /* * * * * * * * 点击事件侦听 * * * * * * * */

    protected ItemClickListener<T> onItemClickListener;

    protected ItemLongClickListener<T> onItemLongClickListener;

    public interface ItemClickListener<T> {
        /**
         * 点击事件回调
         * @param position 点击位置
         * @param bean 点击对应的数据实体类
         */
        void onItemClick(int position, T bean);
    }

    public interface ItemLongClickListener<T> {
        /**
         * 长按事件回调
         * @param view itemView
         * @param position 点击位置
         * @param bean 点击对应的数据实体类
         */
        void onItemLongClick(View view, int position, T bean);
    }

    /* * * * * * * * ViewHolder * * * * * * * */

    /**
     * 有布局服务的ViewHolder构造方法
     *
     * @param inflater 布局服务
     * @param parent 父ViewGroup
     * @param viewType The view type of the new View.
     * @return BaseViewHolder<T>
     */
    public abstract BaseViewHolder<T> onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType);

    /**
     * 无布局服务的ViewHolder构造方法
     *
     * @param parent 父ViewGroup
     * @param viewType The view type of the new View.
     * @return BaseViewHolder<T>
     * @see #onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType)
     */
    @Override
    public BaseViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return onCreateViewHolder(layoutInflater, parent, viewType);
    }

    /**
     * ViewHolder数据绑定回调
     *
     * @param holder 当前ViewHolder
     * @param position 当前数据位置
     */
    @Override
    public void onBindViewHolder(BaseViewHolder<T> holder, final int position) {
        // 调用抽象接口绑定数据
        holder.onBind(getItem(position), position);
        // 设置点击侦听事件
        onClickEvent(holder.itemView, position);
    }

    /**
     * 点击事件
     *
     * @param v ViewHolder.itemView
     * @param position 当前数据位置
     */
    public void onClickEvent(View v, int position) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long currentTime = Calendar.getInstance().getTimeInMillis();
                if (currentTime - lastClickTime > 300) {
                    lastClickTime = currentTime;
                    onItemClickListener.onItemClick(position, getItem(position));
                }
            }
        });

        if (onItemLongClickListener != null) {
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemLongClickListener.onItemLongClick(v, position, getItem(position));
                    return true;
                }
            });
        }
    }

    /**
     * 获取Item
     *
     * @param position 位置
     * @return
     */
    public T getItem(int position) {
        return list == null ? null : list.get(position);
    }

    /**
     * 获取Item数目
     *
     * @return int 数目
     */
    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    /**
     * 获取数据列表List
     *
     * @return List
     */
    public List<T> getCollection() {
        return list;
    }

    /**
     * 设置数据列表List
     *
     * @param list 数据列表
     */
    public void setCollection(List<T> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        this.list.clear();
        this.list.addAll(list);
        this.notifyDataSetChanged();
    }

    /**
     * 在数据列表后添加list
     * @param list 数据列表
     */
    public void appendCollection(List<T> list) {
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
        this.list.addAll(list);
        this.notifyDataSetChanged();
    }

    /**
     * 在数据列表具体位置添加
     *
     * @param pos 期望添加的数据的在列表中的第一个位置
     * @param list 数据列表
     */
    public void insertCollection(int pos, List<T> list) {
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
        this.list.addAll(pos, list);
        this.notifyDataSetChanged();
    }

    /* * * * * * * * SwipeLayout * * * * * * * */

    @Override
    public void openItem(int position) {
        mItemManger.openItem(position);
    }

    @Override
    public void closeItem(int position) {
        mItemManger.closeItem(position);
    }

    @Override
    public void closeAllExcept(SwipeLayout layout) {
        mItemManger.closeAllExcept(layout);
    }

    @Override
    public void closeAllItems() {
        mItemManger.closeAllItems();
    }

    @Override
    public List<Integer> getOpenItems() {
        return mItemManger.getOpenItems();
    }

    @Override
    public List<SwipeLayout> getOpenLayouts() {
        return mItemManger.getOpenLayouts();
    }

    @Override
    public void removeShownLayouts(SwipeLayout layout) {
        mItemManger.removeShownLayouts(layout);
    }

    @Override
    public boolean isOpen(int position) {
        return mItemManger.isOpen(position);
    }

    @Override
    public Attributes.Mode getMode() {
        return mItemManger.getMode();
    }

    @Override
    public void setMode(Attributes.Mode mode) {
        mItemManger.setMode(mode);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return position;
    }
}
