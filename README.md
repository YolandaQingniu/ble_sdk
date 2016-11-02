# 蓝牙SDK，适用于伊欧乐公司下绝大部分的智能秤

## 版本
1. `2.2`解决某些情况下，安卓可能显示指标不完整的bug，提高appid的校验成功率
* `2.1` 增加了几款新型号，提高识别轻牛设备的成功率
* `2.0` 增加了蓝牙扫描所返回的设备的型号名称

## Demo

### 安卓

Android Studio 工程，如果需要使用Eclipse 请自行新建工程，并拷贝相关源文件到工程

onCompete方法的返回值说明
```java
/**
 * 执行成功
 */
int QN_SUCCESS = 0;
/**
 * APPID失效
 */
int QN_UNAVAILABLE_APP_ID = 1;
/**
 * 网络没开
 */
int QN_NETWORK_CLOSED = 2;
/**
 * 网络超时
 */
int QN_NETWORK_TIMEOUT = 3;
/**
 * 没有底功耗蓝牙(蓝牙4.0及以上)
 */
int QN_NO_BLE = 4;
/**
 * 蓝牙错误
 */
int QN_BLE_ERROR = 5;
/**
 * 蓝牙版本太低
 */
int QN_BLE_LOW_VERSION = 6;
/**
 * 蓝牙未开启
 */
int QN_BLE_CLOSED = 7;

/**
 * SDK的版本过低
 */
int QN_BLE_LOW_SDK_VERSION = 8;
```

1. 把Demo导入到 AS 后，请拷贝最新的SDK jar包和so文到lib目录
  * jar包文件名为 qn-ble-api-x.x.jar
  * so文件名为 libyolanda_calc.so,SDK 提供8种CPU架构的so库，可根据自己的项目情况选择
* 在 Application种初始化轻牛的SDK
```java
/**
 * 初始化轻牛SDK,仅在Application中的 onCreate中调用，保证每次app实例都只调用一次。调用这个方法时，尽量要联网
 *
 * @param  AppId 由轻牛所分配的 appId
 * @param  isRelease 是否为开发模式，开发时清设置 false，上线时需要设置为true
 * @param  callback 执行结果的回调,轻牛会尽量保证各种情况都会进行回调
 */
 QNApiManager.getApi(getApplicationContext()).initSDK("123456789", false, new QNResultCallback() {
      @Override
      public void onCompete(int errorCode) {
          //执行结果，为0则成功，其它则参考api文档的种的错误码
      }
});
```
3. 调用 startLeScan 启动蓝牙扫描
```java
/**
 * @param deviceName 蓝牙设备的蓝牙名，如果不为空则扫描只扫描指定蓝牙名的设备，为空则不限定
 * @param mac        蓝牙设备的mac地址，如果不为空则扫描只扫描指定mac地址的设备，为空则不限定
 * @param callback   扫描到蓝牙设备后回调的接口
 **/
QNApiManager.getApi(this).startLeScan(null,null,new new QNBleScanCallback() { 
  //如果失败，会在这个方法中返回错误码
  public void onCompete(int errorCode) { 
  }
  //如果扫描到设备，会在这个方法返回这个设备的相关信息,一个设备只返回一次
  public void onScan(QNBleDevice bleDevice) {
  }
});
```
4. 连接扫描到的回调设备QNBleDevice
```java
/**
 * 连接指定的设备，所有的数据或连接状态都会在QNBleCall种进行回调。除了onComplete方法外，，其它的都会在主线程进行回调
 *
 * @param bleDevice 扫描回调接口中的蓝牙设备
 * @param userId    用户标识，用户唯一，传非空的字符串，可以使用 用户名，手机号，邮箱等其它标识
 * @param height    身高，单位cm
 * @param gender    性别 男：1 女：0
 * @param birthday  生日，精确到天
 * @param callback  称重过程的回调接口
 */
QNApiManager.getApi(this).connectDevice(device, "userId", 170, 1, birthday, new new QNBleCallback() {
    /**
     * 开始连接 在主线程中回调
     *
     * @param bleDevice 轻牛蓝牙设备
     */
    void onConnectStart(QNBleDevice bleDevice);

    /**
     * 已经连接上了 在主线程中回调
     *
     * @param bleDevice 轻牛蓝牙设备
     */
    void onConnected(QNBleDevice bleDevice);

    /**
     * 断开了蓝牙连接 在主线程中回调
     *
     * @param bleDevice 轻牛蓝牙设备
     */
    void onDisconnected(QNBleDevice bleDevice);

    /**
     * 收到了不稳定的体重数据，在称重前期会不断被调用 在主线程中回调
     *
     * @param bleDevice 轻牛蓝牙设备
     * @param weight    不稳定的体重
     */
    void onUnsteadyWeight(QNBleDevice bleDevice, float weight);

    /**
     * 收到了稳定的测量数据 在主线程中回调
     *
     * @param bleDevice 轻牛蓝牙设备
     * @param data      轻牛测量数据
     */
    void onReceivedData(QNBleDevice bleDevice, QNData data);

    /**
     * 收到了存储数据 在主线程中回调
     *
     * @param bleDevice 轻牛蓝牙设备
     * @param datas     存储数据数组（包含多个），可用{@link QNData#getUserId()}判断是哪个用户的数据
     */
    void onReceivedStoreData(QNBleDevice bleDevice, List<QNData> datas);
});
```
### IOS

