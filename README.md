# media_neo

## 介绍

media_neo是我开发的一个用于多媒体项目软件demo实现的工程，里面包含了一些音视频和相机的常见功能的代码实现。

## 配置Live Template

### CameraNeo配置

    // create CameraNeo object in OnCreate function
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
![1635075002475](https://gitee.com/hangliebe/resource/raw/master/webpimg/others/1635075002475.webp)