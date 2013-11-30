package com.project.billardroid;

import java.io.IOException;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.SAXUtils;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;
import org.andengine.util.level.EntityLoader;
import org.andengine.util.level.constants.LevelConstants;
import org.andengine.util.level.simple.SimpleLevelEntityLoaderData;
import org.andengine.util.level.simple.SimpleLevelLoader;
import org.xml.sax.Attributes;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.project.billardroid.SceneManager.SceneType;

public class GameScene extends BaseScene implements IOnSceneTouchListener
{
	
	private HUD gameHUD;
	private Text scoreText;
	private int score = 0;
	private PhysicsWorld physicsWorld;
	
	private Ball whiteball;
	private Ball redball;
	
	private static final String TAG_ENTITY = "entity";
	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";
	    
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_WHITEBALL = "whiteball";
	private static final Object TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_REDBALL = "redball";
	
    @Override
    public void createScene()
    {
        createBackground();
        createHUD();
        createPhysics();
        loadLevel(1);
        setOnSceneTouchListener(this);
    }

    @Override
    public void onBackKeyPressed()
    {
    	SceneManager.getInstance().loadMenuScene(engine);
    }

    @Override
    public SceneType getSceneType()
    {
        return SceneType.SCENE_GAME;
    }

    @Override
    public void disposeScene()
    {
        camera.setHUD(null);
        camera.setCenter(400, 240);

        // TODO code responsible for disposing scene
        // removing all game scene objects.
    }
    
    private void createBackground()
    {
        setBackground(new Background(Color.BLUE));
    }
    
    private void createHUD()
    {
        gameHUD = new HUD();
        
        // CREATE SCORE TEXT
        scoreText = new Text(20, 420, resourcesManager.font, "Score: 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
        scoreText.setAnchorCenter(0, 0);    
        scoreText.setText("Score: 0");
        gameHUD.attachChild(scoreText);
        
        camera.setHUD(gameHUD);
    }

    private void addToScore(int i)
    {
        score += i;
        scoreText.setText("Score: " + score);
    }

    private void createPhysics()
    {
        physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), false);
        physicsWorld.setContactListener(contactListener());
        registerUpdateHandler(physicsWorld);
    }    
    
    private void loadLevel(int levelID)
    {
        final SimpleLevelLoader levelLoader = new SimpleLevelLoader(vbom);
        
        final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.01f, 0.5f);
        
        levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(LevelConstants.TAG_LEVEL)
        {
            public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException 
            {
                final int width = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH);
                final int height = SAXUtils.getIntAttributeOrThrow(pAttributes, LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT);
                
                // TODO later we will specify camera BOUNDS and create invisible walls
                // on the beginning and on the end of the level.

                return GameScene.this;
            }
        });
        
        levelLoader.registerEntityLoader(new EntityLoader<SimpleLevelEntityLoaderData>(TAG_ENTITY)
        {
            public IEntity onLoadEntity(final String pEntityName, final IEntity pParent, final Attributes pAttributes, final SimpleLevelEntityLoaderData pSimpleLevelEntityLoaderData) throws IOException
            {
                final int x = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_X);
                final int y = SAXUtils.getIntAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_Y);
                final String type = SAXUtils.getAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE);
                
                final Sprite levelObject;
                
                if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_WHITEBALL))
                {
                    whiteball = new Ball(x, y, vbom, camera, physicsWorld, resourcesManager.whiteball_region);
                    levelObject = whiteball;
                }
                else if (type.equals(TAG_ENTITY_ATTRIBUTE_TYPE_VALUE_REDBALL))
                {
                    redball = new Ball(x, y, vbom, camera, physicsWorld, resourcesManager.redball_region);
                    levelObject = redball;
                }          
                else
                {
                    throw new IllegalArgumentException();
                }

                levelObject.setCullingEnabled(true);

                return levelObject;
            }
        });

        levelLoader.loadLevelFromAsset(activity.getAssets(), "levels/level_" + levelID + ".xml");
    }
    
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
    {
        if (pSceneTouchEvent.isActionDown())
        {
        	whiteball.applyImpulse();
        }
        return false;
    }
    
    private ContactListener contactListener()
    {
        ContactListener contactListener = new ContactListener()
        {
            public void beginContact(Contact contact)
            {
                final Fixture x1 = contact.getFixtureA();
                final Fixture x2 = contact.getFixtureB();

                if (x1.getBody().getUserData() != null && x2.getBody().getUserData() != null)
                {
                    x1.getBody().setLinearDamping(0.5f);
                    x2.getBody().setLinearDamping(0.5f);
                }
                
                addToScore(1);
            }

            public void endContact(Contact contact)
            {
            }

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub
				
			}
        };
        return contactListener;
    }
}
