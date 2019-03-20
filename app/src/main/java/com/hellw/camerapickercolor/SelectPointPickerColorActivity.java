package com.hellw.camerapickercolor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import java.io.ByteArrayOutputStream;


public class SelectPointPickerColorActivity extends AppCompatActivity implements PickerPointView.PickdrPointMoveListener, View.OnClickListener {
    private static final int REQUEST_CODE_GALLERY = 0x10;// 图库选取图片标识请求码
    private static final int STORAGE_PERMISSION = 0x20;// 动态申请存储权限标识

    private TextView mTv_color;
    private Button mBt_apply;
    private View mColorDisplay;
    private int color;

    private ByteArrayOutputStream stream;
    private String colorStr;
    private String formatRGBcolor;
    private ImageView mSrcImage;
    private PickerPointView mPickerPointView;
    private Bitmap mSrcBitmap;
    private Drawable drawable;
    private ImageView mIv_return, mIv_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p);
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        RelativeLayout rl = findViewById(R.id.header);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(rl.getLayoutParams());
        lp.setMargins(0, Utils.getStateBarHeight(this), 0, 0);
        rl.setLayoutParams(lp);


        mTv_color = findViewById(R.id.tv_color);
        mBt_apply = findViewById(R.id.bt_apply);
        mColorDisplay = findViewById(R.id.view);
        mSrcImage =  findViewById(R.id.iv_image);
        mPickerPointView = findViewById(R.id.pp_view);
        mIv_return = findViewById(R.id.iv_return);
        mIv_img = findViewById(R.id.iv_img);

        mIv_img.setOnClickListener(this);
        mIv_return.setOnClickListener(this);
        formatRGBcolor = getString(R.string.rgb_color_format);
        mPickerPointView.setPickdrPointMoveListener(this);
        int dp = Utils.px2dip(this, 144);
        Log.i("hellw", "dp = "+dp);

        mSrcBitmap = ((BitmapDrawable) mSrcImage.getDrawable()).getBitmap();
    }

    @Override
    public void onClick(View v) {
      switch (v.getId()) {
          case R.id.iv_return:
              this.finish();
              break;
          case R.id.iv_img:
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                  requestStoragePermission();
              }
              else {
                  gallery();
              }
//              Intent intent = new Intent(this, CamerPickerColorActivity.class);
//              startActivity(intent);
//              this.finish();
              break;
      }
    }

    @Override
    public void move(int x, int y) {
        Log.i("hellw", "x = "+x+", y = "+y);
        color = getColor(mSrcBitmap, x, y);
        displayColor(color);
        mPickerPointView.setColor(false, color);
    }

    private void displayColor(int color) {
        int red = (color & 0xff0000) >> 16;
        int green = (color & 0x00ff00) >> 8;
        int blue = (color & 0x0000ff);
        colorStr = String.format(formatRGBcolor, red, green, blue);

        if (!TextUtils.isEmpty(colorStr))
            mTv_color.setText(colorStr);
        if (mColorDisplay != null)
            mColorDisplay.setBackgroundColor(color);
    }

    /**
     * 获得像素点颜色
     *
     * @param source
     * @param intX
     * @param intY
     * @return
     */
    private int getColor(Bitmap source, int intX, int intY) {
        // 为了防止越界
        int color;
        if (intX < 0) intX = 0;
        if (intY < 0) intY = 0;
        if (intX >= source.getWidth()) {
            intX = source.getWidth() - 1;
        }
        if (intY >= source.getHeight()) {
            intY = source.getHeight() - 1;
        }

        color = source.getPixel(intX, intY);
        Log.i("hellw", "leftColor: " + color);
        return color;
    }

    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * 图库选择图片
     */
    private void gallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // 以startActivityForResult的方式启动一个activity用来获取返回的结果
        startActivityForResult(intent, REQUEST_CODE_GALLERY);

    }

    /**
     * 接收#startActivityForResult(Intent, int)调用的结果
     * @param requestCode 请求码 识别这个结果来自谁
     * @param resultCode    结果码
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){// 操作成功了
            switch (requestCode){
                case REQUEST_CODE_GALLERY:// 图库选择图片
                    Uri uri = data.getData();// 获取图片的uri
                    displayImage(uri);
                    break;
            }

        }
    }

    /**
     * Android6.0后需要动态申请危险权限
     * 动态申请存储权限
     */
    private void requestStoragePermission() {
        int hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.e("TAG","开始" + hasCameraPermission);
        if (hasCameraPermission == PackageManager.PERMISSION_GRANTED){
            // 拥有权限，可以执行涉及到存储权限的操作
            Log.e("TAG", "你已经授权了该组权限");
            gallery();
        }else {
            // 没有权限，向用户申请该权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.e("TAG", "向用户申请该组权限");
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
            }
        }

    }

    /**
     * 动态申请权限的结果回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // 用户同意，执行相应操作
                Log.e("TAG","用户已经同意了存储权限");
                gallery();
            }else {
                // 用户不同意，向用户展示该权限作用
            }
        }

    }


    /**
     * 显示图片
     * @param imageUri 图片的uri
     */
    private void displayImage(Uri imageUri) {
        try{
            // glide根据图片的uri加载图片
            Glide.with(this)
                    .load(imageUri)
                    .asBitmap()   //强制转换Bitmap
                    .into(new SimpleTarget<Bitmap>() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                            mSrcImage.setImageBitmap(bitmap );
                            mSrcBitmap = ((BitmapDrawable) mSrcImage.getDrawable()).getBitmap();
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
