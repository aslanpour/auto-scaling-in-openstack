package monitor;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;



/**
 * @ClassName: Monitor
 * @Description: the base class for monitor
 * @author Chenhao Qu
 * @date 06/06/2015 2:15:34 pm
 * 
 */
public abstract class Monitor implements Runnable {

	/**
	 * @Fields monitorName : the monitor name
	 */
	protected String monitorName;
	/**
	 * @Fields monitorInterval : the monitor interval
	 */
	protected int monitorInterval;
	/**
	 * @Fields monitorLog : the monitor log
	 */
	protected Logger log;


	/**
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param monitorName
	 *            the monitor name
	 * @param monitorInterval
	 *            the monitor interval
	 */
	public Monitor(String monitorName, int monitorInterval) {
		this.log = LogManager.getLogger(Monitor.class);
		this.monitorName = monitorName;
		this.monitorInterval = monitorInterval;
	}

	/*
	 * (non-Javadoc) <p>Title: run</p> <p>Description: </p>
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (true) {
			try {
				// monitor and then sleep the monitor interval
				doMonitoring();
				Thread.sleep(monitorInterval * 1000);
			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				log.error(sw.toString());
			}
		}
	}

	/**
	 * @Title: doMonitoring
	 * @Description: do the actual monitoring
	 * @throws
	 */
	public abstract void doMonitoring() throws Exception;

	/**
	 * @Title: refresh
	 * @Description: manually refresh
	 * @throws
	 */
	public synchronized void refresh() {
		try {
			doMonitoring();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * @Title: getMonitorInterval
	 * @Description: get the monitor interval
	 * @return the monitor interval
	 * @throws
	 */
	public int getMonitorInterval() {
		return monitorInterval;
	}

	/**
	 * @Title: getName
	 * @Description: get the monitor name
	 * @return the monitor name
	 * @throws
	 */
	public String getName() {
		return monitorName;
	}
}