package hudson.plugins.deploy.websphere;

import hudson.FilePath; 
import hudson.plugins.deploy.ContainerAdapter; 
import java.io.File; 
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.Session;
import com.ibm.websphere.management.application.AppConstants;
import com.ibm.websphere.management.application.AppManagement;
import com.ibm.websphere.management.application.AppManagementProxy;
import com.ibm.websphere.management.application.AppNotification;
import com.ibm.websphere.management.exception.AdminException;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.management.filetransfer.client.FileTransferClient;
import com.ibm.ws.management.fileservice.FileTransferFactory;

/**
 * Base class for WebSphere adapters.
 * 
 * @author Antonio Sanso
 */
public abstract class WebSphereAdapter extends ContainerAdapter  {

	public final String url;
	private String hostName;
	private String connectPort; 
	private Hashtable prefs;
	private String earFile;
	private String appName;
	private AdminClient adminClient;
	private AppManagement appProxy;
	private String cellName;
	private String nodeName;
	private String serverInstance;
	private String installedAppsDir;
	private final String waitObject = "waitObject";
	private boolean successFlag = true;
	private boolean isUpdate;
	private String taskType = null;
	private Properties waitProps = null;	 
	private String workspaceId;
	private Session session; 

	private static final int HTTP_PREFIX=7;

	public WebSphereAdapter(String url) {		 
		this.url=url;
		prefs = new Hashtable();
		session = new Session();
		workspaceId = session.toString();	
	}

	private void setApplication(FilePath war) throws IOException, InterruptedException{	 
		this.earFile=war.absolutize().toString();		
		String appNameTemp=earFile.substring(earFile.lastIndexOf("\\")+1);		 
		this.appName=appNameTemp.split("\\.")[0];		 
	}

	private void createAppMgmtProxy() throws Exception{ 
		appProxy = AppManagementProxy.getJMXProxyForClient(this.adminClient); 
	}

	private void createAdminClient() throws ConnectorException{		 
		Properties connectionProps = new Properties();
		connectionProps.put (AdminClient.CONNECTOR_HOST,  this.hostName);
		connectionProps.put (AdminClient.CONNECTOR_PORT,  this.connectPort);
		connectionProps.put (AdminClient.CONNECTOR_TYPE, AdminClient.CONNECTOR_TYPE_SOAP);
		connectionProps.put ("open.securityEnabled", "false");
		// Get an AdminClient based on the connector properties
		adminClient = AdminClientFactory.createAdminClient(connectionProps);
	}

	private void setTargetServerString() throws ConnectorException{
		ObjectName mBean;
		mBean = adminClient.getServerMBean();			 
		if(mBean != null){
			cellName = mBean.getKeyProperty("cell");
			nodeName = mBean.getKeyProperty("node");
			serverInstance = mBean.getKeyProperty("name");
		}		 
	}

	private String getTargetServerString(){
		StringBuffer targetServerString = new StringBuffer();		 
		targetServerString.append("WebSphere:cell=");
		targetServerString.append(cellName);
		targetServerString.append(",node=");
		targetServerString.append(nodeName);
		targetServerString.append(",server=");
		targetServerString.append(serverInstance);
		return targetServerString.toString();
	}

	private void setInstalledAppsDir()throws Exception{
		String stagingLocation = null;
		FileTransferClient ftc = null;
		String installedAppsIPath = null;
		StringBuffer stb=new StringBuffer();
		ftc = FileTransferFactory.getFileTransferClient(adminClient);
		stagingLocation = ftc.getServerStagingLocation();
		installedAppsIPath = stagingLocation; 
		if(installedAppsIPath.contains("temp")){
			installedAppsIPath = installedAppsIPath.substring(0,installedAppsIPath.lastIndexOf("temp"));				 				
		}
		if(installedAppsIPath.contains("config")){
			installedAppsIPath = installedAppsIPath.substring(0,installedAppsIPath.lastIndexOf("config"));				 				
		}
		stb.append(installedAppsIPath).append("installedApps").append(File.separator).append(cellName);
		installedAppsDir = stb.toString();		 
	}

	private void setPreferences(){ 
		Hashtable module2server = new Hashtable();
		module2server.put("*", getTargetServerString());
		prefs.put("moduleToServer", module2server);
		prefs.put("classLoadingMode", "1");
		prefs.put (AppConstants.APPDEPL_MODULE_TO_SERVER, module2server);		 
	}

	private AdminAppJMXClient addJMXListener(String type) throws MalformedObjectNameException, NullPointerException, ConnectorException, InstanceNotFoundException{
		AdminAppJMXClient adminAppJMXClient=null;
		NotificationFilterSupport myFilter = new NotificationFilterSupport ();
		myFilter.enableType (AppConstants.NotificationType);
		adminAppJMXClient = new AdminAppJMXClient(myFilter, (new StringBuilder()).append("Install: ").append(appName).toString(), type);
		return adminAppJMXClient;
	}

	private void setNotfStatus(boolean flag){
		successFlag = flag;
	}

