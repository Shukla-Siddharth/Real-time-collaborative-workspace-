package com.purplemerit.workspace;

import org.springframework.boot.SpringApplication;

public class TestCollabWorkspaceBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(CollabWorkspaceBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
