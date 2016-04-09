package com.thommil.softbuddy.levels.mountain.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.thommil.libgdx.runtime.graphics.TextureSet;
import com.thommil.libgdx.runtime.graphics.renderer.sprite.SpriteBatchRenderer;


public class IntroRenderer extends SpriteBatchRenderer{

    /**
     SCREEN_WIDTH      SCREEN_HEIGHT         NORMAL_X_OFFSET        NORMAL_Y_OFFSET
     LIGHT_X           LIGHT_Y               LIGHT_ON               CONSTANT_ATTENUATION
     LIGHT_R           LIGHT_G               LIGHT_B                LINEAR_ATTENUATION
     AMBIENT_R         AMBIENT_G             AMBIENT_B              QUADRATIC_ATTENUATION
     */
    private static final int SCREEN_WIDTH = 0;
    private static final int SCREEN_HEIGHT = 1;
    private static final int NORMAL_X_OFFSET = 2;
    private static final int NORMAL_Y_OFFSET = 3;
    private static final int LIGHT_X = 4;
    private static final int LIGHT_Y = 5;
    private static final int LIGHT_ON = 6;
    private static final int CONSTANT_ATTENUATION = 7;
    private static final int LIGHT_R = 8;
    private static final int LIGHT_G = 9;
    private static final int LIGHT_B = 10;
    private static final int LINEAR_ATTENUATION = 11;
    private static final int AMBIENT_R = 12;
    private static final int AMBIENT_G = 13;
    private static final int AMBIENT_B = 14;
    private static final int QUADRATIC_ATTENUATION = 15;

    final float[] lightData = new float[]{
            0f,0f,0f,0f,
            0f,0f,0f,1f,
            1f,1f,1f,0.1f,
            1f,1f,1f,0.01f
    };

    public IntroRenderer(int size) {
        super(size);
    }

    public void switchLight(final boolean on){
        lightData[LIGHT_ON] = on ? 1f : 0f;
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

    public void setLightColor(final float r, final float g, final float b){
        lightData[LIGHT_R] = r;
        lightData[LIGHT_G] = g;
        lightData[LIGHT_B] = b;
    }

    public void setAmbiantColor(final float r, final float g, final float b){
        lightData[AMBIENT_R] = r;
        lightData[AMBIENT_G] = g;
        lightData[AMBIENT_B] = b;
    }

    public void setFallOff(final float constantAttenuation, final float linearAttenutation, final float quadraticAttenuation){
        lightData[CONSTANT_ATTENUATION] = constantAttenuation;
        lightData[LINEAR_ATTENUATION] = linearAttenutation;
        lightData[QUADRATIC_ATTENUATION] = quadraticAttenuation;
    }

    public void setNormalOffset(final float xOffset, final float yOffset){
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

        /**
         SCREEN_WIDTH      SCREEN_HEIGHT         NORMAL_X_OFFSET        NORMAL_Y_OFFSET
         LIGHT_X           LIGHT_Y               LIGHT_TYPE             CONSTANT_ATTENUATION
         LIGHT_R           LIGHT_G               LIGHT_B                LINEAR_ATTENUATION
         AMBIENT_R         AMBIENT_G             AMBIENT_B              QUADRATIC_ATTENUATION
         */
        String fragmentShader = "#ifdef GL_ES\n" //
                + "precision mediump float;\n" //
                + "#endif\n" //
                + "varying vec2 v_texCoords;\n" //
                + "varying vec4 v_color;\n" //
                + "uniform sampler2D "+ TextureSet.UNIFORM_TEXTURE_0+";\n" //
                + "uniform mat4 u_lightData;\n" //
                + "void main()\n"//
                + "{\n" //
                + "     vec4 DiffuseColor = texture2D("+ TextureSet.UNIFORM_TEXTURE_0+", v_texCoords);\n" // Diffuse
                + "     if (u_lightData[1].b == 1.0){\n" // Light ON
                + "         vec2 normalCoords = vec2(v_texCoords.x + u_lightData[0].b, v_texCoords.y + u_lightData[0].a);\n" //
                + "         vec3 NormalMap = texture2D("+ TextureSet.UNIFORM_TEXTURE_0+", normalCoords).rgb;\n" //
                + "         //NormalMap.g = 1.0 - NormalMap.g; //COMMENT/UNCOMMENT depending on normals generator \n" //
                + "         vec3 LightDir = vec3((u_lightData[1].xy - gl_FragCoord.xy)/u_lightData[0].xy , 0.0);\n" //
                + "         LightDir.x *= u_lightData[0].x / u_lightData[0].y;\n" //
                + "         float D = length(LightDir);\n" //
                + "         vec3 N = normalize(NormalMap * 2.0 - 1.0);\n" //
                + "         vec3 L = normalize(LightDir);\n" //
                + "         vec3 Diffuse = u_lightData[2].rgb * max(dot(N, L), 0.0);\n" //
                + "         float Attenuation = 1.0 / ( u_lightData[1].a + (u_lightData[2].a*D) + (u_lightData[3].a*D*D));\n" //
                + "         vec3 Intensity = u_lightData[3].rgb + Diffuse * Attenuation;\n" //
                + "         vec3 FinalColor = DiffuseColor.rgb * Intensity;\n" //
                + "         gl_FragColor = v_color * vec4(FinalColor, DiffuseColor.a);\n" //
                + "     }\n" //
                + "     else{\n" //Light OFF
                + "         gl_FragColor = vec4(u_lightData[3].rgb, 1.0) * DiffuseColor;\n" //
                + "     }\n" //
                + "}";
        final ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (shader.isCompiled() == false) throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
        return shader;
    }
}
