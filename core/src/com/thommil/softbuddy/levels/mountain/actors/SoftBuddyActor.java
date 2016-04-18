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

    public static final int SOFTBUDDY_MAX_PARTICLES = 1000;
    public static final float SOFTBUDDY_PARTICLES_RADIUS = 0.01f;
    public static final float SOFTBUDDY_PARTICLES_SCALEFACTOR = 2f;

    protected ParticleGroup particleGroup;

    public SoftBuddyActor(final int id, final TextureSet textureSet) {
        super(id, SOFTBUDDY_PARTICLES_RADIUS, textureSet);
    }

    @Override
    public ParticleSystemDef getDefinition() {
        final ParticleSystemDef particleSystemDef = super.getDefinition();
        particleSystemDef.density = 1f;

        particleSystemDef.dampingStrength= 1f;
        particleSystemDef.surfaceTensionPressureStrength= 1f;
        particleSystemDef.surfaceTensionNormalStrength= 1f;
        particleSystemDef.viscousStrength= 0.1f;
        //particleSystemDef.surfaceTensionPressureStrength= 0.1f;

        return particleSystemDef;
    }

    public ParticleGroup createGroup(float x, float y, int particlesCount, float shapeWidth){
        final ParticleGroupDef particleGroupDef = new ParticleGroupDef();
        particleGroupDef.flags.add(ParticleDef.ParticleType.b2_tensileParticle);
        particleGroupDef.flags.add(ParticleDef.ParticleType.b2_waterParticle);
        particleGroupDef.flags.add(ParticleDef.ParticleType.b2_viscousParticle);
        PolygonShape shape = new PolygonShape();
        particleGroupDef.position.set(x,y);
        final float particlesPerLine = (shapeWidth / SOFTBUDDY_PARTICLES_RADIUS) + 3f;
        shape.setAsBox(shapeWidth/2f,particlesCount/particlesPerLine/2f);
        Gdx.app.log("",""+shapeWidth + " "+particlesCount/particlesPerLine);
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
