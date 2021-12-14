package org.fdfsfus.controller.hello;

import org.fdfsfus.api.controller.Hello.HelloControllerApi;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author YanZiMing
 * @DATE 2021/10/20  6:26 下午
 */
@RestController
@RefreshScope
public class helloController implements HelloControllerApi {

    @Override
    public String hello() {
        return "启动正常";
    }

}
