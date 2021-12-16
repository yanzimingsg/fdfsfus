package org.fdfsfus.controller;

import org.fdfsfus.api.controller.FileOperationControllerApi;
import org.fdfsfus.controller.share.BasicShare;
import org.fdfsfus.pojo.FileDownload;
import org.fdfsfus.result.GraceJSONResult;
import org.fdfsfus.service.UploadFileService;
import org.fdfsfus.utils.MyFileUtils;
import org.fdfsfus.utils.RedisOperator;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
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
    public GraceJSONResult checkFile(Map<String, Object> paramMap) throws Exception {
        String fileMd5 = (String) paramMap.get("md5");
        String chunkKey = CHUNK + fileMd5;
        redis.set(chunkKey,"0");
        //模拟从mysql中查询文件表的md5,这里从redis里查询
        List<String> list = redis.lRange(FILEMD5LIST,0,-1);
        if (list != null && list.size() != 0) {
            for (String md5 : list) {
                if (md5.equals(fileMd5)) {
                    return GraceJSONResult.ok(redis.get(FASTDFSPATH+fileMd5));
                }
            }
        }
        return GraceJSONResult.ok(0);
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

}
