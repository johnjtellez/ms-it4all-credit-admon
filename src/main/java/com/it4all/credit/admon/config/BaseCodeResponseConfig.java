package  com.it4all.credit.admon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:codeResponse.properties")
public class BaseCodeResponseConfig {
	
	@Autowired
    private Environment env;

	public String getDescription(String key){
		return this.env.getProperty(key);
	}
}
