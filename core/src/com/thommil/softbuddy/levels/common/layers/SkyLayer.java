package com.thommil.softbuddy.levels.common.layers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.thommil.libgdx.runtime.layer.Layer;

public class SkyLayer extends Layer{

    public static final int VERTEX_SIZE = 3;
    public static final int VERTEX_COUNT = 4;
    public static final int SIZE = VERTEX_COUNT * VERTEX_SIZE;

    protected final Mesh mesh;
    protected final float[] vertices;
    protected final ShaderProgram shader;

    protected final Color topColor = new Color();
    protected final Color bottomColor = new Color();

    public static final float MIDNIGHT = 0f;
    public static final float NOON = 1f;

    private static final Color midnightTopColor = new Color(0x090a0fff);
    private static final Color midnightBottomColor = new Color(0x061029ff);

    private static final Color noonTopColor = new Color(0x7aacf1ff);
    private static final Color noonBottomColor = new Color(0xffffffff);

    private static final float starFieldTreshold = 0.99f;

    protected final boolean starField;
    protected float starsAlpha = 1f;

    public SkyLayer(Viewport viewport, final boolean starField, final float initialTime){
        this(viewport, starField, initialTime, -viewport.getWorldWidth()/2, -viewport.getWorldHeight()/2, viewport.getWorldWidth(), viewport.getWorldHeight());
    }

    public SkyLayer(Viewport viewport, final boolean starField, final float initialTime, final float x, final float y, final float width, final float height){
        super(viewport, 1);
        this.starField = starField;
        this.shader = this.createShader();
        this.mesh = this.createMesh();
        this.vertices = this.createVertices();

        this.vertices[0] = x;
        this.vertices[1] = y + height;
        this.vertices[2] = 0;

        this.vertices[3] = x;
        this.vertices[4] = y;
        this.vertices[5] = 0;

        this.vertices[6] = x + width;
        this.vertices[7] = y;
        this.vertices[8] = 0;

        this.vertices[9] = x + width;
        this.vertices[10] = y + height;
        this.vertices[11] = 0;

        this.setTime(initialTime);
    }


    public void setTime(final float time) {
        if (time <= NOON){
            topColor.set(midnightTopColor);
            bottomColor.set(midnightBottomColor);
            topColor.lerp(noonTopColor, time);
            bottomColor.lerp(noonBottomColor, time);
            if(this.starField) {
                this.starsAlpha = Math.max(1 - 2 * time, 0f);
            }
        }
        this.vertices[2] = this.vertices[11] = topColor.toFloatBits();
        this.vertices[5] = this.vertices[8] = bottomColor.toFloatBits();
        this.mesh.setVertices(this.vertices);
    }

    public void setMidnightColors(final Color topColor, final Color bottomColor){
        midnightTopColor.set(topColor);
        midnightBottomColor.set(bottomColor);
    }

    public void setNoonColors(final Color topColor, final Color bottomColor){
        noonTopColor.set(topColor);
        noonBottomColor.set(bottomColor);
    }

    @Override
    protected void onShow() {

    }

    @Override
    protected void onResize(int i, int i1) {

    }

    @Override
    protected void onHide() {

    }

    @Override
    public void render(float v) {
        this.shader.begin();
        this.shader.setUniformMatrix("u_projectionViewMatrix", this.viewport.getCamera().combined);
        if(this.starField) {
            this.shader.setUniformf("u_starsAlpha", starsAlpha);
        }
        this.mesh.render(this.shader, GL20.GL_TRIANGLES, 0, 6);
        this.shader.end();
    }

    protected Mesh createMesh() {
        Mesh.VertexDataType vertexDataType = Mesh.VertexDataType.VertexArray;
        if (Gdx.gl30 != null) {
            vertexDataType = Mesh.VertexDataType.VertexBufferObjectWithVAO;
        }
        final Mesh mesh = new Mesh(vertexDataType, true, 4, 6,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE));

        final short[] indices = new short[6];
        short j = 0;
        for (int i = 0; i < 6; i += 6, j += 4) {
            indices[i] = j;
            indices[i + 1] = (short)(j + 1);
            indices[i + 2] = (short)(j + 2);
            indices[i + 3] = (short)(j + 2);
            indices[i + 4] = (short)(j + 3);
            indices[i + 5] = j;
        }
        mesh.setIndices(indices);

        return mesh;
    }

    protected float[] createVertices() {
        return new float[SIZE];
    }

    protected ShaderProgram createShader() {
        final String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "uniform mat4 u_projectionViewMatrix;\n" //
                + "varying vec4 v_color;\n" //
                + "\n" //
                + "void main()\n" //
                + "{\n" //
                + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "   gl_Position =  u_projectionViewMatrix * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "}\n";

        final String fragmentShader;

        if(this.starField){
            fragmentShader =
                      "#ifdef GL_ES\n" //
                    + "precision highp float;\n" //
                    + "#endif\n" //
                    + "uniform float u_starsAlpha;\n" //
                    + "const float STARS_TRESHOLD="+starFieldTreshold+";\n" //
                    + "\n" //
                    + "varying vec4 v_color;\n" //
                    + "\n" //
                    + "float snoise(vec2 co){\n"
                    + "    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);\n"
                    + "}\n"
                    + "\n" //
                    + "void main()\n"//
                    + "{\n" //
                    + "   vec3 vColor  = v_color.rgb;\n" //
                    + "   if(u_starsAlpha > 0.0){\n" //
                    + "     float StarVal = snoise( gl_FragCoord.xy/100.0);\n" //
                    + "     if ( StarVal >= STARS_TRESHOLD )\n" //
                    + "     {\n" //
                    + "         StarVal = pow( (StarVal - STARS_TRESHOLD)/(1.0 - STARS_TRESHOLD), 6.0 );\n" //
                    + "         vColor += u_starsAlpha * vec3( StarVal);\n" //
                    + "     }\n" //
                    + "   }\n" //
                    + "   gl_FragColor = vec4(vColor, 1.0);\n" //
                    + "}";
        }
        else{
            fragmentShader =
                      "varying vec4 v_color;\n" //
                    + "\n" //
                    + "void main()\n"//
                    + "{\n" //
                    + "   gl_FragColor = v_color;\n" //
                    + "}";
        }

        final ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled()) throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
        return shader;
    }

    @Override
    public void dispose() {
        this.shader.dispose();
    }
}
