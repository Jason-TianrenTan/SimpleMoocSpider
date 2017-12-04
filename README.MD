 SimpleMoocSpider
 ===========


-----By Chidori and 乱武-----

这是一个简易的Java版本MOOC网爬虫，可以爬取首页推荐课程的信息。



# 准备工作

基本架构为OkHttp3.9.1 + Gson2.8.2， 网页信息通过Fiddler 4分析

### 添加依赖
``` xml
	<dependency>
            <groupId>com.squareup.okhttp3</groupId>
            <artifactId>okhttp</artifactId>
            <version>3.9.1</version>
        </dependency>
        
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.8.2</version>
        </dependency>
```
### 安装Fiddler

下载地址https://www.telerik.com/fiddler (直接百度也可以)
打开之后界面大致如下
![image](https://github.com/Superplasma/SimpleMoocSpider/raw/master/images/fiddler1.png)


----------

# 一 网页分析
###  打开Mooc课程首页
首先打开http://www.icourse163.org/category/all
![image](https://github.com/Superplasma/SimpleMoocSpider/raw/master/images/mooc1.png)
打开Fiddler 4, 点击Remove All
![image](https://github.com/Superplasma/SimpleMoocSpider/raw/master/images/fiddler2.png)
此时Fiddler应该是空的，回到浏览器，刷新页面；
### 分析信息
切回Fiddler界面，此时可以看到加载这个网页的全部信息：
![image](https://github.com/Superplasma/SimpleMoocSpider/raw/master/images/fiddler3.png)
我们希望抓取到的课程信息就在下方的列表中，如“高等数学（二）”“复变函数与积分变换”等；
![image](https://github.com/Superplasma/SimpleMoocSpider/raw/master/images/mooc2.png)
选择右侧列表的"JSON"选项，逐个浏览，我们发现名为“/web/j/courseBean.getMocTermStatisticListByParms.rpc?csrfKey=79741ee1fae643aebe52a8261aa84c16”的数据中含有我们需要的课程信息
![image](https://github.com/Superplasma/SimpleMoocSpider/raw/master/images/json.png)
**不过此时不要急着去做**，网页很重要的东西不能忘了---Cookie；这里我们看到请求该网页需要很多Cookies，并且请求表单需要一个"csrfKey"的参数
![image](https://github.com/Superplasma/SimpleMoocSpider/raw/master/images/cookies1.png)
![image](https://github.com/Superplasma/SimpleMoocSpider/raw/master/images/webform.png)
这些信息应该可以在初始网页返回的数据中找到，于是我们回到"/category/all"中：
![image](https://github.com/Superplasma/SimpleMoocSpider/raw/master/images/cookies2.png)
果然我们需要的Cookies就在这里了。另外有意思的是，MOOC的csrf竟然和NTESSTUDYSI这条属性是一样的，降低了我们的代码实现难度。于是只需要先请求一次"/category/all"，获取到Cookies和csrfKey，之后带着这些值去请求对应的"/courseBean...." URL即可.




# 二 代码实现

我们需要做的无非是模拟Http请求，获取服务器返回的数据
### 首先是请求获取csrf

    
``` java
String url_ = "http://www.icourse163.org/category/all";
        client = new OkHttpClient.Builder()
        //这里添加一个CookieJar用于让程序帮我们自动管理Cookies
                .cookieJar(new CookieJar() {
                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);
                        for (Cookie cookie : cookies) {
                            if (csrf == null)
                                csrf = cookie.value();
                                //获取csrfKey的值
                        }
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .build();
        Request request = new Request.Builder()
                .url(url_)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
```
### 请求课程数据
```java
    public String requestInfo() {
        RequestBody body = new FormBody.Builder()
                .add("csrfKey", csrf)
                .build();
        Request request = new Request.Builder()
                .url(host + csrf)
                //注意地址中含有第一步中的csrf
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
```
### 解析Json数据，这里使用Google的Gson解析库
如果没有使用过Gson，通过Maven添加依赖之后，在IDEA点开File-->Settings-->Plugins，在Browse repositories中搜索"GsonFormat"，点击安装（由于我已经安装过了所以右侧没有那个绿色的Install）
![Alt text](1512318408493.png)
新建一个类，右键-->Generate-->GsonFormat
![Alt text](./1512318487494.png)
将之前的Json全部复制到文本框中，点击"OK"
![Alt text](1512318552769.png)
可以看到，IDEA帮我们自动生成了Json数据对应的类，每个属性对应Json中的属性
![Alt text](1512318610894.png)
**这里建议不要改变量名字了，毕竟是一一对应的，如果擅自修改会改出问题**如果必须要修改变量名，请务必上面加上@SerializedName(原Json属性名字)

最后一步解析很简单，直接调用Gson的方法即可，传入之前创建的类
```java
    private void decode(String str) {
        Gson gson = new Gson();
        GsonProcessor processor = gson.fromJson(str, GsonProcessor.class);
        ArrayList<ResultBean> beans = (ArrayList<ResultBean>) processor.getResult();
        for (ResultBean bean : beans) {
            System.out.println("课程id : " + bean.getCourseId());
            System.out.println("课程名字 : " + bean.getCourseName());
            System.out.println("学校 : " + bean.getSchoolName() + " (" + bean.getSchoolShortName() + ")");
            System.out.println("学期id : " + bean.getTermId());
            System.out.println("已选人数 : " + bean.getEnrollCount());
            System.out.println("图像url : " + bean.getImgUrl());
            System.out.println();
        }
    }
```
## 预览结果
![Alt text](1512318882567.png)



## 反馈与建议
- Github地址：<https://github.com/Superplasma>

---------
