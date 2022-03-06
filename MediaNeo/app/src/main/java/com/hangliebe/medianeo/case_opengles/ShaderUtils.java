package com.hangliebe.medianeo.case_opengles;

import android.util.Log;

import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glGetError;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;

public class ShaderUtils {
    private static String TAG = "ShaderUtils";
    private int mShaderProgram = -1;

    private final String VERTEX_SHADER = "" +
            "attribute vec4 aPosition;\n" +
            "uniform mat4 uTextureMatrix;\n" +
            "attribute vec4 aTextureCoordinate;\n" +
            "varying vec2 vTextureCoord;\n"+
            "void main()\n"+
            "{\n"+
            "  vTextureCoord = (uTextureMatrix * aTextureCoordinate).xy;\n"+
            "  gl_Position = aPosition;\n"+
            "}\n";

    private final String FRAGMENT_SHADER = "" +
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
            "uniform samplerExternalOES uTextureSampler;\n" +
            "varying vec2 vTextureCoord;\n" +
            "void main() \n" +
            "{\n" +
            // get the camera preview frame color
            "  vec4 vFrameColor = texture2D(uTextureSampler, vTextureCoord);\n" +
            "  float r = vFrameColor.r;\n" +
            "  float g = vFrameColor.g;\n" +
            "  float b = vFrameColor.b;\n" +
            // get the gray color of the preview frame
            // "  float fGrayColor = (0.3*r + 0.59*g + 0.11*b);\n" +
            // "  gl_FragColor = vec4(fGrayColor, fGrayColor, fGrayColor, 1.0);\n" +
            "  gl_FragColor = vec4(r, g, b, 1.0);\n" +
            "}\n";

    private int loadAndCompileShader(int type, String shaderSource) {
        Log.d(TAG, "loadShader");
        int shader = glCreateShader(type);
        if (shader == 0) {
            throw new RuntimeException("Create Shader Failed!" + glGetError());
        }
        glShaderSource(shader, shaderSource);
        glCompileShader(shader);
        return shader;
    }

    private int linkProgram(int verShader, int fragShader) {
        Log.d(TAG, "linkProgram");
        int program = glCreateProgram();
        if (program == 0) {
            throw new RuntimeException("Create Program Failed!" + glGetError());
        }
        glAttachShader(program, verShader);
        glAttachShader(program, fragShader);
        glLinkProgram(program);
        return program;
    }

    public int getProgramId() {
        Log.d(TAG, "getProgramId");
        if (mShaderProgram == -1) {
            int vertexShader =  loadAndCompileShader(GL_VERTEX_SHADER,  VERTEX_SHADER);
            int fragmentShader =  loadAndCompileShader(GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
            mShaderProgram = linkProgram(vertexShader, fragmentShader);
        }
        return mShaderProgram;
    }
}
