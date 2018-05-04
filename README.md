# MeshSDK_Android

1： 安卓 6.0及以上版本需要申请以下权限：        
android.Manifest.permission.ACCESS_COARSE_LOCATION
android.Manifest.permission.ACCESS_FINE_LOCATION

2：确保蓝牙在打开状态



private MessageManager mm;
private MessageManager.OnMessageListener onMessageListener = new MessageManager.OnMessageListener() {
	@Override
	public void onMessage(final ScanMessage message) {
		//此处是接收到的消息回调；
	}
};

//	在onCreate中初始化对象；
mm = new MessageManager(onMessageListener);
//	开启MESH消息接收，onMessageListener将收到扫描到的信息；
mm.start();


//	广播消息请执行下面代码；
String content = "此处是需要发送的消息内容！";
mm.startAdvertising(content.getBytes());