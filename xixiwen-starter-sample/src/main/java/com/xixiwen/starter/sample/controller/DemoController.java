package com.xixiwen.starter.sample.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @包名: com.xixiwen.starter.sample.controller </br>
 * @描述: todo </br>
 * @作者:童晶继 tongjj@wedoctor.com </br>
 * @时间:2022年07月21 9:54 </br>
 * @版本:version
 */
@RestController
@RequestMapping("/demo/get")
@Slf4j
@RefreshScope
public class DemoController {



    @Value("${kit.able}")
    private String fas;

    @GetMapping("/text")
    public String text(){
        return fas;
    }
}