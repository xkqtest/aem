package org.training.training.core.wokflow;





import javax.jcr.Node;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkItem;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.exec.WorkflowProcess;
import com.day.cq.workflow.metadata.MetaDataMap;




@Component(service=WorkflowProcess.class,
immediate=true,
property={
		"name = process.label", "value = EPAM WORK process"
})



public class TraingWorkFlow implements WorkflowProcess{
	
	final static Logger logger=LoggerFactory.getLogger(TraingWorkFlow.class);

	@Override
	public void execute(WorkItem workItem, WorkflowSession WorkflowSession, MetaDataMap MetaDataMap) throws WorkflowException {	
		  WorkflowData workflowData=workItem.getWorkflowData();
		  String pathToMove="";
		try {
			if(workflowData.getPayloadType().equals("JCR_PATH")) {
				String path=workflowData.getPayload().toString()+"/jcr:content";			
				
				Node node=(Node) WorkflowSession.getSession().getItem(path);
				logger.info("getName:"+node.getName());
				if(node!=null) {
					try {
						pathToMove=node.getProperty("pathToMove").getString();
					}catch(Exception e) {
						pathToMove=null;
					}
				
						logger.info("pathToMove:"+pathToMove);
					
				}
			}
	    
			
		}catch(Exception e) {
			logger.info(e.getMessage());
			e.printStackTrace();
		}
		
		//logger.info("path:"+workItem.getContentPath());
		
	}
}
