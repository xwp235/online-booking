package jp.onlinebooking.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    private final AsyncService asyncService;

    public TestController(AsyncService asyncService) {
        this.asyncService = asyncService;
    }

    @GetMapping("1")
    public void test1() {
          asyncService.test1();
    }

}
