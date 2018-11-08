package com.fx.spider;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@MapperScan("com.fx.spider.mapper")
@Controller
public class FxSpiderApplication {

	public static void main(String[] args) {
		SpringApplication.run(FxSpiderApplication.class, args);
	}

	@RequestMapping({"", "/"})
	public String toOrder() {
		return "redirect:/order.html";
	}

}
