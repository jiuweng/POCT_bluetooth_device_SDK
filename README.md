# POCT蓝牙检测设备通信SDK Demo
## 一. 目的用途
* 用于安卓客户端与血糖仪，尿酸仪等POCT蓝牙检测设备间数据通信
* 方便互联网/物联网厂商快速将检测设备到自己的平台，无需关注设备指令交互，解析等复杂繁琐的过程，专注于业务开发
* 目前接口仅支持[**艾科**](http://www.acondiabetescare.cn/)产品（包含蓝牙血糖仪，精益佳Plus血糖尿酸测试仪，益优2血糖血酮测试仪，糖化血红蛋白分析仪等内置蓝牙设备，以及小牙蓝牙适配器支持的多款血糖设备），后续陆续接入其它设备厂商的产品 
<!-- ## [Demo下载](http://mc.aconcloud.cn/app/deviceDemo.apk)
&emsp;&emsp;&emsp;&emsp;<img src="https://img-blog.csdnimg.cn/685a23ae3b3a41c4a13da11a8af3b1ae.png#pic_center" width="100" height="100" alt="扫码下载"/> -->
## 二. 去 [github](https://github.com/zjfjf/POCT_bluetooth_device_SDK) 查看Demo
&emsp;&emsp; 在根目录apk文件下
## 三. 操作流程
### &emsp;&emsp; * $\color{#FF0000}{设备要求}$ 
&emsp;&emsp;&emsp;&emsp;  [$\color{#22B14C}{√}$ ] android 4.3及以上操作系统
&emsp;&emsp;&emsp;&emsp;  [$\color{#22B14C}{√}$ ]  蓝牙版本4.0及以上
&emsp;&emsp;&emsp;&emsp;   [$\color{#22B14C}{√}$ ] 支持ble操作接口

### &emsp;&emsp;1. 下载安装demo
#### &emsp;&emsp;&emsp;&emsp; 1.1 安装文件
&emsp;&emsp;&emsp;&emsp;<img src="https://img-blog.csdnimg.cn/ed618c1e4e3d48af93b0a92893e8ef03.png#pic_center" width="100" height="124" alt="安装文件"/>

#### &emsp;&emsp;&emsp;&emsp; 1.2 安装APP
&emsp;&emsp;&emsp;&emsp;<img src="https://img-blog.csdnimg.cn/bd2118f3a917498180bf7a30b94a95bd.png#pic_center" width="215" height="74" alt="安装APP"/>

#### &emsp;&emsp;&emsp;&emsp; 1.3 $\color{#FF0000}{授权APP}$ 
&emsp;&emsp;&emsp;&emsp;<img src="https://img-blog.csdnimg.cn/314b15c0d780499aabd500a78b26a63a.png#pic_center" width="215" height="459" alt="授权APP"/>

### &emsp;&emsp;2. 准备检测
#### &emsp;&emsp;&emsp;&emsp; 2.1 设备准备
&emsp;&emsp;&emsp;&emsp;<img src="https://img-blog.csdnimg.cn/2ed4a193005b4ad38aa45f0e31f6e167.png#pic_center" width="100" height="100" alt="设备准备"/>

#### &emsp;&emsp;&emsp;&emsp; 2.2 APP操作环境准备
&emsp;&emsp;&emsp;&emsp;<img src="https://img-blog.csdnimg.cn/43849b1c856e426985eda32c543e0115.png#pic_center" width="200" height="262" alt="APP操作环境准备"/>

#### &emsp;&emsp;&emsp;&emsp; 2.3 设备插入测试条开始检测

### &emsp;&emsp;3. 检测完成后设备显示测量值。app选择操作的设备，点击【开始】按钮，自动搜索，连接设备并完成数据接收，将过程日志显示出来，使用者可以了解到app接口功能运行过程
#### &emsp;&emsp;&emsp;&emsp; $\color{#FF0000}{APP操作}$ 
&emsp;&emsp;&emsp;&emsp;<img src="https://img-blog.csdnimg.cn/5472ae1e050f4aefadad4c2b1535041a.png#pic_center" width="200" height="237" alt="APP操作"/>

### &emsp;&emsp;* 其它设备操作方法类似
## 四. 接入流程
### &emsp;&emsp;1. 安卓项目添加maven地址
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
### &emsp;&emsp;2. 模块中添加依赖
```
dependencies {
      implementation 'com.github.zjfjf:bluetooth_device_toolkit:bluetoothdevicetoolkitcore-v1.0.+'
}
```
### &emsp;&emsp;3. 使用库中设备交互接口方法
#### &emsp;&emsp;&emsp;&emsp; * $\color{#FF0000}{注意调用设备操作方法前，需要确保需要使用的权限,操作环境均正常}$ 
```
private boolean checkEnvironment() {
    switch (Environment.check(this)){
        case PermissionsForbidden:
            //没有操作权限！！！请在应用程序权限管理设置中打开app相关操作权限
            return false;
        case BluetoothNotOpen:
            //请打开蓝牙！！！
            return false;
        case LocationNotOpen:
            //请打开定位！！！
            return false;
    }
    return true;
}
```
```
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == Environment.PermissionCode) {
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults.length > 0 && grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                Environment.checkPermissions(this);
                return;
            }
        }
        ininDevices();
    }
}
```
#### &emsp;&emsp;&emsp;&emsp; 3.1 初始化
```
private void ininDevices() {
    DeviceActionManager.getInstance().init(getApplicationContext());
    DeviceActionManager.getInstance().setOnGetRecordListener(result -> {
        log("接收数据"+result.toString());
    });
    DeviceActionManager.getInstance().switch2Ogm112Auto();
}
```
#### &emsp;&emsp;&emsp;&emsp; 3.2 结果回调数据结构说明
```
public Record onCallRecord;             //检测记录值
public DetectItemType detectItemType;   //检测类型
public String unit;                     //单位
public String convertValue;             //不同单位换算后的值
```
```
public String value; //检测值
public String time;  //检测时间
```
```
public enum DetectItemType {
    GLU,   //血糖
    UA,    //尿酸
    KET,   //血酮
    HBA1C  //糖化血红蛋白
}
```
#### &emsp;&emsp;&emsp;&emsp; 3.3 可以切换要操作的设备
```
DeviceActionManager.getInstance().switch2Ogm202();
```
#### &emsp;&emsp;&emsp;&emsp; 3.4 可以调用扫描设备方法（搜索哪个设备取决于3.3调用的切换设备接口）
```
DeviceActionManager.getInstance().scan(device->{
    //找到设备
});
```
#### &emsp;&emsp;&emsp;&emsp; 3.5 可以调用连接设备方法（连接哪个设备取决于3.3调用的切换设备接口）
```
DeviceActionManager.getInstance().getCurrentDeviceAction().connect(device.getAddress(), connectResult -> {
    //连接结果
});
```
#### &emsp;&emsp;&emsp;&emsp; 3.6 连接回调数据结构说明
```
public ErrorType error; //错误类型（Succeed：连接成功；DeviceClosed：设备连接断开）
public String message;  //说明信息
public T data;          //实际结果数据内容（true：连接成功；false：连接失败）
public String tag;      //辅助信息
```
### &emsp;&emsp;4. 其它接口方法说明后续补充