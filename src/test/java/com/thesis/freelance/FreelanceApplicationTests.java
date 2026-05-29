package com.thesis.freelance;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class FreelanceApplicationTests {

    @MockitoBean
    private JavaMailSenderImpl javaMailSender;

    @Test
    void contextLoads() {
    }

}
