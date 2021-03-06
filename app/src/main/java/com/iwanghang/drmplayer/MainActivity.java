package com.iwanghang.drmplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.astuetz.viewpager.extensions.sample.QuickContactFragment;
import com.iwanghang.drmplayer.utils.MediaUtils;
import com.iwanghang.drmplayer.vo.Mp3Info;

import java.nio.charset.CoderMalfunctionError;
import java.util.ArrayList;


public class MainActivity extends BaseActivity implements DownloadDialogFragment.DownloadSuccessListener {
	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static String[] PERMISSIONS_STORAGE = {
			"android.permission.READ_EXTERNAL_STORAGE",
			"android.permission.WRITE_EXTERNAL_STORAGE",
	       "android.permission.INTERNET"};


//	private final Handler handler = new Handler();

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;
	public DRMPlayerApp app;//取出全局对象 方便调用


//	private Drawable oldBackground = null;
//	private int currentColor = 0xFFC74B46;

	private MyMusicListFragment myMusicListFragment;
	private NetMusicListFragment netMusicListFragment;

	public static void verifyStoragePermissions(Activity activity) {

		try {
			//检测是否有写的权限
			int permission = ActivityCompat.checkSelfPermission(activity,
					"android.permission.WRITE_EXTERNAL_STORAGE");
			if (permission != PackageManager.PERMISSION_GRANTED) {
				// 没有写的权限，去申请写的权限，会弹出对话框
				ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//取出全局对象 方便调用
		app = (DRMPlayerApp) getApplication();

		setContentView(R.layout.activity_main);

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new MyPagerAdapter(getSupportFragmentManager());

		pager.setAdapter(adapter);

		/**
		 android TypedValue.applyDimension()的作用
		 这个方法是转变为标准尺寸的一个函数，例如
		 int size =(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20,context.getResources().getDisplayMetrics());
		 这里COMPLEX_UNIT_SP是单位，20是数值，也就是20sp。
		 */
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
				.getDisplayMetrics());
		pager.setPageMargin(pageMargin);

		tabs.setViewPager(pager);
		verifyStoragePermissions(this);

		//修改主界面颜色,稍后修复功能,暂时使用默认颜色
		//changeColor(currentColor);

		//绑定服务
		//服务在加载SplashActivity(欢迎页面)的时候,已经启动
		//bindPlayService();
		//这里,我在MyMusicListFragment里面绑定,而没有在MainActivity里绑定

	}

	@Override
	public void publish(int progress) {
		//更新进度条
	}

	@Override
	public void change(int position) {
		//更新歌曲位置.按钮的状态等信息
		/**
		 * 本地音乐的播放UI实际上在MyMusicListFragment中,所以要
		 * 先在MyMusicListFragmen中,写入public void changeUIStatus(int position){}
		 * 然后,传参过去
		 */
		if (pager.getCurrentItem()==0){//如果页面等于0,则说明选中的是第一个页面,我的音乐页面
			System.out.println("MainActivity.change.pager.getCurrentItem()==0");
			myMusicListFragment.loadData();//初始化数据
			myMusicListFragment.changeUIStatusOnPlay(position);
		}else if (pager.getCurrentItem()==1){
			System.out.println("MainActivity.change.pager.getCurrentItem()==1");
		}
	}

	//DownloadDialogFragment回传的是否点击下载按钮的回传值
	//@Override
	//public void downloadSelectListener(int isDownload) {
	//	if (isDownload == 0){
	//		Toast.makeText(MainActivity.this, "DownloadDialogFragment.下载点击事件.回传给.MainActivity", Toast.LENGTH_SHORT).show();
	//
	//	}else if (isDownload == 1){
	//		Toast.makeText(MainActivity.this, "DownloadDialogFragment.取消点击事件.回传给.MainActivity", Toast.LENGTH_SHORT).show();
	//		//MyMusicListFragment.newInstance();
	//	}
	//}

	//DownloadDialogFragment回传的是否下载成功的回传值
	@Override
	public void downloadSuccessListener(String isDownloadSuccess) {
		System.out.println("MainActivity.downloadSuccessListener = " + isDownloadSuccess);
		if (isDownloadSuccess.length() > 0){
			//Toast.makeText(MainActivity.this, "DownloadDialogFragment.下载成功.回传给.MainActivity = " + isDownloadSuccess, Toast.LENGTH_SHORT).show();
			//问题:下载完成后,确实通知媒体库更新了,但是马上初始化数据,可能媒体库还没有更新
			//临时解决:在ActionBar上,添加一个刷新按钮
			//永久解决:通过mp3Infos.size()的改变,来判断媒体库已经更新


			//更新本地音乐列表
			myMusicListFragment.loadData();//初始化数据
			myMusicListFragment.newInstance();//重新实例化一下本地音乐Fragment,加载新的数据
		}
	}

	public class MyPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "本地音乐", "网络音乐"};

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			//return SuperAwesomeCardFragment.newInstance(position);
			if(position==0){
				System.out.println("MainActivity.MyPagerAdapter.position==0");
				if(myMusicListFragment==null){
					myMusicListFragment = MyMusicListFragment.newInstance();
				}
				return myMusicListFragment;
			}else if (position == 1){
				System.out.println("MainActivity.MyPagerAdapter.position==1");
				if(netMusicListFragment==null){
					netMusicListFragment = NetMusicListFragment.newInstance();
				}
				return netMusicListFragment;
			}
			return null;
			//return MyMusicListFragment.newInstance();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {//填充菜单
		getMenuInflater().inflate(R.menu.main_menu,menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item){//菜单中事件
		switch (item.getItemId()){
			case R.id.newMyMusic:
				myMusicListFragment.loadData();//初始化数据
				myMusicListFragment.newInstance();//重新实例化一下本地音乐Fragment,加载新的数据
				System.out.println("刷新");
				break;
			case R.id.ilove:
				Intent intent = new Intent(this,MyLoveMusicActivity.class);
				startActivity(intent);
				System.out.println("我的收藏");
				break;
			case R.id.near_play:
				Intent intent2 = new Intent(this,MyRecordMusicActivity.class);
				startActivity(intent2);
				System.out.println("最近播放");
				break;
			case R.id.loginApp:
				Intent intent3 = new Intent(this,LoginActivity.class);
				startActivity(intent3);
				System.out.println("切换账号");
			case R.id.guanyu:
//				Intent intent4 = new Intent(this,LoadWebActivity.class);
//				startActivity(intent4);
//				System.out.println("关于我们");
//				break;
				Uri uri = Uri.parse("https://mp.weixin.qq.com/s/ehzbKLlfcZUW9i0xLb4aOQ");
				Intent intent4 = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent4);


		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//保存当前播放的一些状态值
		DRMPlayerApp app = (DRMPlayerApp) getApplication();
		SharedPreferences.Editor editor = app.sp.edit();
		//保存 当前正在播放的歌曲的位置
		editor.putInt("currentPosition",playService.getCurrentPosition());
		//保存 播放模式
		editor.putInt("play_mode",playService.getPlay_mode());
		//保存 提交
		editor.commit();

		//创建DRMPlayerApp继承Application,同时需要在把AndroidManiFest中的public换成DRMPlayerApp
		//在DRMPlayerApp的onCreate中 实例化 SharedPreferences
		//在MainActivity的onDestroy中 保存状态值
		//在PlayService的onCreate中 恢复状态值
	}



}


