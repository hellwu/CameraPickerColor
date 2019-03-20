package com.hellw.camerapickercolor;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;

public class CameraPreviewUtil {
    private static final String tag = "hellw";
    private CameraSizeComparator sizeComparator = new CameraSizeComparator();
    private static CameraPreviewUtil myCamPara = null;
    private CameraPreviewUtil(){

    }
    public static CameraPreviewUtil getInstance(){
        if(myCamPara == null){
            myCamPara = new CameraPreviewUtil();
            return myCamPara;
        }
        else{
            return myCamPara;
        }
    }

    public  Size getPreviewSize(List<Camera.Size> list, float th, int minWidth){
        Collections.sort(list, sizeComparator);

        int i = 0;
        for(Size s:list){
            if((s.width > minWidth) && equalRate(s, th)){
                Log.i(tag, "最终设置预览尺寸:w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }
        return list.get(i);
    }
    public Size getPictureSize(List<Camera.Size> list, float th, int minWidth){
        Collections.sort(list, sizeComparator);

        int i = 0;
        for(Size s:list){
            if((s.width > minWidth) && equalRate(s, th)){
                Log.i(tag, "最终设置图片尺寸:w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }

        return list.get(i);
    }

    public boolean equalRate(Size s, float rate){
        float r = (float)(s.width)/(float)(s.height);
        if(Math.abs(r - rate) <= 0.2)
        {
            return true;
        }
        else{
            return false;
        }
    }

    public  class CameraSizeComparator implements Comparator<Camera.Size>{
        //按升序排列
        public int compare(Size lhs, Size rhs) {
            // TODO Auto-generated method stub
            if(lhs.width == rhs.width){
                return 0;
            }
            else if(lhs.width > rhs.width){
                return 1;
            }
            else{
                return -1;
            }
        }
    }
}
