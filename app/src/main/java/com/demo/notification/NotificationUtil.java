package com.demo.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Timer;
import java.util.TimerTask;

import com.demo.HomeActivity;
import com.demo.R;

/**
 * 显示通知栏工具类
 * Created by Administrator on 2016-11-14.
 */

public class NotificationUtil {

    public static final int ic_launcher = R.mipmap.launcher;
    /**
     * 显示一个普通的通知
     *
     * @param context 上下文
     */
    public static void showNotification(Context context) {
        Notification notification = new NotificationCompat.Builder(context)
                /**设置通知左边的大图标**/
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), ic_launcher))
                /**设置通知右边的小图标**/
                .setSmallIcon(R.mipmap.launcher)
                /**通知首次出现在通知栏，带上升动画效果的**/
                .setTicker("通知来了")
                /**设置通知的标题**/
                .setContentTitle("这是一个通知的标题")
                /**设置通知的内容**/
                .setContentText("这是一个通知的内容这是一个通知的内容")
                /**通知产生的时间，会在通知信息里显示**/
                .setWhen(System.currentTimeMillis())
                /**设置该通知优先级**/
                .setPriority(Notification.PRIORITY_DEFAULT)
                /**设置这个标志当用户单击面板就可以让通知将自动取消**/
                .setAutoCancel(true)
                /**设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)**/
                .setOngoing(false)
                /**向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：**/
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setContentIntent(PendingIntent.getActivity(context, 1, new Intent(context, NotificationSenderActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        /**发起通知**/
        notificationManager.notify(sNotificationId++, notification);
    }

    /**
     * 显示一个下载带进度条的通知
     *
     * @param context 上下文
     */
    public static void showNotificationProgress(Context context) {
        //进度条通知
        final NotificationCompat.Builder builderProgress = new NotificationCompat.Builder(context);
        builderProgress.setContentTitle("下载中");
        builderProgress.setSmallIcon(ic_launcher);
        builderProgress.setTicker("进度条通知");
        builderProgress.setProgress(100, 0, false);
        final Notification notification = builderProgress.build();
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        //发送一个通知
        final int progressId = sNotificationId++;
        notificationManager.notify(progressId, notification);
        /**创建一个计时器,模拟下载进度**/
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int progress = 0;

            @Override
            public void run() {
                Log.i("progress", progress + "");
                while (progress <= 100) {
                    progress++;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    //更新进度条
                    builderProgress.setProgress(100, progress, false);
                    //再次通知
                    notificationManager.notify(progressId, builderProgress.build());
                }
                //计时器退出
                this.cancel();
                //进度条退出
                notificationManager.cancel(progressId);
                return;//结束方法
            }
        }, 0);
    }

    /**
     * 悬挂式，支持6.0以上系统
     *
     * @param context
     */
    public static void showFullScreen(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://blog.csdn.net/itachi85/"));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mIntent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), ic_launcher));
        builder.setAutoCancel(true);
        builder.setContentTitle("悬挂式通知 "+ sNotificationId);
        //设置点击跳转
        Intent hangIntent = new Intent();
        hangIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        hangIntent.setClass(context, NotificationSenderActivity.class);
        //如果描述的PendingIntent已经存在，则在产生新的Intent之前会先取消掉当前的
        PendingIntent hangPendingIntent = PendingIntent.getActivity(context, 0, hangIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setFullScreenIntent(hangPendingIntent, true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(sNotificationId++, builder.build());
    }
    static int sNotificationId = 0;
    /**
     * 折叠式
     *
     * @param context
     */
    public static void shwoNotify(Context context) {
        //先设定RemoteViews
        RemoteViews view_custom = new RemoteViews(context.getPackageName(), R.layout.notification_custom_layout);
        //设置对应IMAGEVIEW的ID的资源图片
        view_custom.setImageViewResource(R.id.custom_icon, R.mipmap.notification_custom_icon);
        view_custom.setTextViewText(R.id.tv_custom_title, "今日头条");
        view_custom.setTextColor(R.id.tv_custom_title, Color.BLACK);
        view_custom.setTextViewText(R.id.tv_custom_content, "金州勇士官方宣布球队已经解雇了主帅马克-杰克逊，随后宣布了最后的结果。");
        view_custom.setTextColor(R.id.tv_custom_content, Color.BLACK);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContent(view_custom)
                .setContentIntent(PendingIntent.getActivity(context, 4, new Intent(context, NotificationSenderActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setTicker("有新资讯")
                .setPriority(Notification.PRIORITY_HIGH)// 设置该通知优先级
                .setOngoing(false)//不是正在进行的   true为正在进行  效果和.flag一样
                .setSmallIcon(R.mipmap.notification_custom_icon);
        Notification notify = mBuilder.build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(sNotificationId++, notify);
    }


    public static void showBigPictureStyle(Context context){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("BigPictureStyle");
        builder.setContentText("BigPicture演示示例");
        builder.setSmallIcon(ic_launcher);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),ic_launcher));
        android.support.v4.app.NotificationCompat.BigPictureStyle style = new android.support.v4.app.NotificationCompat.BigPictureStyle();
        style.setBigContentTitle("BigContentTitle");
        style.setSummaryText("SummaryText");
        style.bigPicture(BitmapFactory.decodeResource(context.getResources(),R.mipmap.wallpaper1));
        builder.setStyle(style);
        builder.setAutoCancel(false);
        Intent intent = new Intent(context,HomeActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context,1,intent,0);
        //设置点击大图后跳转
        builder.setContentIntent(pIntent);
        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(/*Integer.MAX_VALUE - 1*/sNotificationId++, notification);
    }

}
