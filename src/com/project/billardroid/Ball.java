package com.project.billardroid;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Ball extends AnimatedSprite {

	public Body body;
	public boolean moving;
	
    // ---------------------------------------------
    // CONSTRUCTOR
    // ---------------------------------------------
    
    public Ball(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld, ITiledTextureRegion ball_region)
    {
    	super(pX, pY, ball_region, vbo);
        createPhysics(camera, physicsWorld);
        moving = false;
    }
    
    // ---------------------------------------------
    // METHODS
    // ---------------------------------------------
    
    private void createPhysics(final Camera camera, PhysicsWorld physicsWorld)
    {        
        body = PhysicsFactory.createCircleBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(10.0f, 1.0f, 0.0f));

        body.setUserData("ball");
        body.setFixedRotation(true);
        
        physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false));
    }
    
    public void applyImpulse() {
    	body.applyLinearImpulse(new Vector2(45.0f, body.getLinearVelocity().y), body.getWorldCenter());
    	body.setLinearDamping(0.5f);
    	moving = true;
    }
}
