package com.wanggh8.mydrive.ui.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.wanggh8.mydrive.R;

/**
 * 通用双按钮Dialog
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/9/29
 */
public class CommonTitleDialog extends Dialog {

    private OnButtonClickListener onButtonClickListener;
    private Context context;

    private TextView tvDialogTitle;
    private TextView tvDialogTip;
    private TextView tvDialogLeft;
    private TextView tvDialogRight;
    // 左右按钮Hint
    private String leftBtnText, rightBtnText;
    // dialog标题
    private String title;
    // dialog内容
    private String tipContent;

    /**
     * 构造方法
     *
     * @param context 上下文
     * @param title 标题
     * @param tipContent 内容
     * @param leftBtnText 左按钮hint
     * @param rightBtnText 右按钮hint
     * @param onButtonClickListener 按钮侦听回调
     */
    public CommonTitleDialog(Context context, String title, String tipContent, String leftBtnText, String rightBtnText
            , OnButtonClickListener onButtonClickListener) {
        super(context, R.style.DialogStyle);
        this.context = context;
        this.title = title;
        this.tipContent = tipContent;
        this.leftBtnText = leftBtnText;
        this.rightBtnText = rightBtnText;
        this.onButtonClickListener = onButtonClickListener;
    }

    /**
     * 不带参构造方法
     *
     * @param context 上下文
     * @param onButtonClickListener 按钮侦听回调
     */
    public CommonTitleDialog(Context context, OnButtonClickListener onButtonClickListener) {
        super(context);
        this.context = context;
        this.leftBtnText = "确认";
        this.rightBtnText = "取消";
        this.onButtonClickListener = onButtonClickListener;
        this.tipContent = "";
        this.title = "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_common_title);

        tvDialogTitle = (TextView) findViewById(R.id.tv_dialog_title);
        tvDialogTip = (TextView) findViewById(R.id.tv_dialog_tip);
        tvDialogLeft = (TextView) findViewById(R.id.tv_dialog_left);
        tvDialogRight = (TextView) findViewById(R.id.tv_dialog_right);

        tvDialogLeft.setText(leftBtnText);
        tvDialogRight.setText(rightBtnText);
        // 左按钮侦听
        tvDialogLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onButtonClickListener != null) {
                    onButtonClickListener.onLeftClick();
                }
                CommonTitleDialog.this.dismiss();
            }
        });
        // 右按钮侦听
        tvDialogRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onButtonClickListener != null) {
                    onButtonClickListener.onRightClick();
                }
                CommonTitleDialog.this.dismiss();
            }
        });
        // show侦听
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (!TextUtils.isEmpty(title)) {
                    tvDialogTitle.setText(title);
                }
                tvDialogTip.setText(tipContent);
            }
        });
    }

    public void showDialog() {
        this.show();
    }

    public void showDialog(String msg) {
        this.tipContent = msg;
        this.show();
    }

    public void setContent(String content) {
        this.tipContent = content;
        tvDialogTip.setText(tipContent);
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }

    public interface OnButtonClickListener {
        /**
         * dialog左按钮侦听回调
         */
        void onLeftClick();

        /**
         * dialog右按钮侦听回调
         */
        void onRightClick();
    }
}
