package fr.nordlys.shoploc.merchants;

import org.springframework.boot.SpringApplication;

public class TestServiceMerchantsApplication {

	public static void main(String[] args) {
		SpringApplication.from(ServiceMerchantsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
