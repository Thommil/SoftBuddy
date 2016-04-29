package com.thommil.softbuddy.levels.common.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.thommil.libgdx.runtime.actor.physics.ParticleSystemActor;
import com.thommil.libgdx.runtime.graphics.TextureSet;
import com.thommil.softbuddy.SharedResources;
import finnstr.libgdx.liquidfun.ParticleDef;
import finnstr.libgdx.liquidfun.ParticleGroup;
import finnstr.libgdx.liquidfun.ParticleGroupDef;
import finnstr.libgdx.liquidfun.ParticleSystemDef;

public class SoftBuddyActor extends ParticleSystemActor{

    protected final SharedResources.SoftBuddyDef softBuddyDef;
    protected ParticleGroup particleGroup;

    public float moveForce = 50;

    public SoftBuddyActor(final int id, final SharedResources.SoftBuddyDef softBuddyDef, final AssetManager assetManager) {
        super(id, softBuddyDef.particlesRadius, new TextureSet(assetManager.get(softBuddyDef.particlesImage, Texture.class)));
        this.softBuddyDef = softBuddyDef;
        this.moveForce = softBuddyDef.moveForce;
    }

    @Override
    public ParticleSystemDef getDefinition() {
        return this.softBuddyDef;
    }

    public ParticleGroup createGroup(float x, float y, float shapeWidth){
        final ParticleGroupDef particleGroupDef = new ParticleGroupDef();
        particleGroupDef.flags.add(ParticleDef.ParticleType.b2_tensileParticle);
        particleGroupDef.flags.add(ParticleDef.ParticleType.b2_waterParticle);
        particleGroupDef.flags.add(ParticleDef.ParticleType.b2_viscousParticle);

        PolygonShape shape = new PolygonShape();
        particleGroupDef.position.set(x,y);
        final float stride = 0.75f * (this.particlesRadius * 2f);
        final float particlesPerLine = (float)Math.ceil(shapeWidth / stride * 2f);
        final float shapeHeight = this.softBuddyDef.maxParticles / particlesPerLine * stride * 2;
        shape.setAsBox(shapeWidth/2f,shapeHeight/2f);
        particleGroupDef.shape = shape;
        this.particleGroup = this.particleSystem.createParticleGroup(particleGroupDef);
        shape.dispose();

        return this.particleGroup;
    }

    public ParticleGroup getParticleGroup(){
        return this.particleGroup;
    }

    public void reset(){
        if(this.particleGroup != null){
            this.particleGroup.destroyParticlesInGroup();
        }
    }

    @Override
    public void dispose() {

    }
}
