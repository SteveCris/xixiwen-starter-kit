package com.xixiwen.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @包名: com.xixiwen.register.starter.zookeeper </br>
 * @描述: ZookeeperApplication.class </br>
 * @作者:童晶继 tongjj@wedoctor.com </br>
 * @时间:2022年07月15 16:09 </br>
 * @版本:version
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ZookeeperApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZookeeperApplication.class, args);
    }
}