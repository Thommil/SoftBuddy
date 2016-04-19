package com.thommil.softbuddy.levels.common.renderers;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thommil.libgdx.runtime.graphics.TextureSet;
import com.thommil.libgdx.runtime.graphics.renderer.buffer.OffScreenRenderer;

public class SoftBuddyRenderer extends OffScreenRenderer {

    public SoftBuddyRenderer(Viewport viewport) {
        super(viewport, Pixmap.Format.RGBA4444);
    }

    @Override
    protected ShaderProgram createShader() {
        final String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "uniform mat4 u_projectionViewMatrix;\n" //
                + "varying vec2 v_texCoords;\n" //
                + "\n" //
                + "void main()\n" //
                + "{\n" //
                + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "   gl_Position =  u_projectionViewMatrix * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "}\n";
        final String fragmentShader = "#ifdef GL_ES\n" //
                + "#define LOWP lowp\n" //
                + "precision mediump float;\n" //
                + "#else\n" //
                + "#define LOWP \n" //
                + "#endif\n" //
                + "varying vec2 v_texCoords;\n" //
                + "uniform sampler2D "+TextureSet.UNIFORM_TEXTURE_0+";\n" //
                + "void main()\n"//
                + "{\n" //
                + "  vec4 color = texture2D("+ TextureSet.UNIFORM_TEXTURE_0+", v_texCoords);\n" //
                + "  if( color.r < 0.15){\n" //
                + "     color = vec4(0,0,0,0);\n" //
                + "  }\n" //
                + "  else if( color.r < 0.3){\n" //
                + "     color = vec4(0.0,1.0,0.0,color.a);\n" //
                + "  }\n" //
                + "  else{\n" //
                + "     color = vec4(0.0,1.0,0.0,0.6);\n" //
                + "  }\n" //
                + "  gl_FragColor = color;\n" //
                + "}";

        final ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (shader.isCompiled() == false) throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
        return shader;
    }
}
