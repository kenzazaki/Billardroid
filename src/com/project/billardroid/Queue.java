package com.project.billardroid;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;


public class Queue extends Sprite{

	public Body body;
	
    // ---------------------------------------------
    // CONSTRUCTOR
    // ---------------------------------------------
    
    public Queue(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld, ITextureRegion queue_region)
    {
    	super(pX, pY, queue_region, vbo);
        createPhysics(camera, physicsWorld);
    }
    
    // ---------------------------------------------
    // METHODS
    // ---------------------------------------------
    
    private void createPhysics(final Camera camera, PhysicsWorld physicsWorld)
    {        
    	FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(10.0f, 0.0f, 0.0f);
    	
    	/* La canne collisionne seulement avec la boule blanche */
    	fixtureDef.filter.categoryBits = ResourcesManager.getInstance().CATEGORY_QUEUE;
    	fixtureDef.filter.maskBits = ResourcesManager.getInstance().MASK_QUEUE;
    	
        body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, fixtureDef);
        body.setUserData("queue");
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false));
    }
    
    public void applyImpulse() {
    	body.applyLinearImpulse(new Vector2(45.0f, body.getLinearVelocity().y), body.getWorldCenter());
    }

}
