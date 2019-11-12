package core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import autoscaling.HaProxyMonitor;
import autoscaling.MonitorDef;
import autoscaling.NodeStatusMonitor;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import config.ASProperties;
import config.AutoScalerConfiguration;
import config.CommandLineArgs;

public class AutoScalerMain {

	private final static Logger log = Logger.getLogger(AutoScalerMain.class);
	private static ArrayList<Node> nodes;

	public static void main(String[] args) throws InterruptedException {

		CommandLineArgs values = new CommandLineArgs();
		CmdLineParser parser = new CmdLineParser(values);

		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			parser.printUsage(System.err);
			return;
		}

		if (values.getHelp())
			parser.printUsage(System.err);

		if (values.getConfigfile() != null)
			AutoScalerConfiguration.DEFAULT_PATH = values.getConfigfile();

		String nodesfile = values.getNodesfile();

		setNodes(nodesfile);

		MonitorDef nodestatus = new NodeStatusMonitor(getNodes(), "nodestatus", ASProperties.RESOLUTION_NODE_STATUS_CHECKING.getValueAsInt());
		
                Thread nodestatusThread = new Thread(nodestatus);
                
		nodestatusThread.setDaemon(true);
		nodestatusThread.start();
		
		MonitorDef haproxy = new HaProxyMonitor("haproxymonitor", ASProperties.RESOLUTION_HAPROXY_STAT_CHECKING.getValueAsInt());
		Thread cpuutilThread = new Thread(haproxy);
		cpuutilThread.setDaemon(true);
		cpuutilThread.start();

		while(true){
			for(Node node:nodes)
				log.info(node.getAddress() + " " + node.getStatus());
			Thread.sleep(30000);
		}
		
		
		
	}

	private static void setNodes(String nodesfile) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(nodesfile));

			nodes = new ArrayList<Node>();

			String line = null;
			while ((line = br.readLine()) != null) {
				String[] item = line.split("\\.");
				nodes.add(new Node(line, item[1], NodeStatus.OFF, 1));
			}

			br.close();
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	
	
	

	public static ArrayList<Node> getNodes() {
		return nodes;
	}
}
