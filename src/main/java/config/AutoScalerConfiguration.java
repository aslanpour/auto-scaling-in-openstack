package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 
 * Loading/managing InterCloud Gateway configuration.
 * 
 * @author Alexandre di Costanzo
 * @author adel Nadjaran Toosi
 */
public class AutoScalerConfiguration {
	
	private final static Logger log = Logger.getLogger(AutoScalerConfiguration.class);

	public static String DEFAULT_PATH = "src/main/resources/default.properties";
	private static AutoScalerConfiguration singleton;
	private Properties properties;

	private AutoScalerConfiguration() {
		// 1. Load default properties
		loadDefaultProperties();
	}


	private void loadDefaultProperties() {
		Properties default_properties = new Properties();

		
		try {
			InputStream in =new FileInputStream(DEFAULT_PATH);
			default_properties.load(in);
			in.close();
		} catch (IOException ex) {
			log.error("Cannot open default properties file");
			System.exit(2);
		}catch(Exception e){
			e.printStackTrace();
		}
		this.properties = new Properties(default_properties);
	}
	
	/**
	 * Get an ICG's property.
	 * 
	 * @param key
	 *            the property's key
	 * @return the value associated to that key.
	 */
	protected String getProperty(String key) {
		synchronized (this.properties) {
			return this.properties.getProperty(key);
		}
	}
	
	
	/**
	 * @return the singleton reference.
	 */
	public static AutoScalerConfiguration getInstance() {
		if (singleton == null) {
			singleton = new AutoScalerConfiguration();
		}
		return singleton;
	}
	
	/**
	 * Set an ICG's property.
	 * 
	 * @param key
	 *            the property's key to set.
	 * @param value
	 *            the new value for the property.
	 * @return the previous value associated to that key.
	 */
	protected Object setProperty(String key, String value) {
		synchronized (this.properties) {
			return this.properties.setProperty(key, value);
		}
	}

}
