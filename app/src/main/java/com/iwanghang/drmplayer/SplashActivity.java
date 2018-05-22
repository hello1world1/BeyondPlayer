package com.iwanghang.drmplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */

//public class SplashActivity extends AppCompatActivity {
public class SplashActivity extends Activity {


    private static final int START_ACTIVITY = 0x1;
    private boolean InMainActivity = false;//布尔值标记是否已经进入MainActivity



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //在SplashActivity中：
        //隐藏标题栏即应用程序的名字
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏：电池状况，信号等
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置显示View对象；
        setContentView(R.layout.activity_splash);
        //一般情况下我只使用下面2行代码
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setContentView(R.layout.activity_splash);

//        mVisible = true;
//        mControlsView = findViewById(R.id.fullscreen_content_controls);
//        mContentView = findViewById(R.id.fullscreen_content);


//        Set up the user interaction to manually show or hide the system UI.
//        mContentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toggle();
//            }
//        });

//         Upon interacting with UI controls, delay any scheduled hide()
//         operations to prevent the jarring behavior of controls going away
//         while interacting with the UI.

//        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
//        findViewById(R.id.InMainActivity_button).setOnTouchListener(mDelayHideTouchListener);


//        //字体设置
//        TextView tv = (TextView)findViewById(R.id.fullscreen_content);
//        //AssetManager mgr = getAssets();//得到AssetManager
//        //Typeface tf = Typeface.createFromAsset(mgr, "fonts/mini.TTF");//根据路径得到Typeface
//        //tv.setTypeface(tf);//设置字体
//        String textStr1 = "<font color=\"#ffff00\">欢迎来到，</font><br>";
//        String textStr2 = "<font color=\"#00ff00\">音乐之声，</font><br>";
//        String textStr3 = "<font color=\"#ff00ff\">乘着梦想，</font><br>";
//        String textStr4 = "<font color=\"#00ffff\">飞向那个音符<br>的<br>海洋……</font><br>";
//        tv.setText(Html.fromHtml(textStr1+textStr2+textStr3+textStr4));
//        //在xml设置阴影
//        //android:shadowColor="#000000"
//        //android:shadowDx="15.0"
//        //android:shadowDy="5.0"
//        //android:shadowRadius=“5.0"



        //通过button进入MainActivity
        Button button = (Button) findViewById(R.id.InMainActivity_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InMainActivity = true;
                startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                finish();
            }
        });

        //在欢迎界面启动服务
        //先将服务器启动起来,然后进行绑定和解除绑定,服务不会被结束;否则,解除绑定是,服务会自动被回收
        Intent intent = new Intent(this,PlayService.class);
        startService(intent);

        //延时自动进入MainActivity
     //   handler.sendEmptyMessageDelayed(START_ACTIVITY,3000);

    }

//    //延时自动进入MainActivity
//    private Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            System.out.println("SplashActivity.java : InMainActivity = " + InMainActivity);
//            //如果InMainActivity == false，则进入MainActivity，为了避免重复进入MainActivity
//            if (InMainActivity == false) {
//                super.handleMessage(msg);
//                switch (msg.what) {
//                    case START_ACTIVITY:
//                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                        finish();
//                        break;
//                }
//            }
//        }
//    };









//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//
//        // Trigger the initial hide() shortly after the activity has been
//        // created, to briefly hint to the user that UI controls
//        // are available.
//        delayedHide(100);
//    }

//    private void toggle() {
//        if (mVisible) {
//            hide();
//        } else {
//            show();
//        }
//    }

//    private void hide() {
//        // Hide UI first
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }
//        mControlsView.setVisibility(View.GONE);
//        mVisible = false;
//
//        // Schedule a runnable to remove the status and navigation bar after a delay
//        mHideHandler.removeCallbacks(mShowPart2Runnable);
//        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
//    }

//    @SuppressLint("InlinedApi")
//    private void show() {
//        // Show the system bar
//        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
//        mVisible = true;
//
//        // Schedule a runnable to display UI elements after a delay
//        mHideHandler.removeCallbacks(mHidePart2Runnable);
//        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
//    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
//    private void delayedHide(int delayMillis) {
//        mHideHandler.removeCallbacks(mHideRunnable);
//        mHideHandler.postDelayed(mHideRunnable, delayMillis);
//    }
}
