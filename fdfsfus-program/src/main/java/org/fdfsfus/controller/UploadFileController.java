package org.fdfsfus.controller;

import org.apache.commons.lang3.StringUtils;
import org.fdfsfus.api.controller.UploadFileControllerApi;
import org.fdfsfus.controller.share.BasicShare;
import org.fdfsfus.result.GraceJSONResult;
import org.fdfsfus.result.ResponseStatusEnum;
import org.fdfsfus.service.UploadFileService;
import org.fdfsfus.utils.MyFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/**
 * 文件上传
 * @author YanZiMing
 * @DATE 2021/10/29  4:48 下午
 */
@RestController
public class UploadFileController extends BasicShare implements UploadFileControllerApi {
    /**
     * 日志
     */
    final static Logger logger = LoggerFactory.getLogger(UploadPictureController.class);

    private final UploadFileService uploadFileService;


    public UploadFileController(UploadFileService uploadFileService) {
        this.uploadFileService = uploadFileService;
    }


    @Override
    public GraceJSONResult uploadFileBase64(String faceBase64) throws Exception {
        String path = null;
        if (faceBase64 !=null) {
            //通过Base64获取后缀
            String fileSuffix = getSubUtilSimple(faceBase64,"/(.*?);");
            //这里是临时存储的文件路径
            String filePath = FACE_PATH + UUID.randomUUID() + "." + fileSuffix;
            MyFileUtils.base64ToFile(filePath, faceBase64);
            MultipartFile faceFile = MyFileUtils.fileToMultipart(filePath);
            path  = uploadFileService.uploadFileOne(faceFile, fileSuffix);
            logger.info("path={}",path);
            //删除临时文件
            MyFileUtils.delFile(filePath);
        }else {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }
        return GraceJSONResult.ok(path);
    }

    @Override
    public GraceJSONResult uploadFile(MultipartFile file) throws Exception {
        String path = null;
        if(file != null) {
            //获得文件名称
            String fileName =  file.getOriginalFilename();
            if (StringUtils.isNotBlank(fileName)){
                String[] fileNameAll = fileName.split("\\.");
                //获得后缀
                String suffix = fileNameAll[fileNameAll.length -1];
                path = uploadFileService.uploadFileOne(file,suffix);
            }
        }else {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        logger.info("path={}",path);
        return GraceJSONResult.ok(path);
    }

    @Override
    public GraceJSONResult uploadSomeFile(MultipartFile[] files) throws Exception {
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
                        path = uploadFileService.uploadIMGOne(file,suffix);
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
    public GraceJSONResult uploadBigFile(Map<String, Object> paramMap, HttpServletRequest request) throws Exception {
        String name = (String) paramMap.get("name");
        String[] fileNameAll = name.split("\\.");
        //获取后缀名
        String suffix = fileNameAll[fileNameAll.length -1];
        //获取文件MD5
        String fileMd5 = (String) paramMap.get("md5");
        //当前第几块
        int chunk =  Integer.parseInt((String) paramMap.get("chunk"));
        //总块数
        int chunks = Integer.parseInt((String) paramMap.get("chunks"));
        if (!paramMap.containsKey("chunk")){
            paramMap.put("chunk","0");
        }
        if (!paramMap.containsKey("chunks")){
            paramMap.put("chunks","1");
        }
        //获取文件块
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");

        String path = uploadFileService.uploadBigFile(files,suffix,chunk,chunks,fileMd5);
        logger.info("path={}",path);
        return GraceJSONResult.ok(path);
    }

    @Override
    public GraceJSONResult uploadBigFileConcurrent(Map<String, Object> paramMap, MultipartFile file) throws Exception {
        String fileId = (String) paramMap.get("onlyId");
        String name = (String) paramMap.get("name");
        String[] fileNameAll = name.split("\\.");
        //获取后缀名
        String suffix = fileNameAll[fileNameAll.length -1];
        //当前第几块
        String chunk =  (String) paramMap.get("chunk");
        String chunksStr = (String) paramMap.get("chunks");
        if (chunksStr==null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_CHUNKS_NOT_NULL);
        }
        //总块数
        int chunks = Integer.parseInt(chunksStr);
        //获取文件MD5
        String fileMd5 = (String) paramMap.get("md5");
        String path = null;
        // 分片处理时，前台会多次调用上传接口，每次都会上传文件的一部分到后台
        String filepath = MyFileUtils.fileUpload(file,FACE_PATH,name,fileId,chunk);
        File file1 = new File(filepath);
        int size = MyFileUtils.FileQuantity(file1);
        if (size == chunks) {
            Map<Integer,MultipartFile> map = MyFileUtils.fileMap(filepath);
            path = uploadFileService.uploadBigFileConcurrent(map,suffix,chunks,fileMd5);
        }
        logger.info("path={}",path);
        return  GraceJSONResult.ok(path);
    }

}
