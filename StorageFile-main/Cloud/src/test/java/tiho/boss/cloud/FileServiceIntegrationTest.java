package tiho.boss.cloud;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tiho.boss.cloud.model.File;
import tiho.boss.cloud.model.User;
import tiho.boss.cloud.repository.UserRepository;
import tiho.boss.cloud.service.FileService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ContextConfiguration(initializers = FileServiceIntegrationTest.Initializer.class)
class FileServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.2")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgres.getJdbcUrl(),
                    "spring.datasource.username=" + postgres.getUsername(),
                    "spring.datasource.password=" + postgres.getPassword(),
                    "spring.datasource.driver-class-name=org.postgresql.Driver",
                    "spring.jpa.hibernate.ddl-auto=create-drop"
            ).applyTo(context.getEnvironment());
        }
    }

    @Autowired
    private FileService fileService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testStoreFile() throws IOException {
        User user = new User();
        user.setLogin("testuser");
        user.setPassword("testpass");
        user = userRepository.save(user);

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello from Testcontainers!".getBytes()
        );

        File savedFile = fileService.storeFile(user, mockFile, null);

        assertNotNull(savedFile.getId());
        assertEquals("test.txt", savedFile.getFilename());
        assertEquals(user.getId(), savedFile.getUser().getId());
        assertArrayEquals("Hello from Testcontainers!".getBytes(), savedFile.getFileContent());
    }
}