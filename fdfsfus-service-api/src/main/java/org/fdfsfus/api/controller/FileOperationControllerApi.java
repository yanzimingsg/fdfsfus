package org.fdfsfus.api.controller;

import org.fdfsfus.pojo.CheckFile;
import org.fdfsfus.pojo.FileMerge;
import org.fdfsfus.result.GraceJSONResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 文件操作接口
 * @author YanZiMing
 * @DATE 2021/10/29  5:59 下午
 */
@RequestMapping("fs")
public interface FileOperationControllerApi {

    /**
     * 文件校验实现秒传
     * @return 结果
     * @throws Exception 错误
     */
    @PostMapping("/checkFile")
    public GraceJSONResult checkFile(CheckFile checkFile) throws Exception;

    /**
     * 文件下载
     * @param path
     * @return 结果
     * @throws Exception 错误
     */
    @GetMapping("/fileDownload")
    public void fileDownload(@RequestParam String path, HttpServletResponse response) throws Exception;

    /**
     * 文件删除
     * @param path
     * @return 结果
     * @throws Exception 错误
     */
    @PostMapping("/fileDelete")
    public GraceJSONResult fileDelete(@RequestParam String path) throws Exception;

    /**
     * 上传文件到fdfs
     * @param fileMerge
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/FDFSMerge")
    public GraceJSONResult FDFSMerge(FileMerge fileMerge, HttpServletRequest request) throws Exception;


}
