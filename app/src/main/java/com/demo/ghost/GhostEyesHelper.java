package com.demo.ghost;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Outline;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.demo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GhostEyesHelper {

    private static String getSDCardPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if(sdCardExist)
        {
            return Environment.getExternalStorageDirectory().toString();//获取跟目录
        }
        return sdDir.toString();
    }

    private static final String IMG_DIR = getSDCardPath() + File.separatorChar + "sunmi" + File.separatorChar;

    static {
        Log.e("SUNMI","Compare Dir: " + IMG_DIR);
    }

    final Context mContext;
    final WindowManager mWindowManager;

    View scan;
    View start;
    View pre;
    View next;
    ImageView mForePicture;
    View mBGView;

    View mGhostView;

    boolean isInScanState = false;
    boolean isStartedState = false;

    private final static int INVALID_INDEX = -1;

    private  final List<String> mPathList = new ArrayList<>();
    private int mPathIndex = INVALID_INDEX;
    private int mNextPathIndex = INVALID_INDEX;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private static GhostEyesHelper sHelper;

    static GhostEyesHelper getGhostInstallHelper(Context appContext){
        if (sHelper == null){
            sHelper = new GhostEyesHelper(appContext);
        }
        return sHelper;
    }


    private GhostEyesHelper(Context appContext){
        mContext = appContext;
        mWindowManager = (WindowManager)appContext.getSystemService(Context.WINDOW_SERVICE);
    }

    private void forceUpdateAction(ViewGroup pGroup){

        ViewOutlineProvider forCycle = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0,0,view.getWidth(), view.getWidth(),view.getWidth() / 2);
                outline.setAlpha(0.4F);
            }
        };
        View actionView;

        int count = pGroup.getChildCount();
        for (int i = 0; i < count; i++) {

            actionView = pGroup.getChildAt(i);

            actionView.setOutlineProvider(forCycle);
            actionView.setClipToOutline(true);

            actionView.setOnClickListener(new GhostClicker());
        }

        scan = pGroup.findViewById(R.id.scan);
        start = pGroup.findViewById(R.id.start);
        pre = pGroup.findViewById(R.id.pre);
        next = pGroup.findViewById(R.id.next);

        pre.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);
    }

    public void installGhost(final Context appContext){

        if (mGhostView != null){
            return;
        }
        View ghost = LayoutInflater.from(appContext).inflate(R.layout.ghost_float,null);
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING
                        | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
                PixelFormat.TRANSLUCENT);
        lp.flags |= WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        lp.gravity = Gravity.TOP | Gravity.RIGHT;

        lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        lp.setTitle("installGhost");
        lp.packageName = appContext.getPackageName();
        ghost.setLayoutParams(lp);
        mWindowManager.addView(ghost, lp);

        mGhostView = ghost;

        forceUpdateAction((ViewGroup) ghost.findViewById(R.id.action));
    }

    public void installBG(final Context appContext){
        if (mBGView == null){
            View bg = LayoutInflater.from(appContext).inflate(R.layout.ghost_float_preview,null);
            Display display = mWindowManager.getDefaultDisplay();
            Point p = new Point();
            display.getRealSize(p);

            final WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                    p.x,
                    p.y,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
            lp.flags |= WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;

            lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
            lp.setTitle("BG");
            lp.packageName = appContext.getPackageName();

            mWindowManager.addView(bg, lp);
            mForePicture = (ImageView) bg.findViewById(R.id.alpha);

            mBGView = bg;
        }

        mWindowManager.removeView(mGhostView);
        mWindowManager.addView(mGhostView, mGhostView.getLayoutParams());
    }

    private void uninstallBG(){
        if (mBGView != null){
            mWindowManager.removeView(mBGView);
            mForePicture.setImageBitmap(null);
            mBGView = null;
        }

        pre.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);
    }

    private void scanAllFiles(File file){
        if (!file.exists())return;

        if (!file.isDirectory()) {
            if (isImage(file.getName())){
                mPathList.add(file.getAbsolutePath());
            }
            return;
        }

        File[] list = file.listFiles();
        if (list != null){
            for (File f : list){
                scanAllFiles(f);
            }
        }
    }

    private void scanFile(){
        isInScanState = true;
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(IMG_DIR));
        intent.setData(uri);
        mContext.sendBroadcast(intent);

        uninstallBG();

        mPathList.clear();
        mPathIndex = INVALID_INDEX;
        mNextPathIndex = INVALID_INDEX;

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {


                mPathList.clear();
                scanAllFiles( new File(IMG_DIR));

                mPathIndex = INVALID_INDEX;
                mNextPathIndex = INVALID_INDEX;

                Toast.makeText(mContext,"********** 扫描结束********* ",Toast.LENGTH_LONG).show();
                isInScanState = false;
            }
        },3000);

        Toast.makeText(mContext,"开始扫描",Toast.LENGTH_SHORT).show();
    }

    private void updateViewState(){
        int c = mPathList.size();

        if (mPathIndex >= c-1 ){
            next.setVisibility(View.INVISIBLE);
        }else{
            next.setVisibility(View.VISIBLE);
        }

        if (mPathIndex <= 0){
            pre.setVisibility(View.INVISIBLE);
        }else{
            pre.setVisibility(View.VISIBLE);
        }
    }

    private void updateForePic(){

        int c = mPathList.size();
        if (mNextPathIndex >= c || mNextPathIndex <0 ){
            mNextPathIndex = INVALID_INDEX;
        }
        if (mNextPathIndex == INVALID_INDEX){
            return;
        }
        String filePath = mPathList.get(mNextPathIndex);
        mPathIndex = mNextPathIndex;

        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeFile(filePath);
        }catch (Throwable throwable){
            Log.e("SUNMI","decodeFile error " + " filePath=" + filePath);
        }

        if (bmp != null){
            Bitmap alphaBitmap = setAlpha(bmp,66);
            mForePicture.setImageBitmap(alphaBitmap);
        }
    }


    private class GhostClicker implements View.OnClickListener{

        @Override
        public void onClick(View view) {

            if (isInScanState){
                Toast.makeText(mContext,"正在扫描...",Toast.LENGTH_LONG).show();
                return;
            }
            switch (view.getId()){

                case R.id.scan:
                    if (isStartedState){
                        isStartedState = false;
                        ((ImageView) start).setImageResource(R.mipmap.start);
                    }
                    scanFile();
                    return;
                case R.id.start:
                    ImageView ig = (ImageView) view;

                    if (isStartedState){
                        isStartedState = false;
                        ig.setImageResource(R.mipmap.start);
                        uninstallBG();
                        return;
                    }
                    if (mPathList.isEmpty()){
                        Toast.makeText(mContext,"没有资源,可以先扫描",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    isStartedState = true;
                    ig.setImageResource(R.mipmap.stop);

                    installBG(mContext);

                    if (mNextPathIndex == INVALID_INDEX){
                        mNextPathIndex = 0;
                    }
                    break;
                case R.id.pre:
                    if (!isStartedState){
                        Toast.makeText(mContext,"没有开始",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mNextPathIndex = mPathIndex -1;
                    break;
                case R.id.next:
                    if (!isStartedState){
                        Toast.makeText(mContext,"没有开始",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mNextPathIndex = mPathIndex + 1;
                    break;
            }

            updateForePic();
            updateViewState();
        }
    }



    public static Bitmap setAlpha(Bitmap sourceImg, int number) {

        int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];

        sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0,sourceImg.getWidth(), sourceImg.getHeight());// 获得图片的ARGB值
        number = number * 255 / 100;

        for (int i = 0; i < argb.length; i++) {
            argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);// 修改最高2位的值
        }
        sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg.getHeight(), Bitmap.Config.ARGB_8888);

        return sourceImg;
    }

    public static boolean isImage(String filePath) {

        int typeIndex = filePath.lastIndexOf(".");

        if (typeIndex <= 0 || typeIndex ==filePath.length() -1){
            return false;
        }

        String type = filePath.substring(typeIndex + 1)
                .toLowerCase();

        if (type != null
                && (type.equals("jpg") || type.equals("gif")
                || type.equals("png") || type.equals("jpeg")
                || type.equals("bmp") || type.equals("wbmp")
                || type.equals("ico") || type.equals("jpe"))) {
            return true;
        }
        return false;
    }

}
