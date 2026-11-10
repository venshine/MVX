# MVX

[English](README.md)

一个界面，三种架构。`MVC/`、`MVP/`、`MVVM/` 是三个互相独立的 Kotlin 演示 App，**业务和界面完全一样**（模拟天气查询：输入城市，点查询，先加载中，再显示结果或错误），方便并排对照阅读。它们是演示 App，不是库。

- 版本 2.0.0，minSdk 23，targetSdk 36，Kotlin，协程，AppCompat DayNight（不使用 Material Components）。
- 包名：`com.wx.mvc`、`com.wx.mvp`、`com.wx.mvvm`。
- 数据**纯模拟**：协程 `delay`（2 秒）后，城市为空（或打开「模拟失败」开关）则失败，否则返回固定数据，不接真实网络接口。

[**下载示例 APK**](https://github.com/venshine/MVX/releases/tag/2.0.0)（MVC、MVP、MVVM 各一个）

## 截图

<img src="screenshot/demo.gif" width="300" alt="MVX demo" />

| | 首页 | 首页（深色） | 成功 | 失败 | 成功（深色） |
|:---:|:---:|:---:|:---:|:---:|:---:|
| MVC | <img src="screenshot/mvc_home.png" width="200" /> | <img src="screenshot/mvc_home_dark.png" width="200" /> | <img src="screenshot/mvc_weather.png" width="200" /> | <img src="screenshot/mvc_weather_error.png" width="200" /> | <img src="screenshot/mvc_weather_dark.png" width="200" /> |
| MVP | <img src="screenshot/mvp_home.png" width="200" /> | <img src="screenshot/mvp_home_dark.png" width="200" /> | <img src="screenshot/mvp_weather.png" width="200" /> | <img src="screenshot/mvp_weather_error.png" width="200" /> | <img src="screenshot/mvp_weather_dark.png" width="200" /> |
| MVVM | <img src="screenshot/mvvm_home.png" width="200" /> | <img src="screenshot/mvvm_home_dark.png" width="200" /> | <img src="screenshot/mvvm_weather.png" width="200" /> | <img src="screenshot/mvvm_weather_error.png" width="200" /> | <img src="screenshot/mvvm_weather_dark.png" width="200" /> |

## 同一业务的三种写法

| | MVC | MVP | MVVM |
|---|---|---|---|
| View | 布局 XML + Activity | `WeatherActivity` 实现 `WeatherView` 接口 | `WeatherActivity`，按 `WeatherUiState` 渲染 |
| 谁处理点击 | Activity（它同时是 Controller） | Activity 转发给 `Presenter.getWeather()` | Activity 转发给 `ViewModel.query()` |
| 谁调用 Model | Activity，在 `lifecycleScope` 里 | `WeatherPresenter` | `WeatherViewModel`，在 `viewModelScope` 里 |
| 谁更新界面 | Activity 直接改 | Presenter 调 `WeatherView.showXxx()` | Activity 收集 `StateFlow<WeatherUiState>` 后渲染 |
| 是否依赖 Android | 全部依赖 | Presenter 不依赖（没有 `Context`/`View`） | ViewModel 不持有 View/Context |
| 视图绑定 | `findViewById` | `findViewById` | ViewBinding |
| 旋转后保留状态 | 否（状态在 View 里） | 否（Presenter 随 Activity 重建） | 是（ViewModel 被保留） |
| 生命周期处理 | `lifecycleScope` 销毁时自动取消 | `attach()` / `detach()` / `destroy()` 取消请求并释放 View | `viewModelScope` 清理时取消；界面用 `repeatOnLifecycle(STARTED)` 收集 |
| 怎么测 | Robolectric | 纯 JVM：假 View + 假 Model + `kotlinx-coroutines-test` | 纯 JVM：假 Model + `kotlinx-coroutines-test` |

数据流向：

```
MVC   点击 -> Activity -> Model            Model -> Activity -> 各个 View
MVP   点击 -> Activity -> Presenter -> Model        Model -> Presenter -> View 接口 -> Activity
MVVM  点击 -> Activity -> ViewModel -> Model        Model -> ViewModel -> StateFlow(UiState) -> Activity 渲染
```

三个模块共用同一套 Model 层写法：`WeatherModel`（返回 `Result<WeatherInfo>` 的 `suspend` 接口）、`WeatherModelImpl(delayMillis = 2000)`（模拟数据源，测试里可换延迟或换成假实现）、数据类 `WeatherInfo`。

### 各自适用场景

- **MVC**：极小的页面或一次性原型。成本低，但 Activity 很快膨胀，也难测。
- **MVP**：想在纯 JVM 上测业务逻辑又暂不迁到 Jetpack，或维护已有的 MVP 代码。attach / detach 和旋转后的状态要自己处理。
- **MVVM**（`ViewModel` + `StateFlow`）：新代码的默认选择。状态在配置变化后保留，界面是单一状态对象的函数。

## 与官方推荐架构的关系

官方指南推荐：关注点分离、用数据模型驱动 UI、单一数据源、**单向数据流**（状态向下流，事件向上流），UI 层由 UI 元素和 `ViewModel` 等状态持有者组成，另有数据层。[Guide to app architecture](https://developer.android.com/topic/architecture) 该页面没有提到 MVC、MVP、MVVM 这些名字（已对照原文），讲的是原则而不是模式名称。

- MVVM 模块最接近官方推荐：`WeatherViewModel` 是状态持有者，对外暴露不可变的 `StateFlow<WeatherUiState>`，View 只发事件（`query`）并渲染状态。[ViewModel overview](https://developer.android.com/topic/libraries/architecture/viewmodel) 写明 ViewModel 会缓存状态并在配置变化后保留，且不应持有 `Context` 或 `Resources`。
- 指南还建议除应用组件外，其他类尽量少依赖 Android 类，这正是 MVP 里 Presenter 的做法；指南反对把所有代码写进 `Activity`，这正是 MVC 的弱点。
- 本演示省略的部分：Repository / 数据层和依赖注入（指南推荐 Hilt）；这里的 Model 只有一个构造器传入的模拟数据源。指南推荐用 Jetpack Compose 写 UI，本演示保留 View 体系，为了和 1.x 版本保持可比。

## MVVM 为什么用 ViewBinding 而不是 DataBinding

1.x 的 MVVM 模块用 DataBinding（XML 表达式 + `BaseObservable`），2.0 改用 ViewBinding：

- `ViewModel` + `StateFlow` 的写法不需要布局变量和表达式，Activity 渲染一个状态对象即可。ViewBinding 不支持布局变量、布局表达式和双向绑定，本演示都用不到。[View binding](https://developer.android.com/topic/libraries/view-binding)
- ViewBinding 编译更快（无需注解处理），相比 `findViewById` 类型安全、空安全（同页）。该页也说明高级布局仍可使用 DataBinding，所以这是本演示的取舍，不是说 DataBinding 被移除。
- 逻辑不写进 XML，三个模块的布局才能完全一致，这是对照阅读的前提。

## 构建与测试

```bash
./gradlew testDebugUnitTest assembleDebug lintDebug
```

- MVP 的 `WeatherPresenterTest`、MVVM 的 `WeatherViewModelTest` 是纯 JVM 测试（加载中、成功、失败的状态序列；MVP 还覆盖解绑和取消上一次查询）。
- 三个 App 各有 Robolectric 冒烟测试（首页和天气页的深浅色启动、主题切换、加载中、成功、城市为空和模拟失败）。

## 从 1.x 迁移

- Java 改 Kotlin；回调 `OnWeatherListener` 改为 `suspend` + `Result`；`Handler.postDelayed` 改为 `delay`。
- MVVM：移除 DataBinding，`BaseObservable` 实体改为不可变的 `WeatherInfo` + `StateFlow`。
- 成功 / 失败的 Toast 改成屏幕上的状态文字（更好测试，也避开一次性事件的问题）。
- `settings.gradle` 不再引用不存在的 `:app` 模块；各模块的 README 已删除，统一看这份。

## 维护状态

作为小型教学演示低频维护。三个 App 已在真机（小米 2509FPN0BC，Android 17）上手动检查：首页、深浅色切换、查询成功、「模拟失败」及上图各页；空城市报错和旋转未在真机上检查（仅有单元测试覆盖）。

## 致谢

三个 App 内置以下字体，均采用 [SIL Open Font License 1.1](https://openfontlicense.org)（协议原文见 [`MVC/licenses/`](MVC/licenses)、[`MVP/licenses/`](MVP/licenses)、[`MVVM/licenses/`](MVVM/licenses)）：
* [Space Grotesk](https://github.com/floriankarsten/space-grotesk)：Copyright 2020 The Space Grotesk Project Authors
* [JetBrains Mono](https://github.com/JetBrains/JetBrainsMono)：Copyright 2020 The JetBrains Mono Project Authors

所有图标均为自绘矢量，不使用位图素材。

## 许可证

Apache License 2.0，见 [LICENSE](LICENSE)。
