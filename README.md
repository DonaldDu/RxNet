# RxNet [![](https://jitpack.io/v/DonaldDu/RxNet.svg)](https://jitpack.io/#DonaldDu/RxNet) [JitPack](https://jitpack.io/#DonaldDu/RxNet)
# Android里面网络请求一般怎么写？

## Kotlin协程
很多人说用Kotlin协程，看了后发现不太好用。

- 外面需要一个asyncUI {}，看着都不爽
- 如果接口出错了，需要在每个调用处，写逻辑来处理。不能全局处理错误，比如网络超时。
- 代码不容易懂，比如： deferred1.wait(TOAST)这个wait(TOAST)是什么东西。
- 没有全局进度框

以下代码引用于 [用 Kotlin 协程把网络请求玩出花来](https://www.jianshu.com/p/272430328b6e)
```
asyncUI {
    // 假设这是两个不同的 api 请求
    val deferred1 = bg {
        Server.getApiStore().login1("173176360", "123456").execute()
    }

    val deferred2 = bg {
        Server.getApiStore().login2("173176360", "123456").execute()
    }

    // 后台请求着 api，此时我还可以在 UI 协程中做我想做的事情
    textView.text = "loading"
    delay(5, TimeUnit.SECONDS)

    // 等 UI 协程中的事情做完了，专心等待 api 请求完成（其实 api 请求有可能已经完成了）
    // 通过提供 ExceptionHandleType 进行异常的过滤
    val response = deferred1.wait(TOAST)
    deferred2.wait(THROUGH) // deferred2 的结果我不关心

    // 此时两个请求肯定都完成了，并且 deferred1 没有异常发生
    textView.text = response.toString()
}
```
## 纯OkHttp
- 代码写起来也比较麻烦，没有提示功能。
- 不能全局处理错误，比如网络超时。
- 没有全局进度框
- 需要切换线程以操作UI
```
//代码同样来源上面引用的文章
callback = {
    onSuccess =  { res ->
        // TODO
    }

    onFail =  { error -> 
        // TODO
    }
}
request.execute(callback)
```
## 封装OkHttp
还有人自己封装OkHttp，这个就看个人了。

我觉得这个不太好用，假如有个接口调用10次，就要写10次接口地址和‘toClass<Response<Student>>()’，显然这样不好。

- 代码有点冗余（服务地址和返回参对象类）。
- ~~不能全局处理错误，比如网络超时~~（没用过，不太确定有没有）。
- ~~没有全局进度框~~（没用过，不太确定有没有）。

以下代码引用于 [RxHttp 2000+star，协程请求，仅需三步](https://juejin.im/post/6856550856796897287?utm_source=gold_browser_extension)
```
val response = RxHttp.get("/service/...")
    .toClass<Response<Student>>()
    .await()
if (response.code == 200) {
    //拿到data字段(Student)刷新UI
} else {
    //拿到msg字段给出错误提示
} 
```
# 干货
那么，有没有一种方法能
- 全局默认进度框，同时支持自定义进度框。
- 全局默认处理错误，同时支持自定义处理错误。
- 多个依次请求也能友好支持。
- 页面结束，自动取消请求。

## 有的：RxNet=RxJava3+Retrofit2+OkHttp3

下面就是调用示例。调用返回的对象是接口中申明的类型，有完整的代码提示，IDE中可以看到Hint
```
    interface API {
        @GET("Simple?net=1&bz=1")
        fun simple(): Observable<ResponsePacket<String>>
    
        @GET("NetError?net=0&bz=1")
        fun netError(): Observable<ResponsePacket<String>>
    
        @GET("BzError?net=1&bz=0")
        fun bzError(): Observable<ResponsePacket<String>>
    
        @GET("authorizeFailed")
        fun authorizeFailed(): Observable<ResponsePacket<String>>
    }

    private fun subscribeX() {
        //简单模式，通常都用这个
        api.simple().subscribeX(context) {
            Toast.makeText(context, "response:" + it.message, Toast.LENGTH_SHORT).show()
        }
        //构造方法模式，看名字就知道功能了
        api.simple().subscribeXBuilder(context)
                .progress {
                    null//null means no default and custom progress
                }.failed {
                    true//return error handled or not
                }.successOnly(true)
                .response {
                    Toast.makeText(context, "response:" + it.message, Toast.LENGTH_SHORT).show()
                }
    }
```
## 多个请求依次发起
需要额外引用
> implementation "com.github.DonaldDu:XIntent:1.5.3"//Waterfall

```
    //多个请求依次发起，不调用 next 就自动结束流程。请求间切换时，进度框不会闪烁
    buttonMultReq.setOnClickListener {
        Waterfall.flow {
            apiSample.subscribeX(context) {
                Log.i("TAG", "apiSample1")
                next()//进入下一个flow，可以带任意类型的数据如：next("DATA")
            }
        }.flow {
            apiSample.subscribeX(context) {
                Log.i("TAG", "apiSample2")
                next()
            }
        }.flow {
            apiSample.subscribeX(context) {
                Log.i("TAG", "apiSample3")
                Toast.makeText(context, "response:" + it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
```
## 延时返回结果
有时，想请求慢一点，动画需要时间展示。比如检查App更新，如果太快，屏幕会闪一下，然后显示没有更新的结果。如果延时900ms就可以解决这个问题（个人感觉好些）。
```
    buttonDelay.setOnClickListener {
        val start = System.currentTimeMillis()
        apiSample.delayResponse(5000)
                .subscribeX(context) {
                    val cost = System.currentTimeMillis() - start
                    Log.i("TAG", "apiSample cost $cost")
                }
    }
```
## 实现方式
为了支持默认进度框和全局错误处理需要实现两个类，StyledProgressGenerator 和 IErrorHandler。已经有了默认实现，需要根据实际需要作一些调整就行了。
```
class SampleStyledProgressGenerator : StyledProgressGenerator {
    override fun generate(observer: IObserverX): StyledProgress? {
        val context = observer.context
        return if (context is FragmentActivity) {
            MultListenerDialog.getInstance(context, observer)
        } else null
    }
}

class SampleErrorHandler : BaseErrorHandler() {
    override fun showDialog(context: Context, msg: String): Dialog? {
        return AlertDialog.Builder(context)
                .setMessage(msg)
                .setPositiveButton("OK", null).show()
    }

    override fun isAuthorizeFailed(activity: Activity, error: IError): Boolean {
        return error.code == 9001
    }

    override fun onLogout(context: Context) {
        val msg = "onLogout"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        Log.i(TAG, msg)
    }

    override fun isDebug(): Boolean = true

    companion object {
        private val TAG = IErrorHandler::class.java.simpleName
    }
}
```
## 进度框样式调整
默认实现的是这个类MultListenerDialog，可以参考这个自己实现。如果只是调整比较小，也可以直接创建一个同名的Layout（R.layout.net_progress_dialog）自己随意调整就好。
## 手动控制进度框
如果在接口调用以外的地方需要控制进度框，可以调用以下两个方法。

比如先压缩图片，再上传。在启动压缩前最好显示进度框，上传完成后会自动关闭进度框。还可以实现压缩过程中取消后，就不上传了。

>需要特别注意下：如果 Activity中也有同名的方法时，调用以下方法执行的是Activity中定义的，而不是下面的。

```
fun FragmentActivity.showProgress(): MultListenerDialog {
    val dialog = MultListenerDialog.getInstance(this)
    dialog.showProgress()
    return dialog
}

fun FragmentActivity.dismissProgress(delay: Boolean = true) {
    MultListenerDialog.getInstance(this).dismissProgress(delay)
}
```
## 引入依赖 [JitPack](https://jitpack.io/#DonaldDu/RxNet)
```
dependencies {
    implementation 'com.github.DonaldDu:RxNet:x.x.x'//JitPack version
}
```

# 最后
开源不易，写文章更不易，劳烦大家给本文点个赞，可以的话，再给个star，感激不尽
