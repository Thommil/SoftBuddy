package com.thommil.softbuddy.levels.mountain.actors;

import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.thommil.libgdx.runtime.actor.physics.ParticleSystemActor;
import com.thommil.libgdx.runtime.graphics.TextureSet;
import finnstr.libgdx.liquidfun.ParticleDef;
import finnstr.libgdx.liquidfun.ParticleSystemDef;

public class SoftBuddyActor extends ParticleSystemActor{

    final ParticleDef particleDef;

    public SoftBuddyActor(final int id, final float radius, final TextureSet textureSet) {
        super(id, radius, textureSet);
        particleDef = new ParticleDef();
        this.particleDef.flags.add(ParticleDef.ParticleType.b2_tensileParticle);
        this.particleDef.flags.add(ParticleDef.ParticleType.b2_viscousParticle);
    }

    @Override
    public ParticleSystemDef getDefinition() {
        final ParticleSystemDef particleSystemDef = super.getDefinition();
        particleSystemDef.density = 1.0f;
        particleSystemDef.viscousStrength = 0.1f;
        return particleSystemDef;
    }


    public void createParticle(float x, float y, float velocityX, float velocityY){
        this.particleDef.position.set(x,y);
        this.particleDef.velocity.set(velocityX, velocityY);
        this.particleSystem.createParticle(this.particleDef);
    }

    @Override
    public void dispose() {
        this.textureSet.dispose();
    }
}
