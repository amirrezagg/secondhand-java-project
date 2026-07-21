package ir.aut.secondhand.config;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Exposes uploaded advertisement files through a static resource path.
 *
 * <p>Routes requests matching /uploads/advertisements/** to the filesystem directory
 * configured by app.upload.advertisements-dir, enabling direct access to stored
 * advertisement media.</p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.advertisements-dir}")
    private String uploadDir;

    /**
     * Registers the handler that serves advertisement uploads from the configured
     * filesystem directory.
     *
     * @param registry the registry used to bind request patterns to resource locations
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = Paths.get(uploadDir).toAbsolutePath().toUri().toString();
        
        registry.addResourceHandler("/uploads/advertisements/**")
                .addResourceLocations(absolutePath);
    }
}