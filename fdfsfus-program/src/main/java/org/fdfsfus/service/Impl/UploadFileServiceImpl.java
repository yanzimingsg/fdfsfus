package org.fdfsfus.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.DefaultGenerateStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang3.StringUtils;
import org.fdfsfus.controller.share.BasicShare;
import org.fdfsfus.pojo.FileDownload;
import org.fdfsfus.service.UploadFileService;
import org.fdfsfus.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author YanZiMing
 * @DATE 2021/10/21  3:36 下午
 */

@Service
public class UploadFileServiceImpl extends BasicShare implements UploadFileService {

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    @Autowired
    private DefaultGenerateStorageClient defaultGenerateStorageClient;

    @Autowired
    private RedisOperator redis;

    @Override
    public String uploadFileOne(MultipartFile file, String suffix, String fileMd5, Set<MetaData> metaDataSet) {
        StorePath storePath = null;

        try {
            storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), suffix, metaDataSet);
            redis.lpush(FDFS_MD5_LIST, fileMd5);
            redis.set(FDFS_PATH+fileMd5,storePath.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (storePath != null) {
            return storePath.getPath();
        }else {
            return "上传失败";
        }
    }

    @Override
    public String uploadFileOne(MultipartFile file, String suffix, Set<MetaData> metaDataSet) {
        StorePath storePath = null;

        try {
            storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), suffix, metaDataSet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (storePath != null) {
            return storePath.getPath();
        }else {
            return "上传失败";
        }
    }

    @Override
    public String uploadIMGOne(MultipartFile file, String suffix, Set<MetaData> metaDataSet) {
        StorePath storePath = null;
        try {
            storePath = storageClient.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(), suffix, metaDataSet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (storePath != null) {
            return storePath.getPath();
        }else {
            return "上传失败";
        }
    }

    @Override
    public String uploadIMGOne(MultipartFile file, String suffix, String fileMd5, Set<MetaData> metaDataSet) {
        StorePath storePath = null;
        try {
            storePath = storageClient.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(), suffix, metaDataSet);
            redis.lpush(FDFS_MD5_LIST, fileMd5);
            redis.set(FDFS_PATH+fileMd5,storePath.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (storePath != null) {
            return storePath.getPath();
        }else {
            return "上传失败";
        }
    }

    @Override
    public String uploadBigFile(Map<Integer, MultipartFile> files, String suffix, Integer chunks, String fileMd5) throws IOException {
        StorePath storePath = null;
        MultipartFile file = null;
        for (int i=0;i<chunks;i++) {
            MultipartFile file1 = files.get(i);
            String[] list = file1.getName().split("_");
            long historyUpload = Long.parseLong(list[1]);
            String fileNum = redis.get(NUM_UP_FILE+fileMd5);
            if (StringUtils.isBlank(fileNum)) {
                fileUpload( file1, fileMd5, suffix, i, historyUpload);
            }else{
                if (!(i<=Integer.parseInt(fileNum))){
                    fileUpload( file1, fileMd5, suffix, i, historyUpload);
                }
            }
        }
         storePath = JSONObject.parseObject(redis.get(FASTDFSPATH + fileMd5),StorePath.class);
        redis.lpush(FDFS_MD5_LIST, fileMd5);
        if (storePath != null) {
            return storePath.getPath();
        }else {
            return "上传失败";
        }
    }


    private void fileUpload(MultipartFile file,String fileMd5,String suffix,Integer chunk,long historyUpload ) throws IOException {
        StorePath storePath = null;
        String GroupPath = null;
        if (!file.isEmpty()) {
            if (chunk == 0) {
                storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP, file.getInputStream(), file.getSize(), suffix);
                String storePathStr  =  JSONObject.toJSONString(storePath);
                redis.set(FASTDFSPATH + fileMd5, storePathStr);
                redis.set(FDFS_PATH+fileMd5,storePath.getPath());
                redis.set(NUM_UP_FILE+fileMd5,String.valueOf(chunk));
            } else {
                storePath = JSONObject.parseObject(redis.get(FASTDFSPATH + fileMd5),StorePath.class);
                GroupPath = storePath.getPath();
                //追加方式实际实用如果中途出错多次,可能会出现重复追加情况,这里用修改模式,即时多次传来重复文件块,依然可以保证文件拼接正确
                appendFileStorageClient.modifyFile(DEFAULT_GROUP, GroupPath, file.getInputStream(),
                        file.getSize(), historyUpload);
                redis.set(NUM_UP_FILE+fileMd5,String.valueOf(chunk));
            }
        }
    }

    @Override
    public List<FileDownload> fileDownload(String path) {
        List<FileDownload> list  = new ArrayList<>();
        FileDownload fileDownload = new FileDownload();
        StorePath  storePath = parseFromUrl(path);
        String fileName = getFileName(storePath.getPath());
        DownloadByteArray byteArray = new DownloadByteArray();
        byte[] fileByte = storageClient.downloadFile(storePath.getGroup(),storePath.getPath(),byteArray);
        fileDownload.setFileName(fileName);
        fileDownload.setFileByte(fileByte);
        list.add(fileDownload);
        return list;
    }

    @Override
    public void fileDelete(String path) {
        StorePath  storePath = parseFromUrl(path);
        defaultGenerateStorageClient.deleteFile(storePath.getGroup(),storePath.getPath());
    }
}
