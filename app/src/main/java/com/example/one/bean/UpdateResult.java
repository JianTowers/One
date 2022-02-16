package com.example.one.bean;

import java.io.Serializable;

/**
 * @author :JianTao
 * @date :2022/2/15 16:28
 * @description : 版本更新:根据实际json文件定义
 */
public class UpdateResult implements Serializable {

    /**
     * Code : 0
     * Msg :
     * UpdateStatus : 1
     * VersionCode : 3
     * VersionName : 1.0.2
     * UploadTime : 2018-07-10 17:28:41
     * ModifyContent :
     1、优化api接口。
     2、添加使用demo演示。
     3、新增自定义更新服务API接口。
     4、优化更新提示界面。
     * DownloadUrl : https://xuexiangjys.oss-cn-shanghai.aliyuncs.com/apk/xupdate_demo_1.0.2.apk
     * ApkSize : 2048
     * ApkMd5 : E4B79A36EFB9F17DF7E3BB161F9BCFD8
     */

    public int Code;
    public String Msg;
    public int UpdateStatus;
    public int VersionCode;
    public String VersionName;
    public String UploadTime;
    public String ModifyContent;
    public String DownloadUrl;
    public int ApkSize;
    public String ApkMd5;
}
