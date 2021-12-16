package org.fdfsfus.api.controller;

import org.fdfsfus.pojo.FileUpload;
import org.fdfsfus.result.GraceJSONResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;


/**
 * 图片上传接口
 * @author YanZiMing
 * @DATE 2021/10/20  6:50 下午
 */
@RequestMapping("fs")
public interface UploadPictureControllerApi {
    /**
     * base64上传单个图片
     * @param faceBase64 base64
     * @return 结果
     * @throws Exception 错误
     */
    @PostMapping("/uploadIMGBase64")
    public GraceJSONResult uploadIMGBase64(@RequestParam String faceBase64) throws Exception;


    /**
     * 上传单个图片
     * @param file 图片
     * @return 结果
     * @throws Exception 错误
     */
    @PostMapping("/uploadIMG")
    public GraceJSONResult uploadIMG(@RequestParam MultipartFile file, String fileMd5) throws Exception;

    /**
     * 上传多张图片
     * @param files 图片
     * @return 结果
     * @throws Exception 错误
     */
    @PostMapping("/uploadSomeIMG")
    public GraceJSONResult uploadSomeIMG(@RequestParam MultipartFile[] files) throws Exception;

    /**
     * 用于上传分片大文件
     * @param fileUpload 上传实体类
     * @return 结果
     * @throws Exception
     */
    @PostMapping("/bigIMGUpload")
    public GraceJSONResult BigIMGUpload(FileUpload fileUpload, HttpServletRequest request) throws Exception;






}
