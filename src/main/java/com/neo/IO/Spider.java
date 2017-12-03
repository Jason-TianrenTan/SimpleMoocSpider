package com.neo.IO;

import com.google.gson.*;
import com.neo.Model.GsonProcessor;

import com.neo.Model.GsonProcessor.ResultBean;

import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Spider {

    private OkHttpClient client;
    private String csrf = null;
    private String host = "http://www.icourse163.org/web/j/courseBean.getMocTermStatisticListByParms.rpc?csrfKey=";

    /**
     * code : 0
     * result : [{"courseId":217012,"courseName":"高等数学（二）","schoolShortName":"TONGJI","termId":1002321010,"enrollCount":62716,"imgUrl":"http://img1.ph.126.net/S_KZHbKd1cVnNakzi7s3nw==/39125021863241218.png","schoolName":"同济大学"},{"courseId":199001,"courseName":"程序设计入门\u2014\u2014C语言","schoolShortName":"ZJU","termId":1002303013,"enrollCount":81886,"imgUrl":"http://img1.ph.126.net/tYhzuDVilzlDOo2bEyH_Qg==/6608226511143817333.jpg","schoolName":"浙江大学"},{"courseId":53004,"courseName":"高等数学（一）","schoolShortName":"TONGJI","termId":1002321008,"enrollCount":218941,"imgUrl":"http://edu-image.nosdn.127.net/65B575B91765DBCD593C825AB376F329.jpeg?imageView&thumbnail=426y240&quality=100","schoolName":"同济大学"},{"courseId":1002338005,"courseName":"中医养生与亚健康防治","schoolShortName":"JNU","termId":1002454011,"enrollCount":9394,"imgUrl":"http://edu-image.nosdn.127.net/1E7F5DCD4C94DEC2A0F14C282EA22452.jpg?imageView&thumbnail=510y288&quality=100","schoolName":"暨南大学"},{"courseId":268001,"courseName":"Python语言程序设计","schoolShortName":"BIT","termId":1002235009,"enrollCount":62314,"imgUrl":"http://img2.ph.126.net/QB-Ic9eSEpzrDVnaNF3MtA==/6631441599652615616.jpg","schoolName":"北京理工大学"},{"courseId":47004,"courseName":"大学计算机","schoolShortName":"BIT","termId":1002252005,"enrollCount":79969,"imgUrl":"http://edu-image.nosdn.127.net/54BB735C0C79D653219EBEF3D9DC9A4D.jpg?imageView&thumbnail=426y240&quality=100","schoolName":"北京理工大学"},{"courseId":1002032006,"courseName":"生活心理学","schoolShortName":"HIT","termId":1002132019,"enrollCount":41289,"imgUrl":"http://edu-image.nosdn.127.net/C8C75F8294F3933FDAEB21BAC0CE7B17.jpg?imageView&thumbnail=510y288&quality=100","schoolName":"哈尔滨工业大学"},{"courseId":17004,"courseName":"大学英语（口语）","schoolShortName":"NUDT","termId":1002299019,"enrollCount":159140,"imgUrl":"http://edu-image.nosdn.127.net/E0D1AA88D1BBFF464CB773E96A082FB9.jpg?imageView&thumbnail=426y240&quality=100","schoolName":"国防科技大学"},{"courseId":94003,"courseName":"当代青年心理学(三)青年自我意识篇","schoolShortName":"SWJTU","termId":1002320011,"enrollCount":2363,"imgUrl":"http://img2.ph.126.net/z9FagnH9TKSSjS3RoxAdeg==/654992270823387908.jpg","schoolName":"西南交通大学"},{"courseId":1001973001,"courseName":"形势与政策","schoolShortName":"HEBUST","termId":1002375011,"enrollCount":26153,"imgUrl":"http://edu-image.nosdn.127.net/8943D9665928DDCBE9318D81D17F45C6.jpg?imageView&thumbnail=510y288&quality=100","schoolName":"河北科技大学"}]
     * message :
     */


    public Spider() {
    }


    public void activate() throws IOException {
        if (requestCSRF()) {
            String json = requestInfo();
            decode(json);
        }
    }


    /**
     * 获取信息
     */
    public String requestInfo() {
        RequestBody body = new FormBody.Builder()
                .add("csrfKey", csrf)
                .build();
        Request request = new Request.Builder()
                .url(host + csrf)
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

    /**
     * 获取csrf
     */
    public boolean requestCSRF() {
        String url_ = "http://www.icourse163.org/category/all";
        client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);
                        for (Cookie cookie : cookies) {
                            if (csrf == null)
                                csrf = cookie.value();
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
        return response.isSuccessful();
    }


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

}
