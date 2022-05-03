package com.example.studentservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
@EnableFeignClients
public class StudentServiceApplication {
    @Autowired
    private Environment environment;

    @Autowired
    private DepartmentServiceClient departmentServiceClient;

    public static void main(String[] args) {
        SpringApplication.run(StudentServiceApplication.class, args);
    }

    @GetMapping("/test")
    public String test() {
        return "[This is Student Service running on "+environment.getProperty("server.port")+"]";
    }

    @GetMapping("/getProperty/{name}")
    public String getProperty(@PathVariable("name") String name) {
        return environment.getProperty(name);
    }

    @GetMapping("/getDepartmentByDockerDNS")
    public String getDepartmentByDockerDNS() {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        ResponseEntity<String> stringResponseEntity = restTemplate.getForEntity("http://department-service:8082/test", String.class);
        return stringResponseEntity.getBody();
    }

    @GetMapping("getDepartmentByOpenFeign")
    public String getDepartmentByOpenFeign() {
        return departmentServiceClient.test();
    }

    @FeignClient(name = "department-service")
    static interface DepartmentServiceClient {
        @GetMapping("/test")
        String test();
    }
}
