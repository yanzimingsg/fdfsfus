package org.fdfsfus.api.controller;

import org.fdfsfus.pojo.FileUpload;
import org.fdfsfus.result.GraceJSONResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * 文件上传接口
 * @author YanZiMing
 * @DATE 2021/10/29  3:17 下午
 */
@RequestMapping("fs")
public interface UploadFileControllerApi {
    /**
     * base64上传单个文件
     * @param faceBase64 base64
     * @return 结果
     * @throws Exception 错误
     */
    @PostMapping("/uploadFileBase64")
    public GraceJSONResult uploadFileBase64(@RequestParam String faceBase64) throws Exception;


    /**
     * 上传单个文件
     * @param file 文件
     * @return 结果
     * @throws Exception 错误
     */
    @PostMapping("/uploadFile")
    public GraceJSONResult uploadFile(@RequestParam MultipartFile file, String fileMd5) throws Exception;

    /**
     * 上传多个文件
     * @param files 文件
     * @return 结果
     * @throws Exception 错误
     */
    @PostMapping("/uploadSomeFile")
    public GraceJSONResult uploadSomeFile(@RequestParam MultipartFile[] files) throws Exception;

    /**
     * 用于上传分片大文件
     * @param fileUpload 上传实体类
     * @return 结果
     * @throws Exception
     */
    @PostMapping("/bigFileUpload")
    public GraceJSONResult BigFileUpload(FileUpload fileUpload, HttpServletRequest request) throws Exception;








}
