package com.hangliebe.medianeo.case_opengles;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glViewport;

public class CameraRender implements GLSurfaceView.Renderer {
    private static String TAG = "CameraRender";
    private SurfaceTexture mSurfaceTexture;
    private float[] mTransformMatrix = new float[16];
    private int mOESTextureId;
    private ShaderUtils mShaderUtils;
    private int mShaderProgramId;
    FloatBuffer mVerDataBuffer;
    FloatBuffer mDexDataBuffer;

    private int aPositionLocation = -1;
    private int aTextureCoordLocation = -1;
    private int uTextureMatrixLocation = -1;
    private int uTextureSamplerLocation = -1;

    // 定点坐标
    private static final float[] vertexData = {
            1f, 1f,
            -1f, 1f,
            -1f, -1f,
            1f, 1f,
            -1f, -1f,
            1f, -1f,
    };

    // 纹理坐标
    private static final float[] textureData = {
            1f, 1f,
            0f, 1f,
            0f, 0f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };

    private void createOESTexObj() {
        Log.d(TAG, "start createOESTexObj");
        int[] tex = new int[1];
        //生成一个纹理
        GLES20.glGenTextures(1, tex, 0);
        //将此纹理绑定到外部纹理上
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, tex[0]);
        //设置纹理过滤参数
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        //解除纹理绑定
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, 0);
        mOESTextureId =  tex[0];
    }

    public int getOESTextureId() {
        return mOESTextureId;
    }

    public FloatBuffer createBuffer(float[] vertexData) {
        Log.d(TAG, "createBuffer");
        FloatBuffer buffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        buffer.put(vertexData, 0, vertexData.length).position(0);
        return buffer;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated");
        createOESTexObj();
        mVerDataBuffer = createBuffer(vertexData);
        mDexDataBuffer = createBuffer(textureData);
        mShaderUtils = new ShaderUtils();
        mShaderProgramId = mShaderUtils.getProgramId();
        glUseProgram(mShaderProgramId);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "onSurfaceChanged, width:" + width + " height:" + height);
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.d(TAG, "onDrawFrame");
        if (mSurfaceTexture != null) {
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mTransformMatrix);
        }

        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        aPositionLocation = GLES20.glGetAttribLocation(mShaderProgramId, "aPosition");
        aTextureCoordLocation = GLES20.glGetAttribLocation(mShaderProgramId, "aTextureCoordinate");
        uTextureMatrixLocation = GLES20.glGetUniformLocation(mShaderProgramId, "uTextureMatrix");
        uTextureSamplerLocation = GLES20.glGetUniformLocation(mShaderProgramId, "uTextureSampler");

        GLES20.glActiveTexture(GL_TEXTURE_EXTERNAL_OES);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mOESTextureId);
        GLES20.glUniform1i(uTextureSamplerLocation, 0);
        GLES20.glUniformMatrix4fv(uTextureMatrixLocation, 1, false, mTransformMatrix, 0);

        mVerDataBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        // The number 8 means that the next set of data is read at 2*4 byte intervals
        GLES20.glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 8, mVerDataBuffer);

        mDexDataBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aTextureCoordLocation);
        // The number 8 means that the next set of data is read at 2*4 byte intervals
        GLES20.glVertexAttribPointer(aTextureCoordLocation, 2, GL_FLOAT, false, 8, mDexDataBuffer);

        GLES20.glDrawArrays(GL_TRIANGLES, 0, 6);
        GLES20.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        mSurfaceTexture = surfaceTexture;
    }
}
