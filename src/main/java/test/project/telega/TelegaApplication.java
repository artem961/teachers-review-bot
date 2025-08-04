package test.project.telega;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import test.project.telega.SheetFormatting.XLSXLoader;

import java.io.File;

@SpringBootApplication
public class TelegaApplication {
	public static void main(String[] args) {
		SpringApplication.run(TelegaApplication.class, args);

		//XLSXLoader loader = AppContext.getBean(XLSXLoader.class);
		//File file = new File("D:\\JavaProjects\\telega\\src\\main\\java\\test\\project\\telega\\отзывы.xlsx");
		//loader.load(file);
	}
}
