package gg.falmer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    public Main() {
        CrptApi api = new CrptApi(TimeUnit.SECONDS, 1);
        CrptApi.Document document = new CrptApi.Document();
        String result = api.createDocument(document, "signature").block();
        System.out.println(result);
    }
}