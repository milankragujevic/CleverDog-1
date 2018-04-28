package com.soowin.cleverdog.activity.index;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.soowin.cleverdog.R;
import com.soowin.cleverdog.activity.login.EditPassWordActivity;
import com.soowin.cleverdog.activity.login.LoginActivity;
import com.soowin.cleverdog.activity.welcome.WelcomeActivity;
import com.soowin.cleverdog.http.HttpTool;
import com.soowin.cleverdog.info.index.EditAvatarBean;
import com.soowin.cleverdog.info.login.GetPersonalDataBean;
import com.soowin.cleverdog.service.ComityService;
import com.soowin.cleverdog.service.DownloadService;
import com.soowin.cleverdog.utlis.BaseActivity;
import com.soowin.cleverdog.utlis.PublicApplication;
import com.soowin.cleverdog.utlis.ScreenManager;
import com.soowin.cleverdog.utlis.Utlis;
import com.soowin.cleverdog.utlis.permissions.PermissionsActivity;
import com.soowin.cleverdog.utlis.permissions.PermissionsChecker;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * 我的页面
 */
public class MeActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = BaseActivity.class.getSimpleName();
    private static final int PHOTOZOOM_CODE = 1003;
    private final int FILESELECT_CODE = 1001;
    private final int CAMERA_CODE = 1002;

    //拍照
    private Uri mUri;
    private String mPicturePath = "";

    private ImageView ivAvatar;
    private TextView tvNice;

    private TextView tvTitle;
    private ImageView ivBack;

    private LinearLayout llMyInfo;
    private LinearLayout llEditPassword;
    private LinearLayout llUpdateDatabase;
    private TextView tvOutLogin;
    private TextView tvVersion;
    //技术支持
    private TextView tvCome;

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
                        GetPersonalDataBean dataBean = new GetPersonalDataBean();
                        dataBean = gson.fromJson(msg.obj.toString(),
                                GetPersonalDataBean.class);
                        int state = dataBean.getState();
                        switch (state) {
                            case 1:
                                tvNice.setText(dataBean.getResult().getUser_nicename());
                                break;
                            default:
                                showToast(dataBean.getMessage());
                                break;
                        }
                    } else if (msg.what == 2) {
                        dismissDialog();
                        Gson gson = new Gson();
                        EditAvatarBean dataBean = new EditAvatarBean();
                        dataBean = gson.fromJson(msg.obj.toString(),
                                EditAvatarBean.class);
                        int state = dataBean.getState();
                        switch (state) {
                            case 1:
                                String avatar = dataBean.getResult().get(0).getUrl();
                                PublicApplication.loginInfo.edit().putString("avatar",
                                        avatar);
                                updateAvatar(avatar);
                                break;
                            default:
                                Toast.makeText(MeActivity.this, dataBean.getMessage(), Toast.LENGTH_SHORT).show();
                                break;
                        }
                    } else if (msg.what == 3) {
                        dismissDialog();
                        Glide.with(MeActivity.this)
                                .load(PublicApplication.loginInfo.getString("avatar", ""))
                                .asBitmap()
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .error(R.drawable.img_avatar_null)
                                .placeholder(R.drawable.img_avatar_null)
                                .centerCrop()
                                .into(new BitmapImageViewTarget(ivAvatar) {
                                    @Override
                                    protected void setResource(Bitmap resource) {
                                        RoundedBitmapDrawable circularBitmapDrawable =
                                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                                        circularBitmapDrawable.setCircular(true);
                                        ivAvatar.setImageDrawable(circularBitmapDrawable);
                                    }
                                });
//                        getInitData(PublicApplication.loginInfo.getString("id", ""));
                    }
                }
            } catch (Exception e) {
                Log.e("DingDanLBActivity", e.toString());
            }
        }
    };
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        ScreenManager.getScreenManager().pushActivity(this);//加入栈

        mPermissionsChecker = new PermissionsChecker(this);// 权限检测器

        initTitle();
        initView();
