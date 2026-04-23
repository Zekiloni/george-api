package com.zekiloni.george.attachment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
        "com.vectorhaul.platform.attachment",
        "com.vectorhaul.platform.common"
})
public class AttachmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttachmentApplication.class, args);
    }
}
