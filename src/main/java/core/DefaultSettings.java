/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

/**
 *
 * @author aslanpour
 */
public class DefaultSettings {
    //File to collect CPU utiliation, located on web servers. 
//    //(1, 2,3, 10, 30, 60)
//    private static String CPU_MONITOR_DURATION = "3"; // second
//    public static final String FILE_LOCATION_CPU_UTILIZATION = "/home/ubuntu/get_cpu_idle"
//            + CPU_MONITOR_DURATION + ".sh";

    //should be <= monitoring interval
    public static final String CPU_LOG_ITEMS = "30";
    public static final String FILE_LOCATION_CPU_UTILIZATION = "/home/ubuntu/cpu_log.txt";
    // file to update haproxy
    public static final String FILE_LOCATION_HAPROXY_RECONFIGURATION = "/home/ubuntu/haproxy_reconfiguration.sh";
    
    // haproxy private key
    public static final String FILE_LOCATION_HAPROXY_PRIVATE_KEY = "/home/ubuntu/mykeypair.pem";
    
    public static final String OS_COMPUTE_API = "http://192.168.0.1:8774/v2.1/192ac977c8034bbd947efac59cdb4725";
    public static final String OS_COMPUTE_API_V2_48 = "http://192.168.0.1:8774/v2.48/192ac977c8034bbd947efac59cdb4725";
    public static final String OS_COMPUTE_API_V3 = "http://192.168.0.1:8774/v3/192ac977c8034bbd947efac59cdb4725";
    public static final String OS_IDENTITY_API = "http://192.168.0.1:5000/v3";
    
    // in our project, 3 refers to medium flavor.
    
    public static final String WEB_SERVER_NAME = "webserver";
    // Default pasword for web servers.
    public static final String WEB_SERVER_PASSWORD = "ubuntu";
    //Default ubuntu user
    public static final String WEB_SERVER_USERNAME = "ubuntu";
    
    // Table of flavors
    public static final String[][] FLAVOR_TABLE = new String[][]{
        //flavor ID, flavor name, VCPUs, RAM, DISK, and price per hour
        {"2", "m1.small", "1", "2", "20", "0.02", "81edade3-9325-4ca1-9d2d-03cb077858b8"},
        {"3", "m1.medium", "2", "4", "40", "0.04", "739ab0a2-ad02-4d54-8cd9-d5ecedce513f"}
    };
    
    // the ID of web server snapshot
    // small snapshot:    [0][6]
    // medium snapshot:   [1][6]
    public static final String OS_COMPUTE_IMAGE_ID = FLAVOR_TABLE[0][6];
    public static final String OS_NEUTRON_NETWORK_UUID_PRIVATE = "e291c471-deca-4dc6-a593-f2e089bb6d86";
    public static final String OS_NEUTRON_SECURITYGROUP_NAME = "AutoscalingSecurityGroup";
    public static final String OS_COMPUTE_KEYPAIRS_NAME = "mykeypair"; 
    public static final String OS_NOVA_AVAILIBILITY_ZONE = "nova";
    
    /** Auto-scaling **/
    public static int INITIAL_WEB_SERVERS = 2;// web servers to begin with
    //Monitoring
    public static int MONITORING_INTERVAL = 60 * 1000; //millisecond
    public static int SCALING_INTERVAL = 120 * 1000;// millisecond
    
    public static final String HAPROXY_API = "http://192.168.0.160/haproxy?stats;csv";
    //Analyzing
    public static String ANALYSIS_METHOD_CPU = Method.SIMPLE.name();
    public static String ANALYSIS_METhOD_RT = Method.SIMPLE.name();
    public static String ANALYSIS_METHOD_REQ = Method.SIMPLE.name();
    
