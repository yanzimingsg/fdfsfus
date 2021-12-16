package org.fdfsfus.controller;

import org.apache.commons.lang3.StringUtils;
import org.fdfsfus.api.controller.FileOperationControllerApi;
import org.fdfsfus.controller.share.BasicShare;
import org.fdfsfus.pojo.CheckFile;
import org.fdfsfus.pojo.FileDownload;
import org.fdfsfus.pojo.FileMerge;
import org.fdfsfus.result.GraceJSONResult;
import org.fdfsfus.result.ResponseStatusEnum;
import org.fdfsfus.service.UploadFileService;
import org.fdfsfus.utils.MyFileUtils;
import org.fdfsfus.utils.RedisOperator;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 文件操作
 * @author YanZiMing
 * @DATE 2021/10/29  6:02 下午
 */
@RestController
public class FileOperationController extends BasicShare implements FileOperationControllerApi {

    private final UploadFileService uploadFileService;
    private final RedisOperator redis;

    public FileOperationController(UploadFileService uploadFileService, RedisOperator redis) {
        this.uploadFileService = uploadFileService;
        this.redis = redis;
    }

    @Override
    public GraceJSONResult checkFile(CheckFile checkFile) throws Exception {
        String fileMd5 = checkFile.getMd5();
        //模拟从mysql中查询文件表的md5,这里从redis里查询
        List<String> list = redis.lRange(FDFS_MD5_LIST,0,-1);
        if (list != null && list.size() != 0) {
            for (String md5 : list) {
                if (md5.equals(fileMd5)) {
                    return GraceJSONResult.ok(redis.get(FDFS_PATH+fileMd5));
                }
            }
        }
        return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_ZERO);
    }

    @Override
    public void fileDownload(String path, HttpServletResponse response) throws Exception {
        List<FileDownload> list = uploadFileService.fileDownload(path);
        String FileName = null;
        byte[] FileByte = null;
        for (FileDownload fileDownload : list) {
            FileName = fileDownload.getFileName();
            FileByte = fileDownload.getFileByte();
            MyFileUtils.FileDownloadUtil(FileName,FileByte,response);
        }

    }

    @Override
    public GraceJSONResult fileDelete(String path) throws Exception {
        uploadFileService.fileDelete(path);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult FDFSMerge(FileMerge fileMerge, HttpServletRequest request) throws Exception {
        String fileMD5 = fileMerge.getMd5();
        CheckFile checkFile = new CheckFile();
        checkFile.setMd5(fileMD5);
        GraceJSONResult graceJSONResult = checkFile(checkFile);
        Boolean doPath = graceJSONResult.getSuccess();
        if (!doPath) {
            String filepath = FACE_PATH + File.separatorChar + fileMD5;
            Map<Integer, MultipartFile> map = MyFileUtils.fileMap(filepath);
            String path = null;
            int chunks  = 0;
            String chunksStr = redis.get(CHUNKS + fileMerge.getMd5());
            if (StringUtils.isNotBlank(chunksStr)) {
                chunks  = Integer.parseInt(chunksStr);
            }else {
                return GraceJSONResult.errorCustom(ResponseStatusEnum.UPLOAD_FAIL);
            }
            try {
                path = uploadFileService.uploadBigFile(map,fileMerge.getSuffix(),chunks,fileMerge.getMd5());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return GraceJSONResult.ok(path);
        }else{
            return GraceJSONResult.ok(graceJSONResult.getData());
        }
    }

}
