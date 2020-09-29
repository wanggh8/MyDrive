package com.wanggh8.fileview;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.hjq.toast.ToastUtils;

import com.wanggh8.fileview.base.BaseActivity;
import com.wanggh8.fileview.base.BaseApplication;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.io.File;

import okhttp3.ResponseBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Response;

/**
 * Office 文件预览
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/9/29
 */
public class OfficeViewActivity extends BaseActivity implements OfficeViewContract.View{
    private OfficeViewContract.Presenter presenter;

    private CommonTitleBar mTitleBar;
    private Context context = BaseApplication.getAppContext();
    private CircularProgressView progressViewOffice;
    private CommonTitleDialog dialog;
    private TBSWebView webViewOffice;
    private View openWPSView;

    // 文件参数
    String fileUrl = "";
    String fileName = "";
    String fileType = "";
    private File mFile;

    @Override
    public int getContentLayout() {
        return R.layout.act_office_view;
    }

    private void initByIntent() {
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        String fileUrl = (String) bundle.getString("fileUrl", "");
        String fileName = (String) bundle.getString("fileName", "");
        String fileType = (String) bundle.getString("fileType", "");
    }

    @Override
    public void beforeInitView() {
        presenter = new OfficeViewPresenter(this, this);
        initByIntent();
    }


    @Override
    public void initView() {
        mTitleBar = findViewById(R.id.titleBar_actKnowledge);
        webViewOffice = (TBSWebView) findViewById(R.id.webView_office);
        progressViewOffice = (CircularProgressView) findViewById(R.id.progress_view_office);
        openWPSView = LayoutInflater.from(this).inflate(R.layout.layout_open_wps, null, false);
        openWPSView.setClickable(true);
        mTitleBar.setRightView(openWPSView);

        webViewOffice.setOnGetFilePathListener(new TBSWebView.OnGetFilePathListener() {
            @Override
            public void onGetFilePath(TBSWebView view) {
                getFilePathAndShowFile(view, fileUrl, fileName);
            }
        });

        webViewOffice.show();
    }

    @Override
    public void afterInitView() {

    }

    @Override
    public void bindListener() {
        mTitleBar.setListener((v, action, extra) -> {
            if (action == CommonTitleBar.ACTION_LEFT_TEXT || action == CommonTitleBar.ACTION_LEFT_BUTTON) {
                finish();
            }
        });

        openWPSView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFile != null) {
                    showDialog(mFile);
                }
            }
        });
    }

    @Override
    public void onClickEvent(View v) {

    }

    /**
     * 内核加载失败后是否使用wps展示
     *
     * @param file 要展示的文件
     */
    private void showDialog(File file) {
        dialog = new CommonTitleDialog(OfficeViewActivity.this, "文件预览","使用WPS打开文档？", "取消", "确认", new CommonTitleDialog.OnButtonClickListener() {
            @Override
            public void onLeftClick() {
                dialog.dismiss();
                finish();
            }

            @Override
            public void onRightClick() {
                OpenWithWPSUtil.openWithWPS(OfficeViewActivity.this, file, false);
                finish();
            }
        });
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 根据内核状态判断如何打开文件
     *
     * @param file 当前文件
     */
    private void openFile(File file) {
        mFile = file;
        if (BaseApplication.getInstance().isInitX5()) {
            boolean isDisplay = webViewOffice.displayFile(file);
            if (!isDisplay) {
                showDialog(file);
            }
        } else {
            showDialog(file);
        }
    }

    /**
     * 判断文件是否为网络文件
     *
     * @param view TBSWebView
     * @param fileUrl 文件url
     * @param fileName 文件名
     */
    private void getFilePathAndShowFile(TBSWebView view, String fileUrl, String fileName) {
        Log.d(TAG, "getFilePathAndShowFile: " + fileUrl);
        // 网络地址要先下载
        if (fileUrl.contains("http")) {
            downLoadFromNet(fileUrl, fileName);
        } else {
            String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!EasyPermissions.hasPermissions(OfficeViewActivity.this, perms)) {
                EasyPermissions.requestPermissions(OfficeViewActivity.this, "需要访问手机存储权限！", 10086, perms);
            }

            openFile(new File(fileUrl));
        }
    }

    /**
     * 文件预览调用
     *
     * @param context 调用者上下文
     * @param url 文件url
     * @param fileName 文件名
     * @param fileType 文件类型
     */
    public static void show(Context context, String url, String fileName, String fileType) {
        Intent intent = new Intent(context, OfficeViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("fileUrl", url);
        bundle.putString("fileName", fileName);
        bundle.putString("fileType", fileType);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    /**
     * 从网络下载文件或直接调用缓存
     *
     * @param fileUrl 文件url
     * @param fileName 文件名
     */
    private void downLoadFromNet(String fileUrl, String fileName) {
        //1.网络下载、存储路径、
        File cacheFile = getCacheFile(fileName);
        if (cacheFile.exists()) {
            if (cacheFile.length() <= 0) {
                Log.d(TAG, "删除空文件！！");
                cacheFile.delete();
            }
            else {
                openFile(cacheFile);
                return;
            }
        }
        progressViewOffice.setVisibility(View.VISIBLE);
        progressViewOffice.startAnimation();
        presenter.getFile(fileUrl,fileName);
    }

    /***
     * 获取缓存目录
     *
     * @return
     */
    public File getCacheDir() {
        File rootDir = context.getExternalCacheDir();
        Log.d(TAG, "getCacheDir: " + rootDir.toString());
        File cacheDir = new File(rootDir, "office");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        Log.d(TAG, "getCacheDir: " + cacheDir.toString());
        return cacheDir;
    }

    /***
     * 绝对路径获取缓存文件
     *
     * @param fileName 文件名
     * @return File
     */
    private File getCacheFile(String fileName) {
        File cacheFile = new File(getCacheDir(), fileName);
        Log.d(TAG, "缓存文件 = " + cacheFile.toString());
        return cacheFile;
    }

    /***
     * 获取文件类型
     *
     * @param fileName 文件名
     * @return String 文件类型
     */
    private String getFileType(String fileName) {
        String str = "";

        if (TextUtils.isEmpty(fileName)) {
            return str;
        }
        int i = fileName.lastIndexOf('.');
        if (i <= -1) {
            Log.d(TAG,"i <= -1");
            return str;
        }

        str = fileName.substring(i + 1);
        return str;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webViewOffice != null) {
            webViewOffice.onStopDisplay();
        }
    }

    @Override
    public void getFileSuccess(Response<ResponseBody> response, String fileName) {
        Log.d(TAG, "下载文件-->onResponse");
        progressViewOffice.stopAnimation();
        progressViewOffice.setVisibility(View.GONE);
    }

    @Override
    public void getFileFail(String fileName) {
        ToastUtils.show("文件下载失败");
        progressViewOffice.stopAnimation();
        progressViewOffice.setVisibility(View.GONE);
        finish();

    }

    @Override
    public void saveFileSuccess(File file) {
        openFile(file);
    }

    @Override
    public void saveFileFail() {
        ToastUtils.show("文件写入失败");
        File file = getCacheFile(fileName);
        if (!file.exists()) {
            Log.d(TAG, "删除下载失败文件");
            file.delete();
        }
    }
}
