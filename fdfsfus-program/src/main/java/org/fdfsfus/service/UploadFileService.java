package org.fdfsfus.service;

import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import org.fdfsfus.pojo.FileDownload;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author YanZiMing
 * @DATE 2021/10/21  3:35 下午
 */
public interface UploadFileService {

    /**
     * 普通文件上传携带metaDataSet
     * @param file 文件
     * @param suffix 文件后缀
     * @return 文件路径
     */
    public String uploadFileOne(MultipartFile file, String suffix, String fileMd5, Set<MetaData> metaDataSet);

    /**
     * 普通文件上传携带metaDataSet
     * @param file 文件
     * @param suffix 文件后缀
     * @return 文件路径
     */
    public String uploadFileOne(MultipartFile file, String suffix, Set<MetaData> metaDataSet);

    /**
     * 单图片上传（会生成缩略图）
     * 原图   http://localhost:8098/M00/00/17/rBEAAl33pQaAWNQNAAHYvQQn-YE374.jpg
     * 缩略图 http://localhost:8098/M00/00/17/rBEAAl33pQaAWNQNAAHYvQQn-YE374_150x150.jpg
     * @param file 文件
     * @param suffix 文件后缀
     * @return 文件路径
     */
    public String uploadIMGOne(MultipartFile file, String suffix ,Set<MetaData> metaDataSet);


    /**
     * 单图片上传（会生成缩略图）
     * 原图   http://localhost:8098/M00/00/17/rBEAAl33pQaAWNQNAAHYvQQn-YE374.jpg
     * 缩略图 http://localhost:8098/M00/00/17/rBEAAl33pQaAWNQNAAHYvQQn-YE374_150x150.jpg
     * @param file 文件
     * @param suffix 文件后缀
     * @return 文件路径
     */
    public String uploadIMGOne(MultipartFile file, String suffix , String fileMd5,Set<MetaData> metaDataSet);

    /**
     * 分片上传
     * @param files 文件
     * @return 文件路径
     */
    public String uploadBigFile(Map<Integer, MultipartFile> files, String suffix, Integer chunks, String fileMd5)throws IOException;

    /**
     * 文件下载
     * @param path 文件地址
     * 有效的路径样式为(group/path) 或者 (http://ip/group/path),路径地址必须包含group
     */
    public List<FileDownload> fileDownload(String path);

    /**
     * 文件删除
     * @param path 文件地址
     * 有效的路径样式为(group/path) 或者 (http://ip/group/path),路径地址必须包含group
     */
    public void fileDelete(String path);

}
