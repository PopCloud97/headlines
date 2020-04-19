package com.popcloud.service;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.popcloud.controller.NewsController;
import com.popcloud.util.CommonUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

import static java.util.UUID.randomUUID;

@Service
public class QiniuService {
    private static final Logger logger = LoggerFactory.getLogger(QiniuService.class);
    private static String QINIU_IMAGE_DOMAIN = "http://q85oes9hc.bkt.clouddn.com/";
    //构造一个带指定 Region 对象的配置类，region1即华北机房
    Configuration cfg = new Configuration(Region.region1());
    //...其他参数参考类注释
    UploadManager uploadManager = new UploadManager(cfg);
    //...生成上传凭证，然后准备上传
    String accessKey = "XbW3br5oB3JZc7sAsU4E9E-4vg8L1oMpeXN7prEG";
    String secretKey = "X2WNiBUNJfdfMILH7rTID_Z0ag5YmiT8_mx8pRwb";
    String bucket = "headlines-toutiao";
    //String localFilePath = "D:\\qiniu\\test.png";
    //默认不指定key的情况下，以文件内容的hash值作为文件名
    String key = null;
    Auth auth = Auth.create(accessKey, secretKey);
    String upToken = auth.uploadToken(bucket);

    public String saveImage(MultipartFile file) throws IOException {
        try {
            int dotPos = Objects.requireNonNull(file.getOriginalFilename()).lastIndexOf(".");
            if (dotPos < 0) {
                return null;
            }
            String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
            if (!CommonUtil.isFileAllowed(fileExt)) {
                return null;
            }
//            String fileName = randomUUID().toString().replaceAll("-", "") + "." + fileExt;

            Response response = uploadManager.put(file.getBytes(), key, upToken);

//            解析上传成功的结果
//            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
//            System.out.println(putRet.key);
//            System.out.println(putRet.hash);

            if (response.isOK() && response.isJson()) {
                return QINIU_IMAGE_DOMAIN + JSONObject.parseObject(response.bodyString()).get("key");
            } else {
                   logger.error("七牛异常:" + response.bodyString());
                return null;
            }
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
                return null;
            } catch (QiniuException ex2) {
                return null;
            }
        }
    }
}
