package com.example.cchess;

import com.example.view.ViewHelp;
import com.example.view.ViewMenu;
import com.example.view.ViewWelcome;
import com.util.ActivityCollector;
import com.vsplay.GameView2;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

	public boolean isSound = true;//是否播放声音
	public MediaPlayer startSound;//开始和菜单时的音乐
	public MediaPlayer gamesound;//游戏声音
	
	public Handler myHandler = new Handler(){//用来更新UI线程中的控件
        public void handleMessage(Message msg) {
        	if(msg.what == 1){	//WelcomeView或HelpView或GameView传来的消息，切换到MenuView
        		initMenuView();//初始化并切换到菜单界面
        	}
        	else if(msg.what == 2){//MenuView传来的消息，切换到GameView
        		initGameView();	//初始化并切换到游戏界面
        	}
        	else if(msg.what == 3){//MenuView传来的消息，切换到HelpView
        		initHelpView();
        	}else if(msg.what == 4){ //双人对战
        		initVs(); 
        	}else if(msg.what == 5){ // 联机对战
        		initCon();
        	}
        }
	};
	
	public int screenW;
	public int screenH; 	
	
    public void onCreate(Bundle savedInstanceState) {//重写的onCreate
        super.onCreate(savedInstanceState);
        
        ActivityCollector.addActivity(this);
        
        
		//全屏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
		              WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenW = dm.widthPixels;
		screenH = dm.heightPixels;
		
		startSound = MediaPlayer.create(this, R.raw.startsound);//加载欢迎声音
		startSound.setLooping(true);//设置游戏声音循环播放 
		gamesound  = MediaPlayer.create(this, R.raw.gamesound);//游戏过程的背景声音
		gamesound.setLooping(true);//设置游戏声音循环播放 
        this.initWelcomeView();//初始化欢迎界面
    }
    
    public void initWelcomeView(){//初始化欢迎界面 
    	this.setContentView(new ViewWelcome(this,this));//
    	if(isSound){//需要播放声音时
    		startSound.start();//播放声音
		}
    }
    
    public void initGameView(){//初始化人机对战界面
    	this.setContentView(new GameView(this,this)); //切换到游戏界面
    }
    
    public void initMenuView(){//初始化菜单界面
    	if(startSound != null){//停止
    		startSound.stop();//停止播放声音
    		startSound = null;
    	}
    	if(this.isSound){//是否播放声音
    		gamesound.start();//播放声音
    	}
    	this.setContentView(new ViewMenu(this,this));//切换到菜单界面
    } 
    
    public void initHelpView(){//初始化帮助界面
    	this.setContentView(new ViewHelp(this,this));//切换到帮助界面
    }
    
    public void initVs(){//初始化双人对战界面
    	this.setContentView(new GameView2(this,this)); //切换到双人对战
    }
    
    public void initCon(){ // 联机对战
    	Intent it=new Intent();
		it.setClass(this, TwoVSActivity.class);
		startActivity(it);
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		ActivityCollector.removeActivity(this);  
	}
    
}
