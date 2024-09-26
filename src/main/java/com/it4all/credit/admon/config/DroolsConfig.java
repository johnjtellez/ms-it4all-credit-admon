package com.it4all.credit.admon.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroolsConfig {

    private static final KieServices kieServices = KieServices.Factory.get();

	@Value("${it4all.folderpathrules}")
	private String folderpathrules;

    @Bean
    public KieContainer kieContainer() throws IOException {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        List<String> droolsFiles = getDroolsFiles(folderpathrules);

        for (String droolsFile : droolsFiles) {
            kieFileSystem.write(ResourceFactory.newFileResource(droolsFile));
        }

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        KieModule kieModule = kieBuilder.getKieModule();
        KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());
        return kieContainer;
    }

    public static List<String> getDroolsFiles(String folderPath) throws IOException {
        List<String> droolsFiles = new ArrayList<>();
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".drl"));
            if (files != null) {
                for (File file : files) {
                    droolsFiles.add(file.getPath());
                }
            }
        }
        return droolsFiles;
    }
}
