package jp.onlinebooking.web.controller;

import jp.onlinebooking.web.modules.base.service.BaseService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService extends BaseService {

    @Async
    public void test1() {
        info("test1 executed...");
//        int i = 1 / 0;
    }


}
