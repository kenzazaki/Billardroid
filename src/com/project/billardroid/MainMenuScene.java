package com.project.billardroid;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.util.GLState;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.project.billardroid.SceneManager.SceneType;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener{

	private MenuScene menuChildScene;
	private final int MENU_PLAY = 0;
	private final int MENU_OPTIONS = 1;
	
	@Override
	public void createScene() {
		createBackground();
		createMenuChildScene();
		if(!checkAuth()) popAuthDialog();
	}

	@Override
	public void onBackKeyPressed() {
		System.exit(0);	
	}

	@Override
	public SceneType getSceneType() {
		 return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub
		
	}
	
	private void createBackground()
	{
	    attachChild(new Sprite(400, 240, resourcesManager.menu_background_region, vbom)
	    {
	        @Override
	        protected void preDraw(GLState pGLState, Camera pCamera) 
	        {
	            super.preDraw(pGLState, pCamera);
	            pGLState.enableDither();
	        }
	    });
	}
	
	private void createMenuChildScene()
	{
	    menuChildScene = new MenuScene(camera);
	    menuChildScene.setPosition(400, 240);
	    
	    final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, resourcesManager.play_region, vbom), 1.1f, 1);
	    final IMenuItem optionsMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_OPTIONS, resourcesManager.options_region, vbom), 1.1f, 1);
	    
	    menuChildScene.addMenuItem(playMenuItem);
	    menuChildScene.addMenuItem(optionsMenuItem);
	    
	    menuChildScene.buildAnimations();
	    menuChildScene.setBackgroundEnabled(false);
	    
	    playMenuItem.setPosition(0, 100);
	    optionsMenuItem.setPosition(0, -100);
	    
	    menuChildScene.setOnMenuItemClickListener(this);
	    
	    setChildScene(menuChildScene);
	}

	public boolean checkAuth() {
		if(resourcesManager.idUser > 0) return true;
		else return false;
	}
	
	public void popAuthDialog() {
		resourcesManager.activity.runOnUiThread(new Runnable() { 
            @Override
            public void run() {
        		LayoutInflater inflater = resourcesManager.activity.getLayoutInflater();
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(resourcesManager.activity).setTitle("Login");
                
                final View inflatedView = inflater.inflate(R.layout.auth_dialog, null);
                Button validateButton = (Button)inflatedView.findViewById(R.id.validateButton);
                
                alertBuilder.setView(inflatedView);
                final AlertDialog alertDialog = alertBuilder.create();
                
                validateButton.setOnClickListener(new OnClickListener() {
        			@Override
        			public void onClick(View v) {
        				String nick = ((EditText)inflatedView.findViewById(R.id.nickField)).getText().toString();
        				String password = ((EditText)inflatedView.findViewById(R.id.passwordField)).getText().toString();
        				password = resourcesManager.md5(password);
        				
        				/* Login script on the server */
        				ClientServer cs = new ClientServer("http://timothy.carbone.etu.p.luminy.univ-amu.fr/projets/Billardroid/login.php");
        				ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
        				postParams.add(new BasicNameValuePair("nick", nick));
        				postParams.add(new BasicNameValuePair("password", password));
        				cs.setPostParameters(postParams);
        				
        				JSONObject loginResponse = new JSONObject();
        				/* Checking response of the server*/
						try {
							loginResponse = cs.execute().get();
							if(loginResponse.getInt("success") == 1) {
								resourcesManager.idUser = loginResponse.getInt("id_user");
								Log.i("LOGIN", "USER ID " + resourcesManager.idUser + " NOW CONNECTED");
								alertDialog.dismiss();
							}
							else {
								Log.i("LOGIN", "NOT CONNECTED : " + loginResponse.getString("message"));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
        			}
                	
                });
                
                alertDialog.show();  
            }
		});
	}
	
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch(pMenuItem.getID())
        {
        case MENU_PLAY:
        	//Load Game Scene!
            SceneManager.getInstance().loadGameScene(engine);
            return true;
        case MENU_OPTIONS:
            return true;
        default:
            return false;
        }
	}

}
