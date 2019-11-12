/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package provision;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static provision.ProvisionerCPUUTIL.log;

/**
 *
 * @author aslanpour
 */
public class Executor {
    
    public static void main(String[] args) {
        String addRemove = "ADD";
        String serverName = "www";
        String serverIP = "10.10.10.10";
        haproxyReconfigurationLocally("ADD", "webTemp", "10.10.0.40");
//        haproxyReconfigurationRemotely(addRemove, serverName, serverIP);
    }
    /**
     * 
     * @param addRemove
     * @param serverName
     * @param serverIP 
     */
    private static void haproxyReconfigurationLocally(String addRemove, String serverName, String serverIP) {
        try {
            //Script Inputs: COMMAND {ADD or REMOVE}, SERVER_NAME, SERVER_IP
            ////sudo bash /home/ubuntu/haproxy_reconfiguration.sh PARA1 PARA2 PARA3
            String command[]= {"sudo", 
                "bash", 
                "/home/ubuntu/haproxy_reconfiguration.sh", 
                addRemove, 
                serverName, 
                serverIP};
            
            ProcessBuilder builder = new ProcessBuilder(command);

            builder.redirectErrorStream(true); // redirect error stream to
            // output stream
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);

            Process p = null;

            try {
                p = builder.start();
                StringBuilder output = new StringBuilder();

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(p.getInputStream()));

		String line;
		while ((line = reader.readLine()) != null) {
			output.append(line + "\n");
		}

		int exitVal = p.waitFor();
		if (exitVal == 0) {
			System.out.println("Success!");
			System.out.println(output);
			System.exit(0);
		} else {
			//abnormal...
		}
            } catch (IOException e) {
                    System.out.println(e);
            }
            p.waitFor();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static void haproxyReconfigurationRemotely(String addRemove, String serverName, String serverIP){
        try {
            //Script Inputs: COMMAND {ADD or REMOVE}, SERVER_NAME, SERVER_IP
            ////sudo bash /home/ubuntu/haproxy_reconfiguration.sh PARA1 PARA2 PARA3
            String[] mycommand = {"sshpass", "-p", "ubuntu", 
                                    "ssh", 
                                    "-o", "StrictHostKeyChecking=no", 
                                    "-o", "UserKnownHostsFile=/dev/null",
                                    "ubuntu@192.168.0.160", 
                                    "bash", 
                                    "/home/ubuntu/haproxy_reconfiguration.sh", 
                                    addRemove, 
                                    serverName, 
                                    serverIP};
            
            ProcessBuilder builder = new ProcessBuilder(mycommand);

            builder.redirectErrorStream(true); // redirect error stream to
            // output stream
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);

            Process p = null;

            try {
                p = builder.start();
                StringBuilder output = new StringBuilder();

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(p.getInputStream()));

		String line;
		while ((line = reader.readLine()) != null) {
			output.append(line + "\n");
		}

		int exitVal = p.waitFor();
		if (exitVal == 0) {
			System.out.println("Success!");
			System.out.println(output);
			System.exit(0);
		} else {
			//abnormal...
		}
            } catch (IOException e) {
                    System.out.println(e);
            }
            p.waitFor();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
}
