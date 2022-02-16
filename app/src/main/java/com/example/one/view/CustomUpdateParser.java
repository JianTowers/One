package com.example.one.view;

import com.example.one.bean.UpdateResult;
import com.xuexiang.xupdate.entity.UpdateEntity;
import com.xuexiang.xupdate.listener.IUpdateParseCallback;
import com.xuexiang.xupdate.proxy.IUpdateParser;
import com.xuexiang.xutil.net.JsonUtil;

/**
 * @author :JianTao
 * @date :2022/2/15 16:21
 * @description : 自定义版本解析
 */
public class CustomUpdateParser implements IUpdateParser {
    @Override
    public UpdateEntity parseJson(String json) throws Exception {
        return getParseResult(json);
    }

    private UpdateEntity getParseResult(String json) {
        UpdateResult result = JsonUtil.fromJson(json, UpdateResult.class);
        if (result != null) {
            return new UpdateEntity()
                    .setHasUpdate(true)
                    .setVersionCode(result.VersionCode)
                    .setVersionName(result.VersionName)
                    .setUpdateContent(result.ModifyContent)
                    .setDownloadUrl(result.DownloadUrl)
                    .setSize(result.ApkSize)
                    .setMd5(result.ApkMd5);
        }
        return null;
    }

    @Override
    public void parseJson(String json, IUpdateParseCallback callback) throws Exception {
        callback.onParseResult(getParseResult(json));
    }

    @Override
    public boolean isAsyncParser() {
        return false;
    }
}