	private boolean getNotfStatus(){
		return successFlag;
	}

	private void setHostnameAndPort(){		
		String urlTemp=url.substring(HTTP_PREFIX);
		this.hostName=urlTemp.split(":")[0];
		this.connectPort=urlTemp.split(":")[1];		 
	}

	public void installApplication(FilePath war)throws Exception{	
		setHostnameAndPort();
		setApplication(war);
		createAdminClient();
		createAppMgmtProxy();
		AdminAppJMXClient adminAppJMXClient=addJMXListener("InstallApplication");
		setTargetServerString(); 
		setInstalledAppsDir();
		setPreferences();
		setNotfStatus(false);
		synchronized (waitObject){
			if(appProxy.checkIfAppExists(appName, prefs, null)){					 
				isUpdate = true;
				taskType = AppNotification.UPDATE;
				prefs.put("contenttype", "app");			 			
				appProxy.updateApplication(appName, null, earFile, "update", prefs, workspaceId);
				waitObject.wait(86400000); //oneday in milliseconds						 
			} else{					 
				isUpdate = false;
				taskType = AppNotification.INSTALL;
				appProxy.installApplication(earFile, appName, prefs, null);
				waitObject.wait(86400000); //oneday in milliseconds 
			}
		}
		if (getNotfStatus()){			 			
			String appStatus = getDistributionStatus(appName);
			int count = 0;
			while (appStatus != null && appStatus.equals(AppNotification.DISTRIBUTION_NOT_DONE) && count < 10){		 
				Thread.sleep(1000);
				count++;
				appStatus =getDistributionStatus(appName);
			}
			if (appStatus != null){
				if (appStatus.equals(AppNotification.DISTRIBUTION_DONE)){					 
					if(!isUpdate){						 
						adminAppJMXClient.startApplication(appName);
					}
				}
				/*else if (appStatus.equals(AppNotification.DISTRIBUTION_UNKNOWN)){				 
				}
				else{					 
				}
			}else{*/				 
			}
		}
	}

	private String getDistributionStatus(String appName) throws AdminException, InterruptedException, MalformedObjectNameException, NullPointerException{

		setNotfStatus(false);
		synchronized (waitObject){
			taskType = AppNotification.DISTRIBUTION_STATUS_NODE;
			appProxy.getDistributionStatus(appName, new Hashtable(), session.getSessionId());
			waitObject.wait(86400000); //oneday in milliseconds
		}
		String appStatus = (waitProps != null) ? waitProps.getProperty(AppNotification.DISTRIBUTION_STATUS_COMPOSITE) : null;			 
		ObjectName on = new ObjectName(appStatus);
		return on.getKeyProperty("distribution");
	}

	//INNER CLASS
	class AdminAppJMXClient implements NotificationListener{
		NotificationFilterSupport filterSupport;
		Object handback;
		ObjectName appMBean;
		String type;
		NotificationFilterSupport filter;

		public AdminAppJMXClient(NotificationFilterSupport filter, Object handback, String type) throws MalformedObjectNameException, NullPointerException, ConnectorException, InstanceNotFoundException{		
			this.filterSupport = filter;
			this.handback = handback;
			this.type = type;
			this.filter = filter;
			setAppManagementMBean();
			registerNotificationListener();
		}

		private void setAppManagementMBean() throws MalformedObjectNameException, NullPointerException, ConnectorException{
			// Query for the ObjectName of the AppManagement MBean
			String query = "WebSphere:type=AppManagement,*";
			ObjectName queryName = new ObjectName(query);
			Set s = adminClient.queryNames(queryName, null);
			if (!s.isEmpty()) {
				appMBean = (ObjectName)s.iterator().next();
			}
			/*else{					 
			}*/
		}

		private void registerNotificationListener() throws InstanceNotFoundException, ConnectorException{
			adminClient.addNotificationListener(appMBean, this, this.filter, this.handback);				
		}

		public void handleNotification(Notification notification,Object handback) {
			AppNotification ev = (AppNotification)notification.getUserData ();			 
			if ((ev.taskName.equals("InstallApplication")   ||ev.taskName.equals("AppDistributionNode")) &&
					(ev.taskStatus.equals (AppNotification.STATUS_COMPLETED) ||
							ev.taskStatus.equals (AppNotification.STATUS_FAILED))){
				synchronized (waitObject){
					if (AppNotification.STATUS_COMPLETED.equals (ev.taskStatus)){
						setNotfStatus(true);
					}
					else{
						setNotfStatus(false);
					}
					taskType = null;
					waitProps = ev.props;
					waitObject.notify();
				}
			}/*else{				 
			}*/
		}

		private String startApplication(String appName) throws InstanceNotFoundException, MBeanException, ReflectionException, ConnectorException{

			return (String) adminClient.invoke(appMBean, "startApplication",
					new Object[] {
					appName,
					new Hashtable(),
					null},
					new String[] {
					String.class.getName(),
					Hashtable.class.getName(),
					String.class.getName()}
			);
		}
	}


}
