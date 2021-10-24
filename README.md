# media_neo

#### 介绍
用于多媒体项目开发

#### 配置Live Template

    // create CameraNeo object in OnCreate
    CameraNeo mCameraNeo = new CameraNeo(this);
    private void neoCamera(String id) {
        // 1 open camera
        mCameraNeo.openCamera(id, new NeoCallback.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                super.onOpened(camera);
                mCameraNeo.setCameraDevice(camera);
                // 2 config session
                List<OutputConfiguration> list = new ArrayList<>();
                list.add(new OutputConfiguration(surfaceView.getHolder().getSurface()));
                // 3 create session
                mCameraNeo.createSession(SESSION_REGULAR, list, new NeoCallback.SessionStateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        mCameraNeo.setCaptureSession(session);
    
                        // 4 create request and send request
                        CaptureRequest.Builder builder = mCameraNeo.getBuilder(CameraDevice.TEMPLATE_RECORD);
                        builder.addTarget(surfaceView.getHolder().getSurface());
                        mCameraNeo.sendRequest(builder.build(), null, REPEATING_REQUEST);
                    }
                });
            }
        });
    }
![1635075002475](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1635075002475.png)