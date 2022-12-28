package ru.fakel.smile.file.hosting;

import java.time.ZoneOffset;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    static {
        System.setProperty("user.timezone", "UTC");
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneOffset.UTC.getId()));
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
