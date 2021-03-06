package com.project.billardroid;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class Border extends Sprite {

	public Body body;
	
    // ---------------------------------------------
    // CONSTRUCTOR
    // ---------------------------------------------
    
    public Border(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld, ITextureRegion border_region)
    {
    	super(pX, pY, border_region, vbo);
        createPhysics(camera, physicsWorld);
    }
    
    // ---------------------------------------------
    // METHODS
    // ---------------------------------------------
    
    private void createPhysics(final Camera camera, PhysicsWorld physicsWorld)
    {        
    	FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(0.0f, 0.0f, 0.0f);
    	/* Les bordures collisionnent avec toutes les boules (1 et 8), mais pas la queue (4)*/
    	fixtureDef.filter.categoryBits = ResourcesManager.getInstance().CATEGORY_BORDER;
    	fixtureDef.filter.maskBits = ResourcesManager.getInstance().MASK_BORDER;
    	
        body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.StaticBody, fixtureDef);
        body.setUserData("border");
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false));
    }
}
