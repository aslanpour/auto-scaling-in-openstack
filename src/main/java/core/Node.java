package core;

public class Node {
	public double getCpu_idle() {
		return cpu_idle;
	}
	public void setCpu_idle(double cpu_idle) {
		this.cpu_idle = cpu_idle;
	}

	private String address;
	private NodeStatus status;
	private String site;
	private double cpu_idle; 
	
	public Node(String address, String site, NodeStatus status, double cpu_idle) {
		super();
		this.address = address;
		this.status = status;
		this.site = site;
		this.cpu_idle =  cpu_idle;
	}
	public String getAddress() {
		return address;
	}
	
	public NodeStatus getStatus() {
		return status;
	}
	
	public void setStatus(NodeStatus status) {
		this.status = status;
	}
	
	public String getSite() {
		return site;
	}

}
