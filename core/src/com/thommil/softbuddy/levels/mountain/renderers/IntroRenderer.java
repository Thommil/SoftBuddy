package com.thommil.softbuddy.levels.mountain.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.thommil.libgdx.runtime.graphics.TextureSet;
import com.thommil.libgdx.runtime.graphics.renderer.sprite.SpriteBatchRenderer;


public class IntroRenderer extends SpriteBatchRenderer{

    /**
     [
     SCREEN_WIDTH,      SCREEN_HEIGHT,          NORMAL_X_OFFSET,        NORMAL_Y_OFFSET
     LIGHT_TYPE,        LIGHT_X,                LIGHT_Y,                LIGHT_R,
     LIGHT_G,           LIGHT_B,                AMBIENT_R,              AMBIENT_G,
     AMBIENT_B,         CONSTANT_ATTENUATION    LINEAR_ATTENUATION,     QUADRATIC_ATTENUATION
     ]
     */
    private static final int SCREEN_WIDTH = 0;
    private static final int SCREEN_HEIGHT = 1;
    private static final int NORMAL_X_OFFSET = 2;
    private static final int NORMAL_Y_OFFSET = 3;
    private static final int LIGHT_TYPE = 4;
    private static final int LIGHT_X = 5;
    private static final int LIGHT_Y = 6;
    private static final int LIGHT_R = 7;
    private static final int LIGHT_G = 8;
    private static final int LIGHT_B = 9;
    private static final int AMBIENT_R = 10;
    private static final int AMBIENT_G = 11;
    private static final int AMBIENT_B = 12;
    private static final int CONSTANT_ATTENUATION = 13;
    private static final int LINEAR_ATTENUATION = 14;
    private static final int QUADRATIC_ATTENUATION = 15;


    public static final int LIGHT_OFF = 0;
    public static final int LIGHT_DIRECTIONAL = 1;
    public static final int LIGHT_SPOT = 2;

    final float[] lightData = new float[]{
            0,0,0,0,
            0,0,0,0,
            0,0,1,1,
            1,1,0.1f,0.01f
    };

    public IntroRenderer(int size) {
        super(size);
    }

    public void setLightType(final int lightType){
        lightData[LIGHT_TYPE] = lightType;
    }

    public void setLightPosition(final int x, final int y){
        lightData[LIGHT_X] = x;
        lightData[LIGHT_Y] = y;
    }

    public void setScreenSize(final int width, final int height){
        lightData[LIGHT_X] = lightData[LIGHT_X] * width/lightData[SCREEN_WIDTH];
        lightData[LIGHT_Y] = lightData[LIGHT_Y] * height/lightData[SCREEN_HEIGHT];
        lightData[SCREEN_WIDTH] = width;
        lightData[SCREEN_HEIGHT] = height;
    }

    public void setLightColor(final Color color){
        lightData[LIGHT_R] = color.r;
        lightData[LIGHT_G] = color.g;
        lightData[LIGHT_B] = color.b;
    }

    public void setAmbiantColor(final Color color){
        lightData[AMBIENT_R] = color.r;
        lightData[AMBIENT_G] = color.g;
        lightData[AMBIENT_B] = color.b;
    }

    public void setFallOff(final float constantAttenuation, final float linearAttenutation, final float quadraticAttenuation){
        lightData[CONSTANT_ATTENUATION] = constantAttenuation;
        lightData[LINEAR_ATTENUATION] = linearAttenutation;
        lightData[QUADRATIC_ATTENUATION] = quadraticAttenuation;
    }

    public void setNormalOffset(final int xOffset, final int yOffset){
        lightData[NORMAL_X_OFFSET] = xOffset;
        lightData[NORMAL_Y_OFFSET] = yOffset;
    }

    @Override
    public void begin() {
        super.begin();
        this.shader.setUniformMatrix4fv("u_lightData", lightData, 0 , 16);
    }

    /**
     * Subclasses should override this method to use their specific vertices
     */
    @Override
    protected ShaderProgram createShader() {
        String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "uniform mat4 u_projectionViewMatrix;\n" //
                + "varying vec4 v_color;\n" //
                + "varying vec2 v_texCoords;\n" //
                + "\n" //
                + "void main()\n" //
                + "{\n" //
                + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "   gl_Position =  u_projectionViewMatrix * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "}\n";

        String fragmentShader = "#ifdef GL_ES\n" //
                + "precision mediump float;\n" //
                + "#endif\n" //
                + "varying vec2 v_texCoords;\n" //
                + "varying vec4 v_color;\n" //
                + "uniform sampler2D "+ TextureSet.UNIFORM_TEXTURE_0+";\n" //
                + "uniform mat4 u_lightData;\n" //
                + "void main()\n"//
                + "{\n" //
                + "     float x = u_lightData[3][0];\n" //
                + "     gl_FragColor = texture2D("+ TextureSet.UNIFORM_TEXTURE_0+", v_texCoords) * x;\n" //
                + "}";
                /*+ "const vec3 LightColor = vec3(1.0, 0.8, 0.4);\n" //
                + "const vec3 Falloff = vec3(1, 20.0, 100.0);\n" //
                + "const vec3 AmbientColor = vec3(0.3, 0.3, 0.3);\n" //
                + "void main()\n"//
                + "{\n" //
                + "     vec4 DiffuseColor = texture2D("+ TextureSet.UNIFORM_TEXTURE_0+", v_texCoords);\n" //
                + "     if (u_lightOn == 1){\n" //
                + "         vec3 NormalMap = texture2D("+ TextureSet.UNIFORM_TEXTURE_1+", v_texCoords).rgb;\n" //
                + "         NormalMap.g = 1.0 - NormalMap.g; //COMMENT/UNCOMMENT depending on normals generator \n" //
                + "         vec3 LightDir = vec3((u_lightData.xy - gl_FragCoord.xy)/u_lightData.ba , 0.0);\n" //
                + "         LightDir.x *= u_lightData.b / u_lightData.a;\n" //
                + "         float D = length(LightDir);\n" //
                + "         vec3 N = normalize(NormalMap * 2.0 - 1.0);\n" //
                + "         vec3 L = normalize(LightDir);\n" //
                + "         vec3 Diffuse = LightColor.rgb * max(dot(N, L), 0.0);\n" //
                + "         float Attenuation = 1.0 / ( Falloff.x + (Falloff.y*D) + (Falloff.z*D*D) );\n" //
                + "         vec3 Intensity = AmbientColor + Diffuse * Attenuation;\n" //
                + "         vec3 FinalColor = DiffuseColor.rgb * Intensity;\n" //
                + "         gl_FragColor = v_color * vec4(FinalColor, DiffuseColor.a);\n" //
                + "     }\n" //
                + "     else{\n" //
                + "         vec3 FinalColor = DiffuseColor.rgb * AmbientColor;\n" //
                + "         gl_FragColor = v_color * vec4(FinalColor, DiffuseColor.a);\n" //
                + "     }\n" //
                + "}";*/
        final ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (shader.isCompiled() == false) throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
        return shader;
    }
}
