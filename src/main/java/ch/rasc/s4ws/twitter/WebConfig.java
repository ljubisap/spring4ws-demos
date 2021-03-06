package ch.rasc.s4ws.twitter;

import java.util.Collections;

import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;

@Configuration
@EnableWebMvc
@EnableScheduling
@ComponentScan(basePackages = "ch.rasc.s4ws.twitter")
public class WebConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("index.html");
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Bean
	public ITopic<Tweet> hazelcastTopic() {
		Config config = new Config();
		config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
		config.getNetworkConfig().getJoin().getTcpIpConfig()
				.setMembers(Collections.singletonList("127.0.0.1")).setEnabled(true);

		HazelcastInstance hc = Hazelcast.newHazelcastInstance(config);
		return hc.getTopic("tweets");
	}

	@PreDestroy
	public void destroy() {
		Hazelcast.shutdownAll();
	}

}
