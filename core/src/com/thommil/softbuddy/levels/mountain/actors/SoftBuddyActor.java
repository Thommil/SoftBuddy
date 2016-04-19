package com.thommil.softbuddy.levels.mountain.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.thommil.libgdx.runtime.actor.physics.ParticleSystemActor;
import com.thommil.libgdx.runtime.graphics.TextureSet;
import finnstr.libgdx.liquidfun.ParticleDef;
import finnstr.libgdx.liquidfun.ParticleGroup;
import finnstr.libgdx.liquidfun.ParticleGroupDef;
import finnstr.libgdx.liquidfun.ParticleSystemDef;

public class SoftBuddyActor extends ParticleSystemActor{

    public static final int DEFAULT_MAX_PARTICLES = 1000;
    public static final float DEFAULT_PARTICLES_RADIUS = 0.03f;
    public static final float DEFAULT_PARTICLES_SCALEFACTOR = 2f;

    protected ParticleGroup particleGroup;

    public SoftBuddyActor(final int id, final TextureSet textureSet) {
        super(id, DEFAULT_PARTICLES_RADIUS, textureSet);
    }

    public SoftBuddyActor(final int id, final float particlesRadius, final TextureSet textureSet) {
        super(id, particlesRadius, textureSet);
    }

    @Override
    public ParticleSystemDef getDefinition() {
        final ParticleSystemDef particleSystemDef = super.getDefinition();
        particleSystemDef.density = 10f;

        return particleSystemDef;
    }

    public ParticleGroup createGroup(float x, float y, int particlesCount, float shapeWidth){
        final ParticleGroupDef particleGroupDef = new ParticleGroupDef();
        particleGroupDef.flags.add(ParticleDef.ParticleType.b2_tensileParticle);
        particleGroupDef.flags.add(ParticleDef.ParticleType.b2_waterParticle);
        particleGroupDef.flags.add(ParticleDef.ParticleType.b2_viscousParticle);

        PolygonShape shape = new PolygonShape();
        particleGroupDef.position.set(x,y);
        final float stride = 0.75f * (this.particlesRadius * 2f);
        final float particlesPerLine = (float)Math.ceil(shapeWidth / stride * 2f);
        final float shapeHeight = particlesCount / particlesPerLine * stride * 2;
        shape.setAsBox(shapeWidth/2f,shapeHeight/2f);
        particleGroupDef.shape = shape;
        this.particleGroup = this.particleSystem.createParticleGroup(particleGroupDef);
        shape.dispose();
        return this.particleGroup;
    }

    public ParticleGroup getParticleGroup(){
        return this.particleGroup;
    }


    @Override
    public void dispose() {
        this.textureSet.dispose();
    }
}