//        initCeshi();
    }

    /**
     * 参数测试部分 用后可删除
     */
    /*EditText etYanchi;
    EditText etGengxin;
    EditText etJuli;
    EditText etSudu;
    TextView tvXiugai;

    private void initCeshi() {
        etYanchi = findViewById(R.id.et_yanchi);
        etGengxin = findViewById(R.id.et_gengxin);
        etJuli = findViewById(R.id.et_juli);
        etSudu = findViewById(R.id.et_sudu);
        tvXiugai = findViewById(R.id.tv_xiugai);

        etYanchi.setText(PublicApplication.mVariableTime + "");
        etGengxin.setText(PublicApplication.mapTime + "");
        etJuli.setText(PublicApplication.longVariable + "");

        tvXiugai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublicApplication.mVariableTime = Double.parseDouble(etYanchi.getText().toString());
                PublicApplication.mapTime = Double.parseDouble(etGengxin.getText().toString());
                PublicApplication.longVariable = Double.parseDouble(etJuli.getText().toString());
            }
        });
    }*/
    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(this)
                .load(PublicApplication.loginInfo.getString("avatar", ""))
                .asBitmap()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.img_avatar_null)
                .placeholder(R.drawable.img_avatar_null)
                .centerCrop()
                .into(new BitmapImageViewTarget(ivAvatar) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        ivAvatar.setImageDrawable(circularBitmapDrawable);
                    }
                });
        tvNice.setText(PublicApplication.loginInfo.getString("user_nicename", ""));
        initData();
    }

    /**
     * 查询个人资料
     */
    private void initData() {
        showDialog();
        new Thread() {
            @Override
            public void run() {
               /* HttpTool httpTool = new HttpTool();
                String data = "&user_id=" + PublicApplication.loginInfo.getString("id", "");
                String result = httpTool.httpPost(
                        PublicApplication.urlData.getPersonalData,
                        data);*/
                RequestBody body = new FormBody.Builder()
                        .add("json", PublicApplication.urlData.getPersonalData)
                        .add("user_id", PublicApplication.loginInfo.getString("id", ""))
                        .build();
                String result = HttpTool.okPost(body);
                Log.e("查询个人资料==..result.=", result + "");
                Message msg = handle.obtainMessage();
                msg.obj = result;
                msg.what = 1;
                handle.sendMessage(msg);
            }
        }.start();
    }

    private void initTitle() {
        tvTitle = findViewById(R.id.tv_title);
        ivBack = findViewById(R.id.iv_back);

        tvTitle.setText("我的");
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(this);
    }

    private void initView() {
        tvCome = findViewById(R.id.tv_come);
        ivAvatar = findViewById(R.id.iv_avatar);
        llMyInfo = findViewById(R.id.ll_my_info);
        llEditPassword = findViewById(R.id.ll_edit_password);
        llUpdateDatabase = findViewById(R.id.ll_update_database);
        tvOutLogin = findViewById(R.id.tv_out_login);
        tvNice = findViewById(R.id.tv_nice);
        tvVersion = findViewById(R.id.tv_version);

        ivAvatar.setOnClickListener(this);
        llMyInfo.setOnClickListener(this);
        llEditPassword.setOnClickListener(this);
        llUpdateDatabase.setOnClickListener(this);
        tvOutLogin.setOnClickListener(this);
        tvCome.setOnClickListener(this);

        int myV = PublicApplication.db.getVersion();
        int webV = Integer.parseInt(PublicApplication.loginInfo.getString("versionnumber", ""));
        if (myV != webV)
            tvVersion.setVisibility(View.VISIBLE);
        else
            tvVersion.setVisibility(View.GONE);

        Glide.with(MeActivity.this)
                .load(PublicApplication.loginInfo.getString("avatar", ""))
                .asBitmap()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.img_avatar_null)
                .placeholder(R.drawable.img_avatar_null)
                .centerCrop()
                .into(new BitmapImageViewTarget(ivAvatar) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        ivAvatar.setImageDrawable(circularBitmapDrawable);
                    }
                });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_avatar:
                mShowDialog();
                break;
            case R.id.tv_come:
                Intent comeintent = new Intent(this, WebViewActivity.class);
                comeintent.putExtra(WebViewActivity.URLS, "http://www.yanzhaoit.cn/");
                comeintent.putExtra(WebViewActivity.TITLE, "燕赵互联");
                startActivity(comeintent);
                break;
            case R.id.ll_my_info:
                Intent intent = new Intent(this, MyInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_edit_password:
                startActivity(new Intent(this, EditPassWordActivity.class));
                break;
            case R.id.ll_update_database:
                showDialog();
                Intent mIntent = new Intent();
                mIntent.setAction("com.soowin.cleverdog.service.DownloadService");//你定义的service的action
                mIntent.setPackage(getPackageName());//这里你需要设置你应用的包名
                startService(mIntent);
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        dismissDialog();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast("更新完毕");
                                tvVersion.setVisibility(View.GONE);
                            }
                        });
                    }
                }, 3000);
                break;
            case R.id.tv_out_login:
                PublicApplication.loginInfo.edit().putString("user_login",
                        "").apply();
                PublicApplication.loginInfo.edit().putString("id",
                        "").apply();
                PublicApplication.loginInfo.edit().putString("user_nicename",
                        "").apply();
                PublicApplication.loginInfo.edit().putString("user_email",
                        "").apply();
                PublicApplication.loginInfo.edit().putString("display_name",
                        "").apply();
                PublicApplication.loginInfo.edit().putString("phone",
                        "").apply();
                PublicApplication.loginInfo.edit().putString("token",
                        "").apply();
                PublicApplication.loginInfo.edit().putString("user_url",
                        "").apply();
                PublicApplication.loginInfo.edit().putString("avatar",
                        "").apply();

                Intent stopIntent = new Intent();
                stopIntent.setAction("com.soowin.cleverdog.service.ComityService");//你定义的service的action
                stopIntent.setPackage(getPackageName());//这里你需要设置你应用的包名
                stopService(stopIntent);

                PublicApplication.mVoiceManage.delVoiceAll();

                startActivity(new Intent(MeActivity.this, LoginActivity.class));
                ScreenManager.getScreenManager().popAllActivityExceptOne(WelcomeActivity.class);
                finish();
                break;
        }
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
                        photoZoom();
                    } else
                        startPhotoZoom(mUri);
                break;
            case PHOTOZOOM_CODE:
                Glide.with(this)
                        .load(PublicApplication.pathAvatar)
                        .asBitmap()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .error(R.drawable.img_avatar_null)
                        .placeholder(R.drawable.img_avatar_null)
                        .centerCrop()
                        .into(new BitmapImageViewTarget(ivAvatar) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                ivAvatar.setImageDrawable(circularBitmapDrawable);
                            }
                        });
                updateAvatar();//上传头像
                break;
            case FILESELECT_CODE:
                if (data != null) {
                    if (resultCode == -1) {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            Log.e("onActivityResult: ", data.toString());
                            Uri uri = data.getData();
                            photoZoom(uri);
                        } else {
                            Uri uri = data.getData();
                            startPhotoZoom(uri);
                        }
                    }
                }
                break;
        }
    }

    /**
     * 上传头像
     */
    private void updateAvatar() {
        showDialog();
        Thread thread = new Thread() {
            @Override
            public void run() {
                File path = new File(PublicApplication.pathAvatar);
                HttpTool httpTool = new HttpTool();
                String result = httpTool.uploadFile(
                        "avatar", path,
                        PublicApplication.urlData.hostUrl + "?json=uploadimg/uploadImg"
                );
                Log.e("上传头像==..result.=", result + "");
                Message msg = handle.obtainMessage();
                msg.obj = result;
                msg.what = 2;
                handle.sendMessage(msg);
            }
        };
        thread.start();

    }

    private void updateAvatar(final String url) {
        showDialog();
        Thread thread = new Thread() {
            @Override
            public void run() {
                RequestBody body = new FormBody.Builder()
                        .add("json", PublicApplication.urlData.EditAvatar_user)
                        .add("url", url)
                        .add("user_id", PublicApplication.loginInfo.getString("id", ""))
                        .build();
                String result = HttpTool.okPost(body);
                Log.e("上传头像22==..result.=", result + "");
                Message msg = handle.obtainMessage();
                msg.obj = result;
                msg.what = 3;
                handle.sendMessage(msg);
            }
        };
        thread.start();
    }

    /**
     * 7.0裁剪图片
     */
    private void photoZoom() {
        File file = new File(PublicApplication.pathAvatar);
        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        Uri outputUri = Uri.fromFile(file);
        Uri imageUri = FileProvider.getUriForFile(this, "com.soowin.cleverdog.myprovider", new File(PublicApplication.pathAvatar));//通过FileProvider创建一个content类型的Uri
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, PHOTOZOOM_CODE);
    }

    /**
     * 7.0裁剪图片
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void photoZoom(Uri uri) {
        Uri outputUri = Uri.fromFile(new File(PublicApplication.pathAvatar));

        String inputImg = Utlis.getPath(this, uri);

        Uri imageUri = FileProvider.getUriForFile(this,
                "com.soowin.cleverdog.myprovider", new File(inputImg));//通过FileProvider创建一个content类型的Uri
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, PHOTOZOOM_CODE);
    }

    /**
     * 跳转到剪切界面
     **/
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        // 输出地址
        File externalDir = Environment.getExternalStorageDirectory();
        File path = new File(externalDir.getAbsolutePath() + "/CleverDog/Images");
        if (!path.exists()) { // 判断目录是否存在
            if (path.mkdirs()) {
                Log.e("dbMethond", "mkdirs-->true");
            }// 创建目录
        }
        mPicturePath = path + "/heard.jpg";
        intent.putExtra("output", Uri.fromFile(new File(mPicturePath)));
        intent.putExtra("outputFormat", "JPEG");// 返回格式
        startActivityForResult(intent, PHOTOZOOM_CODE);
    }
}
