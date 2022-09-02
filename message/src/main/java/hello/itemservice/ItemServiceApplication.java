package hello.itemservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;

@SpringBootApplication
public class ItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

//	@Bean
//	public MessageSource messageSource() {
//		ResourceBundleMessageSource messageSource = new
//				ResourceBundleMessageSource();
//		messageSource.setBasenames("messages", "errors");
//		messageSource.setDefaultEncoding("utf-8");
//		return messageSource;
//	}
//	이렇게 하는것이 직접등록, 부트가 해주니까 우리는 안해도 된다. 자동으로 빈등록해놓는다.

}
