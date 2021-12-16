package org.fdfsfus.controller;


import org.apache.commons.lang3.StringUtils;
import org.fdfsfus.api.controller.UploadPictureControllerApi;
import org.fdfsfus.controller.share.BasicShare;
import org.fdfsfus.pojo.FileUpload;
import org.fdfsfus.result.GraceJSONResult;
import org.fdfsfus.result.ResponseStatusEnum;
import org.fdfsfus.service.UploadFileService;
import org.fdfsfus.utils.MyFileUtils;
import org.fdfsfus.utils.RedisOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 图片上传
 * @author YanZiMing
 * @DATE 2021/10/20  7:02 下午
 */
@RestController
public class UploadPictureController extends BasicShare implements UploadPictureControllerApi {
    /**
     * 日志
     */
    final static Logger logger = LoggerFactory.getLogger(UploadPictureController.class);

    private final UploadFileService uploadFileService;
    private final RedisOperator redisOperator;

    public UploadPictureController(UploadFileService uploadFileService, RedisOperator redisOperator) {
        this.uploadFileService = uploadFileService;
        this.redisOperator = redisOperator;
    }

    @Override
    public GraceJSONResult uploadIMGBase64( String faceBase64) throws Exception {
        String path = null;
        if (faceBase64 !=null) {
            //通过Base64获取后缀/ 不是图片报错
            String fileSuffix = getSubUtilSimple(faceBase64,"/(.*?);");
            String contentType = getSubUtilSimple(faceBase64,":(.*?);");
            if (!fileSuffix.equals(IMG_TYPE_JPG)||!fileSuffix.equals(IMG_TYPE_GIF)||!fileSuffix.equals(IMG_TYPE_DMG)||!fileSuffix.equals(IMG_TYPE_PNG)) {
                GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_FORMATTER_FAILD);
            }
            //这里是临时存储的文件路径
            String filePath = FACE_PATH + UUID.randomUUID() + "." + fileSuffix;
            MyFileUtils.base64ToFile(filePath, faceBase64);
            File file = new File(filePath);
            MultipartFile faceFile = MyFileUtils.fileToMultipart(file,fileSuffix,contentType);
            path  = uploadFileService.uploadIMGOne(faceFile, fileSuffix,null);
            logger.info("path={}",path);
            //删除临时文件
            MyFileUtils.delFile(filePath);
        }else {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }
        return GraceJSONResult.ok(path);
    }

    @Override
    public GraceJSONResult uploadIMG(MultipartFile file, String fileMd5) throws Exception {
        String path = null;
        if(file != null) {
            //获得文件名称
            String fileName =  file.getOriginalFilename();
            if (StringUtils.isNotBlank(fileName)){
                String[] fileNameAll = fileName.split("\\.");
                //获得后缀
                String suffix = fileNameAll[fileNameAll.length -1];
                if (!suffix.equalsIgnoreCase(IMG_TYPE_PNG)&&!suffix.equalsIgnoreCase(IMG_TYPE_JPG)&&!suffix.equalsIgnoreCase(IMG_TYPE_JPEG)) {
                    return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_FORMATTER_FAILD);
                }
                path = uploadFileService.uploadIMGOne(file,suffix,fileMd5,null);

            }
        }else {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        logger.info("path={}",path);
        return GraceJSONResult.ok(path);
    }



    @Override
    public GraceJSONResult uploadSomeIMG( MultipartFile[] files) throws Exception {
        List<String> imageList = new ArrayList<>();
        if (files != null && files.length>0) {
            for (MultipartFile file: files) {
                String path = null;
                if(file != null) {
                    //获得文件名称
                    String fileName =  file.getOriginalFilename();
                    if (StringUtils.isNotBlank(fileName)){
                        String[] fileNameAll = fileName.split("\\.");
                        //获得后缀
                        String suffix = fileNameAll[fileNameAll.length -1];
                        if (!suffix.equalsIgnoreCase(IMG_TYPE_PNG)&&!suffix.equalsIgnoreCase(IMG_TYPE_JPG)&&!suffix.equalsIgnoreCase(IMG_TYPE_JPEG)) {
                            continue;
                        }
                        path = uploadFileService.uploadIMGOne(file,suffix,null);
                    }
                }else {
                    continue;
                }
                logger.info("path={}",path);
                if (StringUtils.isNoneBlank(path)) {
                    imageList.add(path);
                }
            }
        }
        return GraceJSONResult.ok(imageList);
    }



    @Override
    public GraceJSONResult BigIMGUpload(FileUpload fileUpload, HttpServletRequest request) throws Exception {
        int chunk = 0;
        String fileMd5 = fileUpload.getMd5();
        long start = fileUpload.getStart();
        if (fileUpload.getChunk() != null) {
            chunk = fileUpload.getChunk();
            // 分片处理时，前台会多次调用上传接口，每次都会上传文件的一部分到后台
            MyFileUtils.fileUpload(fileUpload.getFile(),FACE_PATH,start,fileMd5,chunk);
            redisOperator.set(CHUNKS + fileMd5,String.valueOf(fileUpload.getChunks()));
            return GraceJSONResult.ok();
        }else {
            return uploadIMG(fileUpload.getFile(),fileMd5);
        }
    }



}
