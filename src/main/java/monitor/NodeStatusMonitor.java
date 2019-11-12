package monitor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import core.Node;
import core.NodeStatus;

public class NodeStatusMonitor extends Monitor{
	
	ArrayList<Node> nodes;

	public NodeStatusMonitor(ArrayList<Node> nodes,String monitorName, int monitorInterval) {
		super(monitorName, monitorInterval);
		this.nodes = nodes;
	}

	@Override
	public void doMonitoring() throws Exception {
		
		synchronized(nodes){
			for(Node node:nodes){
				node.setStatus(checkOn(node.getAddress()));
				if(node.getStatus().equals(NodeStatus.ON))
					if(!canSSH(node.getAddress(), 22, 5000))	
						node.setStatus(NodeStatus.PENDING);
			}
		}
		
	}


	private NodeStatus checkOn(String address) {
	    InetAddress inet;
		try {
			inet = InetAddress.getByName(address);
			boolean bool = inet.isReachable(5000);
			return (bool?NodeStatus.ON:NodeStatus.OFF);
		} catch (IOException e) {
			return NodeStatus.OFF;
		}
	}
	
	protected boolean canSSH(String address, int port, int timeout) throws IOException{
		Socket socket = null;
		boolean reaching = false;
		try {
		    socket = new Socket();
		    socket.connect(new InetSocketAddress(address,port), timeout);
		    reaching = true;
		} catch(Exception e) {
		    reaching = false;
		}
		socket.close();
		return reaching;
	}
	
}