    public static double ANALYSIS_SES_ALPHA = 0.2;
    public static int ANALYSIS_TIME_WINDOW = SCALING_INTERVAL / MONITORING_INTERVAL;
    public enum Method {
        SIMPLE, //returns just the current observed paramteres
        COMPLEX_MA, //is Moving Average 
        COMPLEX_WMA, //is Weighted Moving Average 
        COMPLEX_WMAfibo, //is Fibonacci Weighted Moving Average, i.e., weighting by Fibonacci numbers
        COMPLEX_SES; //is Single Exponential Smoothing 
        
    }
    //planner
    public static ScalingRule rule = ScalingRule.LOAD_AWARE;
    public enum ScalingRule {
        /* please cite: https://ieeexplore.ieee.org/abstract/document/7498443/ */
        RESOURCE_AWARE, // Resource aware, - Decision-making by Resource utilization rule (Amazon default policy)
        SLA_AWARE, //      SLA aware - Decision making by delay time rule
        HYBRID, //        Resource-and-SLA aware - Decision making by both resource utilization and delay time rules
        LOAD_AWARE,
        LOAD_RESOURCE_AWARE,
        
        /* please cite: https://www.sciencedirect.com/science/article/pii/S1389128612003763 */
        UT_1Al, //    'Util.-based One Alaram'- Decision-making by resources utilization
        UT_2Al, //    'Util.-based Two alarms' - Decision-making by resources utilization
        LAT_1Al, //   'Latency Two alarms' - Decision-making by latency (i.e., delay)
        LAT_2Al; //   'Latency Two alarms' - Decision-making by latency (i.e., delay) */
    }  
    public static double PLANNER_CPU_UP = 70;
    public static double PLANNER_CPU_DOWN = 30;
    
    public static double PLANNER_RT_UP = 1;
    public static double PLANNER_RT_DOWN = 0.2;
    
    public static double PLANNER_REQ_UP = 7;
    public static double PLANNER_REQ_DOWN = 4;
    
    public static int PLANNER_StEP_SIZE = 1;
    
    public enum PlannerDecision {
        SCALE_UP,
        SCALE_DOWN,
        DO_NOTHING
    }
    //Executor
    public static final SurplusVMSelectionPolicy surplusVMSelectionPolicy = 
                                                SurplusVMSelectionPolicy.THE_OLDEST; 
    public enum SurplusVMSelectionPolicy{
        /* when performing scale down decisions, it is neccessary to choose a policy for selecting surplus VM
            Implemented policies are:*/
        RANDOM, // selects the VM randomly
        THE_OLDEST, // selects the oldest VM (like Amazon default policy)
        THE_YOUNGEST, // selects the youngest VM 
        RESOURCE_AWARE,
        LOAD_AWARE, // selects the VM which has the lowest load
        COST_AWARE, // selects the the most cost-efficient VM 
    }
    public static boolean COOLDOWN_ENABLED = false;
    public static int COOLDOWN = 0;
    public static int MAX_ALLOWED_WEB_SERVER = 10;
    public static int MIN_ALLOWED_WEB_SERVER = 1;
    
    
    //ip net: 10.10.0.0/24 
    public static final String netIP = "10.10.0.";
    public static int[] alreadyAllocatedIPs = new int[]{7, 8, 9, 12, 39};
    
    public static final String EXECUTOR_SCALING_FLAVOR_ID = FLAVOR_TABLE[0][0];
    public static final boolean VM_QUARANTINING = false;
    //tag
    public enum Action {
        SCALE_UP,
        SCALE_DOWN,
        DO_NOTHING,
        SCALE_UP_BANNED_BY_COOLDOWN,
        SCALE_UP_BANNED_BY_MAX_ALLOWED_VM,
        SCALE_UP_REDUCED_BY_MAX_ALLOWED_VM,
        SCALE_DOWN_NO_VM_EXIST,
        SCALE_DOWN_REDUCED_BY_MAX_ALLOWED,
        SCALE_DOWN_BANNED_BY_MIN_ALLOWED_VM
    }
    
    public static final int TEST_TERMINATION_COUNTER = 5;
}
