package com.thommil.softbuddy.levels.mountain.actors;

import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.thommil.libgdx.runtime.actor.physics.ParticleSystemActor;
import com.thommil.libgdx.runtime.graphics.TextureSet;
import finnstr.libgdx.liquidfun.ParticleDef;
import finnstr.libgdx.liquidfun.ParticleGroup;
import finnstr.libgdx.liquidfun.ParticleGroupDef;
import finnstr.libgdx.liquidfun.ParticleSystemDef;

public class SoftBuddyActor extends ParticleSystemActor{

    protected ParticleGroup particleGroup;

    public SoftBuddyActor(final int id, final float radius, final TextureSet textureSet) {
        super(id, radius, textureSet);
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

    public void createGroup(float x, float y, float w, float h){
        ParticleGroupDef particleGroupDef = new ParticleGroupDef();
        particleGroupDef.flags.add(ParticleDef.ParticleType.b2_tensileParticle);
        particleGroupDef.flags.add(ParticleDef.ParticleType.b2_waterParticle);
        particleGroupDef.flags.add(ParticleDef.ParticleType.b2_viscousParticle);
        PolygonShape waterShape = new PolygonShape();

        particleGroupDef.position.set(x,y);
        waterShape.setAsBox(w/2,h/2);
        particleGroupDef.shape = waterShape;

        this.particleGroup = this.particleSystem.createParticleGroup(particleGroupDef);
    }

    public ParticleGroup getParticleGroup(){
        return this.particleGroup;
    }


    @Override
    public void dispose() {
        this.textureSet.dispose();
    }
}
