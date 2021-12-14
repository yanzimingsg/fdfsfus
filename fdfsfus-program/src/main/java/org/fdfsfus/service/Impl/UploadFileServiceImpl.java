package org.fdfsfus.service.Impl;

import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.DefaultGenerateStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.github.tobato.fastdfs.service.TrackerClient;
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
    private static TrackerClient trackerClient;


    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    @Autowired
    private DefaultGenerateStorageClient defaultGenerateStorageClient;
//
//    /**
//     * 连接池
//     */
//    protected FdfsConnectionManager manager = createConnectionManager();
//
//    private FdfsConnectionManager createConnectionManager() {
//        return new FdfsConnectionManager(createPool());
//    }
//
//    private FdfsConnectionPool createPool() {
//        PooledConnectionFactory factory = new PooledConnectionFactory();
//        factory.setConnectTimeout(connectTimeout);
//        factory.setSoTimeout(soTimeout);
//        return new FdfsConnectionPool(new PooledConnectionFactory());
//    }
//
//    protected <T> T executeStoreCmd(FdfsCommand<T> command) {
//        return manager.executeFdfsCmd(store_address, command);
//    }

    @Autowired
    private RedisOperator redis;

    private StorePath storePath = null;

    @Override
    public String uploadFileOne(MultipartFile file, String suffix) {
        try {
            storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), suffix, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return storePath.getPath();
    }

    @Override
    public String uploadFileOne(MultipartFile file, String suffix, Set<MetaData> metaDataSet) {
        try {
            storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), suffix, metaDataSet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return storePath.getPath();
    }

    @Override
    public String uploadIMGOne(MultipartFile file, String suffix) {
        try {
            storePath = storageClient.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(), suffix, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return storePath.getPath();
    }

    @Override
    public String uploadIMGOne(MultipartFile file, String suffix, Set<MetaData> metaDataSet) {
        try {
            storePath = storageClient.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(), suffix, metaDataSet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return storePath.getPath();
    }

    @Override
    public String uploadBigFile(List<MultipartFile> files, String suffix, Integer chunk, Integer chunks, String fileMd5) throws IOException{
        MultipartFile file = null;
        //redis中获取当前应该从第几块开始(从0开始)
        String chunkKey = CHUNK + fileMd5;

        //如果不使用checkFile接口请加上不然会报错
//        if (StringUtils.isBlank(chunkStr)) {
//            redis.set(chunkKey,"0");
//        }
        String chunkStr = redis.get(chunkKey);
        if (StringUtils.isEmpty(chunkStr)) {
            return "无法获取当前文件chunk";
        }
        int chunkCurrInt = Integer.parseInt(chunkStr);
        for (MultipartFile multipartFile : files) {
            file = multipartFile;
            fileUpload( file, chunkKey, chunkCurrInt, fileMd5, suffix, chunk);
        }
        //上传完成将文件MD5储存在redis里
        if (chunk + 1 == chunks) {
            redis.lpush(FILEMD5LIST, fileMd5);
        }
        return storePath.getPath();
    }

    @Override
    public String uploadBigFileConcurrent(Map<Integer, MultipartFile> files, String suffix, Integer chunks, String fileMd5) throws IOException {
        MultipartFile file = null;
        String chunkKey = CHUNK + fileMd5;
        String chunkStr = redis.get(chunkKey);
        //如果不使用checkFile接口请加上不然会报错
//        if (StringUtils.isBlank(chunkStr)) {
//            redis.set(chunkKey,"0");
//        }
        if (StringUtils.isEmpty(chunkStr)) {
            return "无法获取当前文件chunk";
        }
        int chunkCurrInt = Integer.parseInt(chunkStr);
        for (Integer chunk : files.keySet()) {
            MultipartFile file1 = files.get(chunk);
            fileUpload( file1, chunkKey, chunkCurrInt, fileMd5, suffix, chunk);
            //将文件MD5储存在redis里
            if (chunk + 1 == chunks) {
                redis.lpush(FILEMD5LIST, fileMd5);
            }
        }
        return storePath.getPath();
    }

    private void fileUpload(MultipartFile file,String chunkKey,Integer chunkCurrInt,String fileMd5,String suffix,Integer chunk) throws IOException {
        String GroupPath = null;
        if (!file.isEmpty()) {
            //获取已经上传文件大小
            long historyUpload = 0L;
            String historyUploadStr = redis.get(HISTORYUPLOAD + fileMd5);
            if (StringUtils.isNotBlank(historyUploadStr)) {
                historyUpload = Long.parseLong(historyUploadStr);
            }
            redis.set(chunkKey, String.valueOf(chunkCurrInt + 1));
            if (chunk == 0) {
                storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP, file.getInputStream(), file.getSize(), suffix);
                GroupPath = storePath.getPath();
                redis.set(FASTDFSPATH + fileMd5, GroupPath);
                System.out.println(GroupPath);
            } else {
                GroupPath = redis.get(FASTDFSPATH + fileMd5);
                //追加方式实际实用如果中途出错多次,可能会出现重复追加情况,这里改成修改模式,即时多次传来重复文件块,依然可以保证文件拼接正确
                appendFileStorageClient.modifyFile(DEFAULT_GROUP, GroupPath, file.getInputStream(),
                        file.getSize(), historyUpload);
            }
            //修改历史上传大小
            historyUpload = historyUpload + file.getSize();
            redis.set(HISTORYUPLOAD + fileMd5, String.valueOf(historyUpload));
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
