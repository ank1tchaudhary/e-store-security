package com.estoresecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EStoreSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(EStoreSecurityApplication.class, args);
	}


//	@Bean
//	public CommandLineRunner run(@Autowired UserService service, @Autowired UserRoleService roleService){
//		return args -> {
//			roleService.createRole(RoleRequest.builder().roleName("USER").description("user").permissions(Arrays.asList("READ")).build());
//			service.saveUser(User.builder()
//			.firstName("Ankit")
//			.lastName("ankit")
//			.userName("admin")
//			.password("admin@123".toCharArray())
//			.email("admin@japfa.com")
//			.phoneNo("1234567890")
//			.isActive(true)
//			.roles(new ArrayList<>()).build());
//			service.addRoleToUser(RoleToUser.builder().userName("admin").role(Arrays.asList(Role.builder().roleName("SUPER_ADMIN").permission(Arrays.asList("READ","WRITE","DELETE")).description("role admin").build())).build());
//			};
//	}

}
