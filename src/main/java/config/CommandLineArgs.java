package config;

import org.kohsuke.args4j.Option;

/**
 * Class with command line options.
 * 
 * @author Adel
 *
 */
public class CommandLineArgs {
	public Boolean getHelp() {
		return help;
	}

	@Option(required = true, name = "-f", aliases = { "--filenodes" }, usage = "Path to a file containing domian/IP addresses of the nodes")
	private String nodesfile;

	@Option(required = false, name = "-c", aliases = { "--config" }, usage = "System confguration file path")
	private String configfile;
	
	@Option(required = false, name = "-h", aliases = { "--help" }, usage = "Help")
	private Boolean help = false;

	public String getConfigfile() {
		return configfile;
	}

	public String getNodesfile() {
		return nodesfile;
	}

}
