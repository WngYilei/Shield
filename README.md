[![](https://jitpack.io/v/WngYilei/Shield.svg)](https://jitpack.io/#WngYilei/Shield)
# Shield

通过字节码插桩的方式，实现的方法防抖方案。

## 使用：

1.在项目根目录的`build.gradle` 中引入插件

```groovy
 classpath 'com.github.WngYilei.Shield:shieldplugin:1.0.9'
```

2.在需要使用的moudle 中应用插件：

```groovy
apply plugin: 'shield-plugin'
```

3.添加工具依赖

```groovy
implementation 'com.github.WngYilei.Shield:shieldlib:1.0.9'
```

4.在需要进行防抖的方法上面加入注解：

```kotlin
@ShieldAnnotation(key = "test",time = 200)
fun test(){

}
```

 `key`是用来识别方法的唯一`key`值，`time` 是防抖时间，规定时间内不可以重复执行。

## 联系方式：

wangyilei0318@gmail.com

