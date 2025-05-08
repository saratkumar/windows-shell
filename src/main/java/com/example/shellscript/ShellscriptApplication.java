package com.example.shellscript;

import com.example.shellscript.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(Config.class)
public class ShellscriptApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShellscriptApplication.class, args);

	}

}
