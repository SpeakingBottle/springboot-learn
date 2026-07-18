package com.example.springbootlearn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
 * 三个注解的合体，作用是：
 *   1. 标记这是一个 Spring Boot 配置类
 *   2. 自动配置（根据 pom.xml 里的依赖，自动配好 Tomcat、Spring MVC 等）
 *   3. 扫描当前包及子包下的所有 @Component、@Controller、@Service 等注解
 *
 * 所以你的 controller、service 等都要放在 com.example.springbootlearn 包下，
 * 否则 Spring 找不到它们。
 */
@SpringBootApplication
@EnableCaching  //启用Redis缓存
public class SpringbootLearnApplication {

    public static void main(String[] args) {
        // SpringApplication.run() 启动整个 Spring 容器
        // 相当于前端执行 npm run dev，启动开发服务器
        SpringApplication.run(SpringbootLearnApplication.class, args);
    }

}
