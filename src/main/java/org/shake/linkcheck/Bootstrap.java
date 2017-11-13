package org.shake.linkcheck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class Bootstrap
{
    static
    {
        System.setProperty("spring.config.name", "link-checker");
    }

    public static void main(String[] args) throws URISyntaxException, ExecutionException, InterruptedException
    {
        SpringApplicationBuilder appBuilder = new SpringApplicationBuilder();
        SpringApplication app = appBuilder.sources(Bootstrap.class).build(args);
        app.setAdditionalProfiles("cfg");

        ConfigurableApplicationContext ctx = app.run();

        CheckExecutor checkExecutor = ctx.getBean(CheckExecutor.class);
        checkExecutor.start();
    }

}
