RetrofitRxUtil [![RetrofitRxUtil](https://jitpack.io/v/DonaldDu/RetrofitRxUtil.svg)](https://jitpack.io/#DonaldDu/RetrofitRxUtil)

App Demo
```
public class App extends Application {
    public static API api;
    public IDisposableHandler disposableHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        disposableHandler = new SimpleDisposableHandler();
        ObserverWithBZ.setDefaultStyledProgressGenerator(new SampleStyledProgressGenerator());
        ObserverWithBZ.setDefaultErrorHandler(new SampleErrorHandler());
    }
}
```

BaseActivity Demo
```
public abstract class BaseActivity extends AppCompatActivity implements IDisposable {
    private IDisposableHandler disposableHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposableHandler = App.getInstance(this).disposableHandler;
    }

    @Override
    public void registerDisposable(@NonNull Context context, @NonNull Disposable disposable) {
        disposableHandler.registerDisposable(context, disposable);
    }

    @Override
    public void onComplete(Context context, @NonNull Disposable disposable) {
        disposableHandler.onComplete(context, disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposableHandler.onDestroy(this);
    }
}
```

MainActivity中带默认错误处理和进度框的调用
```
    api.simple()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ObserverWithBZ<ResponsePacket<String>>(context) {
                override fun onResponse(ResponsePacket<String> response) {
                    Toast.makeText(context, "response:" + response.message, Toast.LENGTH_SHORT).show()
                }
            });
```

ObserverWithBZ中常用方法说明
```
public abstract class ObserverWithBZ<T> implements Observer<T> {
 
    public ObserverWithBZ(@Nullable Context context) {
        this(context, true, true);
    }

    /**
     * @param autoDismiss autoDismissProgress
     */
    public ObserverWithBZ(@Nullable Context context, boolean successOnly, boolean autoDismiss) 

    /**
     * Override this for custom progress
     */
    @Nullable
    protected StyledProgress getStyledProgress() {
        return defaultStyledProgressGenerator.generate(this);
    }

    /**
     * Override this for custom ErrorHandler
     */
    @NonNull
    protected IErrorHandler getErrorHandler() {
        return defaultErrorHandler;
    }
```
更多信息，请参考Demo

