package provision;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import monitor.Monitor;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import config.ASProperties;
import core.AutoScalerMain;
import core.Node;
import core.NodeStatus;

public class ProvisionerCPUUTIL {
	public static Logger log = LogManager.getLogger(Monitor.class);

	public static void autoscale(double cpuutil) {

		log.info("CPU idle:");

		ArrayList<Node> nodes = AutoScalerMain.getNodes();
		int onservers = 0;
		int noservers = nodes.size();
		synchronized (nodes) {
			for (Node node : nodes) {
				if (node.getStatus().equals(NodeStatus.ON)) {
					onservers++;
					log.info(node.getAddress() + " " + node.getCpu_idle());
				}
			}
		}

		int totalReqServers = (int) Math.ceil(cpuutil
				/ ASProperties.SCALING_THRESHOLD_CPUUTIL.getValueAsDouble());

		int diff = totalReqServers - onservers;
		int k;
		if (diff > 0) {
			k = Math.min(Math.abs(diff), noservers - onservers);
			log.info("Scaling up by " + k);
			scale(true, k);
		} else if (diff < 0) {
			k = Math.min(Math.abs(diff), onservers - 1);
			log.info("Scaling down by " + k);
			scale(false, Math.abs(k));
		} else {
			log.info("No scaling is requried");
		}
	}

	private static void scale(Boolean updown, int cnt) {
		if (cnt == 0)
			return;
		ArrayList<Node> nodes = AutoScalerMain.getNodes();
		ArrayList<Node> candidatenodes = new ArrayList<Node>();
		ArrayList<Node> affectednodes = new ArrayList<Node>();
		synchronized (nodes) {
			for (int i = nodes.size() - 1; i >= 0; i--) {
				Node node = nodes.get(i);
				if (node.getStatus().equals(
						(updown ? NodeStatus.OFF : NodeStatus.ON))) {
					candidatenodes.add(node);
				}
			}
			int k = 0;

			for (Node node : candidatenodes) {
				if (turn(node, updown)) {
					k++;
					affectednodes.add(node);
				}
				if (k == cnt)
					break;
			}
		}

		affectednodesready(affectednodes, updown);

		if (updown) {
			for (Node node : affectednodes)
				startganglia(node);

		}

		updatehaproxy(AutoScalerMain.getNodes());

	}

	private static void startganglia(Node node) {
		try {
			String command = "sshpass -p grid5000 ssh root@"
					+ node.getAddress()
					+ " -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no 'sudo service ganglia-monitor start'";

			log.info(command);

			ProcessBuilder builder = new ProcessBuilder("bash", "-c", command);
			builder.redirectErrorStream(true); // redirect error stream to
												// output stream
			builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);

			Process p = null;

			try {
				p = builder.start();
			} catch (IOException e) {
				System.out.println(e);
			}

			p.waitFor();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void updatehaproxy(ArrayList<Node> nodes) {
		try {
			String command = ". lb.sh";

			for (Node node : nodes) {
				if (node.getStatus() == NodeStatus.ON) {
					command += " " + node.getAddress() + " 1";
				}
			}
			log.info(command);

			ProcessBuilder builder = new ProcessBuilder("bash", "-c", command);
			builder.redirectErrorStream(true); // redirect error stream to
												// output stream
			builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);

			Process p = null;

			try {
				p = builder.start();
			} catch (IOException e) {
				System.out.println(e);
			}
			p.waitFor();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void affectednodesready(ArrayList<Node> affectednodes,
			boolean updown) {
		int cnt = affectednodes.size();

		log.info("Affected nodes:");
		for (Node node : affectednodes) {
			log.info(node.getAddress() + " " + node.getStatus());
		}
		try {
			while (cnt > 0) {
				log.info("Waiting for nodes...");
				Thread.sleep(ASProperties.RESOLUTION_NODE_STATUS_CHECKING
						.getValueAsInt() * 1000);
				for (Node node : affectednodes) {
					log.info("Node "+ node.getAddress() + " Status:" + node.getStatus());
					if (node.getStatus() == (updown ? NodeStatus.ON
							: NodeStatus.OFF)) {
						cnt--;
					}
				}
			}
		} catch (InterruptedException e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			log.error(sw.toString());
		}
	}

	private static boolean turn(Node node, Boolean onoff) {
		try {
			String command = "sshpass -p "
					+ ASProperties.CREDENTIALS_GRID5K_PASSWORD.getValue()
					+ " ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no "
					+ ASProperties.CREDENTIALS_GRID5K_USERNAME.getValue() + "@"
					+ node.getSite() + " '" + "kapower3 --"
					+ (onoff ? "on" : "off") + "  --machine "
					+ node.getAddress() + "'";
			log.info(command);

			ProcessBuilder builder = new ProcessBuilder("bash", "-c", command);
			Process p = null;
			try {
				p = builder.start();
			} catch (IOException e) {
				System.out.println(e);
			}

			p.waitFor();

		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
