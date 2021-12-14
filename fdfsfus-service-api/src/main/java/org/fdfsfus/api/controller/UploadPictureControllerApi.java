package org.fdfsfus.api.controller;

import org.fdfsfus.pojo.FileUpload;
import org.fdfsfus.result.GraceJSONResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


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
    public GraceJSONResult uploadIMG(@RequestParam MultipartFile file) throws Exception;

    /**
     * 上传多张图片
     * @param files 图片
     * @return 结果
     * @throws Exception 错误
     */
    @PostMapping("/uploadSomeIMG")
    public GraceJSONResult uploadSomeIMG(@RequestParam MultipartFile[] files) throws Exception;

    /**
     * 用于上传大图片（ 500kb 以上）
     * @param request
     * @return 结果
     * @throws Exception 错误
     */
    @PostMapping("/uploadBigIMGMap")
    public GraceJSONResult uploadBigIMG(@RequestParam Map<String, Object> paramMap, HttpServletRequest request) throws Exception;

    @PostMapping("/uploadBigIMG")
    public GraceJSONResult uploadBigIMG(@RequestParam FileUpload fileUpload, HttpServletRequest request) throws Exception;

    /**
     * 用于并发上传大图片（ 1mb 以上）
     * @param
     * @return 结果
     * @throws Exception 错误
     */
    @PostMapping("/uploadBigIMGConcurrent")
    public GraceJSONResult uploadBigIMGConcurrent(@RequestParam Map<String, Object> paramMap, MultipartFile file) throws Exception;



}
