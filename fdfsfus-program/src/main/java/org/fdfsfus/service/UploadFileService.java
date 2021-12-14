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
     * 普通文件上传不携带metaDataSet
     * @param file 文件
     * @param suffix 文件后缀
     * @return 文件路径
     */
    public String uploadFileOne(MultipartFile file, String suffix);

    /**
     * 普通文件上传携带metaDataSet
     * @param file 文件
     * @param suffix 文件后缀
     * @return 文件路径
     */
    public String uploadFileOne(MultipartFile file, String suffix, Set<MetaData> metaDataSet);

    /**
     * 单图片上传不携带metaDataSet（会生成缩略图）
     * 原图   http://localhost:8098/M00/00/17/rBEAAl33pQaAWNQNAAHYvQQn-YE374.jpg
     * 缩略图 http://localhost:8098/M00/00/17/rBEAAl33pQaAWNQNAAHYvQQn-YE374_150x150.jpg
     * @param file 文件
     * @param suffix 文件后缀
     * @return 文件路径
     */
    public String uploadIMGOne(MultipartFile file, String suffix);

    /**
     * 单图片上传携带metaDataSet（会生成缩略图）
     * 原图   http://localhost:8098/M00/00/17/rBEAAl33pQaAWNQNAAHYvQQn-YE374.jpg
     * 缩略图 http://localhost:8098/M00/00/17/rBEAAl33pQaAWNQNAAHYvQQn-YE374_150x150.jpg
     * @param file 文件
     * @param suffix 文件后缀
     * @return 文件路径
     */
    public String uploadIMGOne(MultipartFile file, String suffix, Set<MetaData> metaDataSet);

    /**
     * 上传大文件
     * @param files 文件
     * @return 文件路径
     */
    public String uploadBigFile(List<MultipartFile> files, String suffix, Integer chunk, Integer chunks, String fileMd5)throws IOException;


    /**
     * 并发上传大文件
     * @param files 文件
     * @return 文件路径
     */
    public String uploadBigFileConcurrent(Map<Integer, MultipartFile> files, String suffix, Integer chunks, String fileMd5) throws IOException;

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
