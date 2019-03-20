package com.hellw.camerapickercolor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class CamerPickerColorActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback, View.OnClickListener {
    private SurfaceView mSurfaceView;
    private TextView mTv_color;
    private Button mBt_apply;
    private SurfaceHolder mSurfaceHolder;
    private Camera camera;
    private RoundPickerColorView mColorDisplay;
    private int color;
    private int mCenterX, mCenterY;

    private ImageView mIv_return, mIv_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camer_picker_color);

        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        RelativeLayout rl = findViewById(R.id.header);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(rl.getLayoutParams());
        lp.setMargins(0, Utils.getStateBarHeight(this), 0, 0);
        rl.setLayoutParams(lp);

        mSurfaceView = findViewById(R.id.surfaceview);
        mTv_color =  findViewById(R.id.tv_color);
        mBt_apply =  findViewById(R.id.bt_apply);
        mColorDisplay = findViewById(R.id.view);
        mIv_return = findViewById(R.id.iv_return);
        mIv_img = findViewById(R.id.iv_img);

        mIv_img.setOnClickListener(this);
        mIv_return.setOnClickListener(this);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        formatRGBcolor = getString(R.string.rgb_color_format);
    }

    private void initCamera() {
        Camera.Parameters parameters = camera.getParameters();//获取camera的parameter实例
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();//获取所有支持的camera尺寸
        List<Camera.Size> pictureSizes  = parameters.getSupportedPictureSizes();
        float previewRate = getScreenRate(this);
        float previewRate2 = (1080.0f / 1080.0f);
        Log.i("hellw", "previewRate = "+previewRate);
        Log.i("hellw", "previewRate2 = "+previewRate2);
        //设置PictureSize
        Camera.Size pictureSize = CameraPreviewUtil.getInstance().getPictureSize(pictureSizes,previewRate2, 800);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);
        //设置PreviewSize
        Camera.Size previewSize = CameraPreviewUtil.getInstance().getPreviewSize(previewSizes, previewRate2, 800);
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        mCenterX = previewSize.width / 2;
        mCenterY = previewSize.height / 2;
        if (BuildConfig.DEBUG)
            Log.i("hellw", "camer center point: mCenterX = " + mCenterX + ", mCenterY = " + mCenterY);
        Log.i("hellw", "preview% = "+(previewSize.height / (float)previewSize.width ));
        camera.setParameters(parameters);//把parameters设置给camera
        camera.startPreview();//开始预览
        camera.setDisplayOrientation(90);//将预览旋转90度
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                this.finish();
                break;
            case R.id.iv_img:
                Intent intent = new Intent(this, SelectPointPickerColorActivity.class);
                startActivity(intent);
                this.finish();
                break;
        }
    }
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        size = camera.getParameters().getPreviewSize();
        try {
            image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
            if (image != null) {
                stream = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);
                bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                color = getColor(bmp, mCenterX, mCenterY);
                displayColor(color);
                stream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void displayColor(int color) {
        int red = (color & 0xff0000) >> 16;
        int green = (color & 0x00ff00) >> 8;
        int blue = (color & 0x0000ff);
        colorStr = String.format(formatRGBcolor, red, green, blue);

        if (!TextUtils.isEmpty(colorStr))
            mTv_color.setText(colorStr);
        mColorDisplay.setColor(color);
    }

    private Camera.Size size;
    private YuvImage image;
    private ByteArrayOutputStream stream;
    private Bitmap bmp;
    private String colorStr;
    private String formatRGBcolor;

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
        source.recycle();
        return color;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open();
            camera.setPreviewCallback(this);
            camera.setPreviewDisplay(mSurfaceHolder);
        } catch (Exception e) {
            if (null != camera) {
                camera.release();
                camera = null;
            }
            e.printStackTrace();
            Toast.makeText(CamerPickerColorActivity.this, "启动摄像头失败,请开启摄像头权限", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        initCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (null != camera) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
    public static float getScreenRate(Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float H = dm.heightPixels;
        float W = dm.widthPixels;
        Log.i("hellw", "widthPixels = "+W+", heightPixels = "+H);
        return (H/W);
    }

}
