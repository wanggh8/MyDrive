package com.wanggh8.fileview;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

/**
 * TBS浏览WebView
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/9/29
 */
public class TBSWebView extends FrameLayout implements TbsReaderView.ReaderCallback {

    private TbsReaderView mTbsReaderView;
    private OnGetFilePathListener mOnGetFilePathListener;

    private static String TAG = "TBSWebView";
    private Context context;

    public TBSWebView(Context context) {
        this(context, null, 0);
    }

    public TBSWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TBSWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTbsReaderView = new TbsReaderView(context, this);
        this.addView(mTbsReaderView, new LinearLayout.LayoutParams(-1, -1));
        this.context = context;
    }

    private TbsReaderView getTbsReaderView(Context context) {
        return new TbsReaderView(context, this);
    }

    /**
     * 预览文件
     *
     * @param mFile 显示的文件
     */
    public boolean displayFile(File mFile) {

        if (mFile != null && !TextUtils.isEmpty(mFile.toString())) {
            //增加下面一句解决没有TbsReaderTemp文件夹存在导致加载文件失败
            String bsReaderTemp = "/storage/emulated/0/TbsReaderTemp";
            File bsReaderTempFile =new File(bsReaderTemp);

            if (!bsReaderTempFile.exists()) {
                Log.d(TAG, "准备创建/storage/emulated/0/TbsReaderTemp！！");
                boolean mkdir = bsReaderTempFile.mkdir();
                if(!mkdir){
                    Log.d(TAG, "准备创建/storage/emulated/0/TbsReaderTemp！！");
                }
            }

            //加载文件
            Bundle localBundle = new Bundle();
            Log.d(TAG, mFile.toString());
            localBundle.putString("filePath", mFile.toString());
            Log.d(TAG, "displayFile: filePath  " + mFile.toString());

            localBundle.putString("tempPath", Environment.getExternalStorageDirectory() + "/" + "TbsReaderTemp");

            if (this.mTbsReaderView == null) {
                this.mTbsReaderView = getTbsReaderView(context);
            }

            boolean bool = this.mTbsReaderView.preOpen(getFileType(mFile.toString()), false);
            if (bool) {
                this.mTbsReaderView.openFile(localBundle);
                return true;
            }
            return false;
        } else {
            Log.d(TAG, "文件路径无效  ！" + mFile.toString());
            return false;
        }
    }

    /**
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    private String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            Log.d(TAG, "paramString---->null");
            return str;
        }
        Log.d(TAG, "paramString:" + paramString);
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            Log.d(TAG, "i <= -1");
            return str;
        }

        str = paramString.substring(i + 1);
        Log.d(TAG, "paramString.substring(i + 1)------>" + str);
        return str;
    }


    public void show() {
        if(mOnGetFilePathListener != null){
            mOnGetFilePathListener.onGetFilePath(this);
        }
    }

    public void setOnGetFilePathListener(OnGetFilePathListener mOnGetFilePathListener) {
        this.mOnGetFilePathListener = mOnGetFilePathListener;
    }

    /**
     * 获取File路径的接口
     */
    public interface OnGetFilePathListener {
        void onGetFilePath(TBSWebView view);
    }


    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {
        Log.d(TAG,"****************************************************" + integer);
    }

    public void onStopDisplay() {
        if (mTbsReaderView != null) {
            mTbsReaderView.onStop();
        }
    }
}

