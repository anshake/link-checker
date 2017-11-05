package org.shake.linkcheck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URISyntaxException;

@SpringBootApplication
public class Bootstrap
{
    public static void main(String[] args) throws URISyntaxException
    {
        ConfigurableApplicationContext ctx = SpringApplication.run(Bootstrap.class, args);

        CheckExecutor checkExecutor = ctx.getBean(CheckExecutor.class);
        checkExecutor.start();
    }

}
