# MeshSDK_Android

### 概述

MTC SDK开发包（MeshSDK）使用苹果的蓝牙协议，提供了蓝牙数据扫描、APP唤醒、广播蓝牙数据，并支持配置指定设备参数等API。你可以访问MTC官网（http://www.mtc.io）了解更多信息，加入MTC社群交流我们软硬件相关问题。

MTC SDK开发包需要手持设备硬件支持蓝牙4.0及其以上，并要求系统版本至少Android 5.0及其以上。 

### 集成指南
1： 安卓 6.0及以上版本需要申请以下权限：        
android.Manifest.permission.ACCESS_COARSE_LOCATION
android.Manifest.permission.ACCESS_FINE_LOCATION

2：确保蓝牙在打开状态

3: 将演示中的jar包拷贝到工程中；

### 使用方法

- 声明对象及回调消息回调接口

private MessageManager mm;
private MessageManager.OnMessageListener onMessageListener = new MessageManager.OnMessageListener() {
	@Override
	public void onMessage(final ScanMessage message) {
		//此处是接收到的消息回调；
	}
};

- 在onCreate中初始化对象；

mm = new MessageManager(onMessageListener);


- 接收普通数据

mm.start();


- 广播普通数据

String content = "此处是需要发送的消息内容！";
mm.startAdvertising(content.getBytes());