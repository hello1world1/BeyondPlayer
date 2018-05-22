package com.iwanghang.drmplayer;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
//import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.ActivityCollector;

/**
 * Created by iwanghang on 16/4/19.
 * MainActivity继承BaseActivity,实现APP所有绑定功能
 * 继承后,直接调用子类方法,就可以进行绑定和解除,bindPlayService,unbindPlayService
 *
 * 模板设计模式,给FragmentActivity做了一个抽象,用来绑定服务
 */
public abstract class BaseActivity extends FragmentActivity {

    protected PlayService playService;
    private ForceOfflineReceiver receiver;

    private boolean isBound = false;//是否已经绑定

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.broadcastbestpractice.FORCE_OFFLINE");
        receiver=new ForceOfflineReceiver();
        registerReceiver(receiver,intentFilter);
    }

    //绑定Service
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {//服务连接
            //转换
            PlayService.PlayBinder playBinder = (PlayService.PlayBinder) service;
            //绑定播放服务
            playService = playBinder.getPlayService();
            //设置监听器
            playService.setMusicUpdatrListener(musicUpdatrListener);
            //真正调用的是PlayActivity里面change()
            musicUpdatrListener.onChange(playService.getCurrentPosition());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {//服务断开
            playService = null;
            isBound = false;
        }
    };

    //实现MusicUpdatrListener的相关方法,把PlayService.MusicUpdatrListener的具体内容,
    // 延迟到子类来具体实现(把具体的操作步骤在子类中实现)
    private PlayService.MusicUpdatrListener musicUpdatrListener = new PlayService.MusicUpdatrListener() {
        @Override
        public void onPublish(int progress) {
            publish(progress);
        }

        @Override
        public void onChange(int progress) {
            change(progress);
        }
    };
    //抽象类(子类来具体实现,用于更新UI)
    public abstract void publish(int progress);
    public abstract void change(int progress);



    //绑定服务(子类决定什么时候调用,比如在onCreate时调用,或者在onResume,onPause时调用)
    public void bindPlayService(){
        if(!isBound) {
            Intent intent = new Intent(this, PlayService.class);
            bindService(intent, conn, Context.BIND_AUTO_CREATE);
            isBound = true;
        }
    }
    //解绑服务(子类决定什么时候调用,比如在onCreate时调用,或者在onResume,onPause时调用)
    public void unbindPlayService(){
        if(isBound) {
            unbindService(conn);
            isBound = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(receiver!=null){
            unregisterReceiver(receiver);
            receiver=null;

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    //点击事件
    public void onClick(View v){

    }
    class ForceOfflineReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            AlertDialog.Builder builder=new AlertDialog.Builder(context);
            builder.setTitle("warnning");
            builder.setMessage("You are force to be offline.Please try to login again");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCollector.finishAll();//销毁所有活动
                    Intent intent=new Intent(context,LoginActivity.class);
                    context.startActivity(intent);
                }
            });
            builder.show();
        }
    }

}
