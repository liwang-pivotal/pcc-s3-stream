package io.spring;


import io.pivotal.spring.cloud.service.gemfire.GemfireServiceConnectorConfig;

import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.util.CacheListenerAdapter;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.cloud.service.ServiceConnectorConfig;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

@EnableBinding(Source.class)
@Configuration
@EnableConfigurationProperties(DemoProperties.class)
public class DemoConfig extends AbstractCloudConfig {
	
	@Autowired
	@Qualifier(Source.OUTPUT)
	MessageChannel output;
	
	@Autowired
	DemoProperties config;
	
	public ServiceConnectorConfig createGemfireConnectorConfig() {

        GemfireServiceConnectorConfig gemfireConfig = new GemfireServiceConnectorConfig();
        gemfireConfig.setPoolSubscriptionEnabled(true);
        gemfireConfig.setPdxSerializer(new ReflectionBasedAutoSerializer(".*"));
        gemfireConfig.setPdxReadSerialized(false);

        return gemfireConfig;
    }
    
	@Bean(name = "gemfireCache")
    public ClientCache getGemfireClientCache() throws Exception {		
		
		Cloud cloud = new CloudFactory().getCloud();
		ClientCache clientCache = cloud.getSingletonServiceConnector(ClientCache.class,  createGemfireConnectorConfig());

        return clientCache;
    }

	@SuppressWarnings("unchecked")
	@Bean
	public Region<String, Object> offerRegion(@Autowired ClientCache clientCache) {
		ClientRegionFactory<String, Object> regionFactory = clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY);

		regionFactory.addCacheListener(new DummyListener());
		Region<String, Object> region = regionFactory.create(config.getRegionName());

		region.registerInterest("ALL_KEYS");

		return region;
	}
	
    
    class DummyListener extends CacheListenerAdapter {
    	
    	Logger logger = LoggerFactory.getLogger(this.getClass());
    	
    	@Override
    	public void afterCreate (EntryEvent e) {
    		PdxInstance pdx = (PdxInstance)e.getNewValue();
    		JSONObject json = new JSONObject();
    		
    		for (String field : pdx.getFieldNames()) {
    			try {
    				json.put(field, pdx.getField(field));
    			} catch (JSONException exception) {
    				exception.printStackTrace();
    			}
    		}
    		

    		output.send(new GenericMessage<String>(json.toString()));
    	}
    	
    	@Override
    	public void afterUpdate (EntryEvent e) {
    		PdxInstance pdx = (PdxInstance)e.getNewValue();
    		JSONObject json = new JSONObject();
    		
    		for (String field : pdx.getFieldNames()) {
    			try {
    				json.put(field, pdx.getField(field));
    			} catch (JSONException exception) {
    				exception.printStackTrace();
    			}
    		}
    		
    		output.send(new GenericMessage<String>(json.toString()));
    		
    	}

    }

}
