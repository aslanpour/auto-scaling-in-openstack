package config;

import org.apache.log4j.Logger;





/**
 * All InterCloud Properties.
 * 
 * This class's methods must be used instead of System.{get|set}Property()
 * methods.
 * 
 * This code is adapted from ProActive Parallel Suite (PAProperties.java):
 * http://proactive.inria.fr/
 * 
 * @author Adel Nadjaran Toosi
 * @author Mohsen Amini Salehi
 * @author Alexandre di Costanzo
 */
public enum ASProperties {

	//RESOULUTION_SCALING("resolution.scaling", PropertiesType.INTEGER),
	RESOLUTION_NODE_STATUS_CHECKING("resolution.node.status.checking", PropertiesType.INTEGER),
	RESOLUTION_CPU_UTILIZATION_CHECKING("resolution.cpu.utilization.checking", PropertiesType.INTEGER),
	RESOLUTION_CPU_UTILIZATION_SAMPLING("resolution.cpu.utilization.sampling", PropertiesType.INTEGER),
	RESOLUTION_HAPROXY_STAT_CHECKING("resolution.haproxy.stat.checking", PropertiesType.INTEGER),
	CREDENTIALS_GRID5K_USERNAME("credentials.grid5k.username", PropertiesType.STRING),
	CREDENTIALS_GRID5K_PASSWORD("credentials.grid5k.password", PropertiesType.STRING),
	CREDENTIALS_HAPROXY_USERNAME("credentials.haproxy.username", PropertiesType.STRING),
	CREDENTIALS_HAPROXY_PASSWORD("credentials.haproxy.password", PropertiesType.STRING),	
	SCALING_THRESHOLD_CPUUTIL("scaling.threshold", PropertiesType.DOUBLE),
	SCALING_THRESHOLD_REQS("scaling.threshold.reqs", PropertiesType.LONG),
	;
	
	private String key;
	private PropertiesType type;
	private final static Logger log = Logger.getLogger(ASProperties.class);

	/**
	 * @param str
	 *            string representing the property's key.
	 * @param type
	 *            type of the property.
	 */
	ASProperties(String str, PropertiesType type) {
		this.key = str;
		this.type = type;
	}

	/**
	 * Returns the key associated to this property
	 * 
	 * @return the key associated to this property
	 */
	public String getKey() {
		return this.key;
	}

	/**
	 * @return the type of this property.
	 */
	public PropertiesType getType() {
		return this.type;
	}

	/**
	 * Returns the value of this property.
	 * 
	 * @return the value of this property.
	 */
	public String getValue() {
		return AutoScalerConfiguration.getInstance().getProperty(this.key);
	}

	/**
	 * @return this property's value as a int.
	 */
	public int getValueAsInt() {
		if (type != PropertiesType.INTEGER) {
			log.error(this.key + " is not an integer property. getValueAsInt cannot be called on this property");
			System.exit(2);
		}

		return Integer.parseInt(this.getValue());
	}

	/**
	 * @return this property's value as a long.
	 * 
	 * @exception IllegalArgumentException
	 *                if the value is not a long.
	 */
	public long getValueAsLong() {
		if (type != PropertiesType.LONG) {
			log.error(this.key	+ " is not a long property. getValueAslong cannot be called on this property");
			System.exit(2);
		}

		return Long.parseLong(this.getValue());
	}

	/**
	 * @return this property's value as a double.
	 * 
	 * @exception IllegalArgumentException
	 *                if the value is not a double.
	 */
	public double getValueAsDouble() {
		if (type != PropertiesType.DOUBLE) {
			log.error(this.key	+ " is not double property. getValueAsDouble cannot be called on this property");
			System.exit(2);
		}
		return Double.parseDouble(this.getValue());
	}

	/**
	 * @return this property's value as a boolean.
	 * 
	 * @exception IllegalArgumentException
	 *                if the value is not a boolean.
	 */
	public boolean getValueAsBoolean() {
		if (this.type != PropertiesType.BOOLEAN) {
			log.error(this.key	+ " is not a boolean. getValueAsBoolean cannot be called on this property");
			System.exit(2);
		}
		return Boolean.parseBoolean(this.getValue());
	}

	/**
	 * Set the value of this property.
	 * 
	 * @param value
	 *            new value of the property.
	 */
	public void setValue(String value) {
		AutoScalerConfiguration.getInstance().setProperty(this.key, value);
	}

	/**
	 * Set the value of this property.
	 * 
	 * @param i
	 *            new value.
	 */
	public void setValue(int i) {
		AutoScalerConfiguration.getInstance().setProperty(this.key,	Integer.toString(i));
	}

	/**
	 * Set the value of this property.
	 * 
	 * @param bool
	 *            new value.
	 */
	public void setValue(boolean bool) {
		AutoScalerConfiguration.getInstance().setProperty(this.key,	Boolean.toString(bool));
	}

	/**
	 * Set the value of this property.
	 * 
	 * @param dbl
	 *            new value.
	 */
	public void setValue(double dbl) {
		AutoScalerConfiguration.getInstance().setProperty(this.key, Double.toString(dbl));
	}

	@Override
	public String toString() {
		return this.key + "=" + this.getValue();
	}

	/**
	 * @return true is this property is set.
	 */
	public boolean isSet() {
		return AutoScalerConfiguration.getInstance().getProperty(key) != null;
	}

	public enum PropertiesType {
		STRING, INTEGER, BOOLEAN, DOUBLE, LONG;
	}

}
