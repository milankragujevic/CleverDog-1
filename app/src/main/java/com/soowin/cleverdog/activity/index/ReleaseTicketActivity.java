package com.soowin.cleverdog.activity.index;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.soowin.cleverdog.R;
import com.soowin.cleverdog.http.HttpTool;
import com.soowin.cleverdog.info.index.BaseBean;
import com.soowin.cleverdog.info.index.TicketListBean;
import com.soowin.cleverdog.info.index.UploadImgBean;
import com.soowin.cleverdog.utlis.BaseActivity;
import com.soowin.cleverdog.utlis.PublicApplication;
import com.soowin.cleverdog.utlis.ScreenManager;
import com.soowin.cleverdog.utlis.StrUtils;
import com.soowin.cleverdog.utlis.Utlis;
import com.soowin.cleverdog.utlis.permissions.PermissionsActivity;
import com.soowin.cleverdog.utlis.permissions.PermissionsChecker;

import java.io.File;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * 发布罚单
 */
public class ReleaseTicketActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = ReleaseTicketActivity.class.getSimpleName();
    private final int FILESELECT_CODE = 1001;
    private final int CAMERA_CODE = 1002;
    //拍照
    private Uri mUri;
    private String mPicturePath = "";
    private String imgUrl = "";

    private TextView tvTitle;
    private ImageView ivBack;
    private TextView tvOk;

    private EditText etContent;
    private ImageView ivContent;

    /**
     * 权限部分
     */
    private static final int REQUEST_CODE = 0; // 请求码
    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private PermissionsChecker mPermissionsChecker; // 权限检测器

    Handler handle = new Handler() {
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                if (!TextUtils.isEmpty(msg.obj.toString())) {
                    if (msg.what == 1) {
                        dismissDialog();
                        Gson gson = new Gson();
                        BaseBean dataBean = new BaseBean();
                        dataBean = gson.fromJson(msg.obj.toString(),
                                BaseBean.class);
                        int state = dataBean.getState();
                        switch (state) {
                            case 1:
                                showToast("上传成功！");
                                finish();
                                break;
                            default:
                                showToast(dataBean.getMessage());
                                break;
                        }
                    } else if (msg.what == 2) {
                        Gson gson = new Gson();
                        UploadImgBean dataBean = new UploadImgBean();
                        dataBean = gson.fromJson(msg.obj.toString(),
                                UploadImgBean.class);
                        int state = dataBean.getState();
                        switch (state) {
                            case 1:
                                if (dataBean.getResult() != null)
                                    if (dataBean.getResult().size() > 0)
                                        imgUrl = dataBean.getResult().get(0).getUrl();
                                break;
                            default:
                                showToast(dataBean.getMessage());
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_ticket);
        ScreenManager.getScreenManager().pushActivity(this);//加入栈
        mPermissionsChecker = new PermissionsChecker(this);// 权限检测器

        initTitle();
        initView();
    }

    private void initView() {
        etContent = findViewById(R.id.et_content);
        ivContent = findViewById(R.id.iv_content);

        ivContent.setOnClickListener(this);
    }

    private void initTitle() {
        tvTitle = findViewById(R.id.tv_title);
        ivBack = findViewById(R.id.iv_close);
        tvOk = findViewById(R.id.tv_ok);

        tvTitle.setText("上传罚单");
        tvOk.setText("完成");
        tvOk.setTextColor(Color.WHITE);

        ivBack.setVisibility(View.VISIBLE);
        tvOk.setVisibility(View.VISIBLE);

        ivBack.setOnClickListener(this);
        tvOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                finish();
                break;
            case R.id.tv_ok:
                submit();
                break;
            case R.id.iv_content:
                mShowDialog();
                break;
        }
    }

    /**
     * 提交罚单
     */
    private void submit() {
        String content = etContent.getText().toString().trim();
        if (StrUtils.isEmpty(content))
            showToast("内容不可以为空!");
        if (StrUtils.isEmpty(imgUrl))
            showToast("照片不能为空!");
        else
            updataInfo(content);
    }

    /**
     * 上传罚单
     *
     * @param content
     */
    private void updataInfo(final String content) {
        showDialog();
        new Thread() {
            @Override
            public void run() {
                /*HttpTool httpTool = new HttpTool();
                String data = "&thumb=" + imgUrl
                        + "&content=" + Utlis.toUtf8(content)
                        + "&user_id=" + PublicApplication.loginInfo.getString("id", "");
                String result = httpTool.httpPost(
                        PublicApplication.urlData.getTicket,
                        data);*/
                RequestBody body = new FormBody.Builder()
                        .add("json", PublicApplication.urlData.getTicket)
                        .add("thumb", imgUrl)
                        .add("content", content)
                        .add("user_id", PublicApplication.loginInfo.getString("id", ""))
                        .build();
                String result = HttpTool.okPost(body);
                Log.e("上传罚单==..result.=", result + "");
                Message msg = handle.obtainMessage();
                msg.obj = result;
                msg.what = 1;
                handle.sendMessage(msg);
            }
        }.start();
    }

    private void mShowDialog() {
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {// 权限检测器
            PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
        } else {
            String[] choice = {"选取照片", "拍摄照片"};
            new AlertDialog.Builder(this).setTitle("操作")
                    .setItems(choice, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            if (which == 0) {
                                Intent intent;
                                if (Build.VERSION.SDK_INT < 19) {
                                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.setType("image/*");
                                } else {
                                    intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                }
                                startActivityForResult(intent, FILESELECT_CODE);
                            } else {
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                                    onClickCamera();
                                } else {
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    File file = new File(Environment.getExternalStorageDirectory() + "/Images");
                                    if (!file.exists()) {
                                        file.mkdirs();
                                    }
                                    mUri = Uri.fromFile(
                                            new File(Environment.getExternalStorageDirectory() + "/Images/",
                                                    "cameraImg" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                                    intent.putExtra("return-data", true);
                                    startActivityForResult(intent, CAMERA_CODE);
                                }
                            }
                        }
                    }).show();
        }
    }

    /**
     * 7。0拍照
     */
    public void onClickCamera() {
        File externalDir = Environment.getExternalStorageDirectory();
        //头像地址
        File path = new File(externalDir.getAbsolutePath() + "/CleverDog/Images");
        if (!path.exists()) { // 判断目录是否存在
            if (path.mkdirs()) {
                Log.e("dbMethond", "mkdirs-->true");
            }// 创建目录
        }
        PublicApplication.pathAvatar = path + "/heard.jpg";

        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {

            File mCurrentPhotoFile = new File(PublicApplication.pathAvatar);
            Intent intentC = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri imageUri = FileProvider.getUriForFile(this, "com.soowin.cleverdog.myprovider", mCurrentPhotoFile);
                intentC.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intentC.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            } else {
                intentC.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
            }
            startActivityForResult(intentC, CAMERA_CODE);
        } else {
            Toast.makeText(this, "没有SD卡", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_CODE:
                if (resultCode == -1)
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        upImg();
                    } else
                        upImg(mUri);
                break;
            case FILESELECT_CODE:
                if (data != null) {
                    if (resultCode == -1) {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            Log.e("onActivityResult: ", data.toString());
                            Uri uri = data.getData();
                            upImg(uri);
                        } else {
                            Uri uri = data.getData();
                            upImg(uri);
                        }
                    }
                }
                break;
        }
    }

    /**
     * 上传图片
     */
    private void upImg() {
        Glide.with(this)
                .load(PublicApplication.pathAvatar)
                .error(R.drawable.img_avatar_null)
                .placeholder(R.drawable.img_avatar_null)
                .centerCrop()
                .into(ivContent);
        showDialog();
        new Thread() {
            @Override
            public void run() {
                File path = new File(PublicApplication.pathAvatar);
                HttpTool httpTool = new HttpTool();
                String result = httpTool.uploadFile(
                        "avatar", path,
                        PublicApplication.urlData.hostUrl + "?json=uploadimg/uploadImg"
                );
                Log.e("上传图片==..result.=", result + "");
                Message msg = handle.obtainMessage();
                msg.obj = result;
                msg.what = 2;
                handle.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 上传图片
     */
    private void upImg(Uri mUri) {
        String inputImg = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            inputImg = Utlis.getPath(this, mUri);
        } else
            inputImg = mUri.toString();

        final Uri imageUri = FileProvider.getUriForFile(this,
                "com.soowin.cleverdog.myprovider", new File(inputImg));//通过FileProvider创建一个content类型的Uri
        Glide.with(this)
                .load(inputImg)
                .error(R.drawable.img_avatar_null)
                .placeholder(R.drawable.img_avatar_null)
                .centerCrop()
                .into(ivContent);
        final String finalInputImg = inputImg;
        new Thread() {
            @Override
            public void run() {
                File path = new File(finalInputImg);
                HttpTool httpTool = new HttpTool();
                String result = httpTool.uploadFile(
                        "avatar", path,
                        PublicApplication.urlData.hostUrl + "?json=uploadimg/uploadImg"
                );
                Log.e("上传图片==..result.=", result + "");
                Message msg = handle.obtainMessage();
                msg.obj = result;
                msg.what = 2;
                handle.sendMessage(msg);
            }
        }.start();
    }

}
