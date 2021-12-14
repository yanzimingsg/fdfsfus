package org.fdfsfus.api.controller;

import org.fdfsfus.result.GraceJSONResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

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
    public GraceJSONResult checkFile(@RequestParam Map<String, Object> paramMap) throws Exception;

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
}
