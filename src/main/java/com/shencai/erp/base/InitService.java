package com.shencai.erp.base;

import com.shencai.erp.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @program: fileUpload
 * @description:
 * @author: fujl
 * @create: 2018-09-19 19:22
 **/
@Service
@Slf4j
public class InitService {
    @Autowired
    private StorageService storageService;

    @PostConstruct
    private void init() {
        log.info("--------------init---------");
        storageService.init();
    }

}
