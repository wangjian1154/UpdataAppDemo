### 版本更新需要注意的点

- **兼容Android7.0**
> 最近我们发现Android7.0的手机更新App的时候会出android.os.FileUriExposedException这个异常，出现这个异常是因为7.0的系统更改了文件系统访问权限。这无疑对我们开发者增加了麻烦~~~，若要在应用间共享文件，需要发送content://格式的URI，并授予 URI 临时访问权限。而进行此授权的最简单方式是使用 FileProvider类。下面就这个问题我们对Android更新apk进行一下整理。

- **兼容Android8.0**
>因为8.0添加了新的安全措施，不允许应用内安装未经过Google play验证的应用,所以添加下面这个权限即可解决问题。

  ```java
  <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
  ```


[操作详情](https://www.jianshu.com/p/40b7b9f00700)
