package cn.ljpc.vod.service.impl;

import cn.ljpc.servicebase.exception.MyException;
import cn.ljpc.vod.service.VideoService;
import cn.ljpc.vod.util.AliyunVodSDKUtils;
import cn.ljpc.vod.util.ConstantPropertiesUtil;
import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.aliyuncs.vod.model.v20170321.DeleteVideoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author Jacker
 * @Description
 * @create 2021-06-20 17:07
 */
@Slf4j
@Service
public class VideoServiceImpl implements VideoService {

    @Override
    public String uploadVideo(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String title = originalFilename.substring(0, originalFilename.lastIndexOf("."));
            UploadStreamRequest request = new UploadStreamRequest(
                    ConstantPropertiesUtil.ACCESS_KEY_ID,
                    ConstantPropertiesUtil.ACCESS_KEY_SECRET,
                    title, originalFilename, inputStream);

            UploadVideoImpl uploader = new UploadVideoImpl();
            UploadStreamResponse response = uploader.uploadStream(request);
            //如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。
            // 其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
            String videoId = response.getVideoId();
            if (!response.isSuccess()) {
                String errorMessage = "阿里云上传错误：" + "code：" + response.getCode() + ", message：" + response.getMessage();
                log.warn(errorMessage);
                if (StringUtils.isEmpty(videoId)) {
                    throw new MyException(20001, errorMessage);
                }
            }
            return videoId;
        } catch (IOException e) {
            throw new MyException(20001, "guli vod 服务上传失败");
        }
    }

    @Override
    public void removeVideoSource(String videoId) {
        try {
            DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                    ConstantPropertiesUtil.ACCESS_KEY_ID,
                    ConstantPropertiesUtil.ACCESS_KEY_SECRET);
            DeleteVideoRequest request = new DeleteVideoRequest();
            request.setVideoIds(videoId);
            DeleteVideoResponse response = client.getAcsResponse(request);
            log.info("RequestId = " + response.getRequestId());
        } catch (Exception e) {
            throw new MyException(20001, "视频删除失败");
        }
    }

    @Override
    public void batchDelete(List<String> videoList) {
        try {
            DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(
                    ConstantPropertiesUtil.ACCESS_KEY_ID,
                    ConstantPropertiesUtil.ACCESS_KEY_SECRET);
            DeleteVideoRequest request = new DeleteVideoRequest();
            StringBuilder stringBuilder = new StringBuilder();

            for (String videoId : videoList) {
                if (!StringUtils.isEmpty(videoId)) {
                    stringBuilder.append(videoId);
                }
            }

            request.setVideoIds(stringBuilder.toString());
            DeleteVideoResponse response = client.getAcsResponse(request);
            log.info("RequestId = " + response.getRequestId());
        } catch (Exception e) {
            throw new MyException(20001, "视频删除失败");
        }
    }
}
