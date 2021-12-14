package org.fdfsfus.api.controller.Hello;

import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author YanZiMing
 * @DATE 2021/10/20  6:32 下午
 */

/**
 * 用于测试连接是否正常
 */
public interface HelloControllerApi {
    @GetMapping("/hello")
    public String hello();
}
