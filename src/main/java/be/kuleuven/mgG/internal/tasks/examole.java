package be.kuleuven.mgG.internal.tasks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

import be.kuleuven.mgG.internal.model.MGGManager;

public class examole extends AbstractTask{
	
	private final MGGManager manager;
	 
	public examole(MGGManager manager){
		this.manager=manager;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		// TODO Auto-generated method stub
		String cxFilePath = "C:\\Users\\herme\\Desktop\\mgg_beta.cx" ; // Change this to your CX file path
	    
	    try {
	      
	        String cxContent = new String(Files.readAllBytes(Paths.get(cxFilePath)));
	        
	        
	        CloseableHttpClient httpClient = HttpClients.createDefault();
	        String cytoscapeAPIURL = "http://localhost:1234/v1/networks?format=cx";
	        
	        
	        HttpPost httpPost = new HttpPost(cytoscapeAPIURL);
	        StringEntity entity = new StringEntity(cxContent);
	        httpPost.setEntity(entity);
	        httpPost.setHeader("Accept", "application/json");
	        httpPost.setHeader("Content-type", "application/json");
	        
	        // Execute and get the response
	        CloseableHttpResponse response = httpClient.execute(httpPost);
	        HttpEntity responseEntity = response.getEntity();
	        
	        if(responseEntity != null) {
	            String result = EntityUtils.toString(responseEntity);
	            System.out.println(result);
	        }
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

}	
		
	