#### 文件说明
* libQingNiuSDK.a 包含SDK的头文件定义和具体实现。
* QingNiuDevice.h是设备类，QingNiuUser.h是用户类，QingNiuSDK.h包含整个测量过程的所有方法。

#### 集成过程
1. 将SDK中的libQingNiuSDK.a和.h文件拷贝到应用开发的目录下。然后添加到工程中即可。
* 添加SDK依赖的系统库文件。分别是 CoreBluetooth.framework,SystemConfiguration.framework,CoreGraphics.Framework,libsqlite3.dylib,MobileCoreServices.framework。
* 修改必要的工程配置属性，在工程配置中的“Build Settings”一栏中找到“Linking”配置区，给“Other Linker Flags”配置项添加属性值“-ObjC”， 如果，某些 Xcode 版本中，出现问题，修改设置为 -all_load，
如果工程已经如此配置则不需重复，若没有，请务必按照步骤配置，否则会影响SDK使用。

#### 使用方法
1.registerApp,在使用其它的方法之前，请先调用此方法，并请确保第一次调用时网络畅通(可在登录的时候调用)。每次验证都会有一个过期时间，过期时间到了之后会无法扫描，请重新调用该方法。所以建议每次在didFinishLaunchingWithOptions 方法都调用该方法
```objective-c
  //注册轻牛APP
    [QingNiuSDK registerApp:@"123456789" andReleaseModeFlag:NO registerAppBlock:^(QingNiuRegisterAppState qingNiuRegisterAppState) {
        NSLog(@"%ld",(long)qingNiuRegisterAppState);
    }];
```
#### 枚举说明
* 验证信息
```objective-c
typedef NS_ENUM(NSInteger,QingNiuRegisterAppState) {//验证appid状态
  QingNiuRegisterAppStateSuccess = 0,           //成功
  QingNiuRegisterAppStateFailParamsError = 1,   //appid错误，确保appid正确再调用一次
  QingNiuRegisterAppStateFailVersionTooLow = 2, //版本号过低或过高，需要新的SDK请联系客服
};
```
* 扫描设备失败的原因
```objective-c
typedef NS_ENUM(NSInteger,QingNiuScanDeviceFail) { 
  QingNiuScanDeviceFailUnsupported = 0,       //设备不支持蓝牙4.0
  QingNiuScanDeviceFailPoweredOff = 1,        //蓝牙关闭(打开手机蓝牙)
  QingNiuScanDeviceFailValidationFailure = 2, //app验证失败(重新调用registerApp接口) 
  QingNiuScanDevicePoweredOn = 3,             //蓝牙开启(这不是扫描失败情况下的枚举，为了跟以前的版本兼容，不另外添加枚举)
};
```
* 连接过程中的各种状态枚举(0-4是代表连接过程中失败的枚举，5-10代表连接过程中成功的枚举)
```objective-c
typedef NS_ENUM(NSInteger,QingNiuDeviceConnectState) { 
  QingNiuDeviceConnectStateParamsError = 0,       //传入连接参数错误(这里有可能出现的参数错误包括qingNiuUser，qingNiuDevice，出现这种错误要重新扫描再连接)
  QingNiuDeviceConnectStateConnectFail = 1,       //连接设备失败(重新连接或重新扫描再连接)
  QingNiuDeviceConnectStateDiscoverFail = 2,      //查找设备的服务或者特性失败(重新连接)
  QingNiuDeviceConnectStateDataError = 3,         //接收到的数据出错(重新连接)
  QingNiuDeviceConnectStateLowPower = 4,          //设备低电(设备电量不足)
  QingNiuDeviceConnectStateIsWeighting = 5,       //正在测量(接收实时数据)
  QingNiuDeviceConnectStateWeightOver = 6,        //测量完毕(接收正常测量的数据)
  QingNiuDeviceConnectStateIsGettingSavedData= 7, //正在接收存储数据(接收设备存储的数据)
  QingNiuDeviceConnectStateGetSavedDataOver＝8,   //接收到了所有存储数据(此时deviceData的值为nil)
  QingNiuDeviceConnectStateDisConnected = 9,      //测量完毕之后自动断开了连接(此时deviceData为nil)
  QingNiuDeviceConnectStateConnectedSuccess = 10, //连接成功时候的回调(此时deviceData为nil)
};
```
* 断开连接的状态
```objective-c
typedef NS_ENUM(NSInteger,QingNiuDeviceDisconnectState) {//断开连接的各种状态
  QingNiuDeviceDisconnectStateDisConnectSuccess = 0,  //断开连接成功
  QingNiuDeviceDisconnectStateParamsError = 1,        //传入连接参数错误(比如外设为空)
  QingNiuDeviceDisconnectStateIsDisConnected = 2,     //已经处于断开的状态
};
```


## 注意事项

* 测试版 APPID：123456789，测试版版本的服务器可能会不稳定，
* 使用`测试版APPID`调试成功后，请切换到`发布`模式，并使用`正式的APPID`上线
* SDK中有方法可以指定测试或发布模式

=======================
如有问题，请mail: huangdunren@yolanda.hk   huangwenhai@yolanda.hk
或者直接在QQ，微信的SDK技术讨论组中咨询
