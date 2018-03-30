/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.training.training.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.RowIterator;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@Component(service=Servlet.class,
           property={
                   Constants.SERVICE_DESCRIPTION + "=Servlet",
                   "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                   "sling.servlet.paths="+ "/bin/training/search",
                   "sling.servlet.extensions=" + "txt"
           })


public class SearchServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUid = 1L;
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    StringBuffer path=new StringBuffer();
    @Reference ResourceResolverFactory resourceResolverFactory;
    private Session session;


    @Override
    protected void doGet(final SlingHttpServletRequest request,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
    	 Map<String, Object> param = new HashMap<String, Object>();
    	 param.put(ResourceResolverFactory.USER,"admin");
    	 param.put(ResourceResolverFactory.PASSWORD,"admin".toCharArray());
        // param.put(ResourceResolverFactory.SUBSERVICE, "readPage");
         try {
        	 logger.info("step 1");
        	 path=new StringBuffer();
            ResourceResolver resourceResolver = resourceResolverFactory.getResourceResolver(param);
        	    session = resourceResolver.adaptTo(Session.class);
        	    QueryManager queryManager= session.getWorkspace().getQueryManager();             
             
             String sqlStatement = "select * from [cq:Page] where [jcr:path] like '%/content/geometrixx%' and (contains(*,'industry') or contains(*,'successful'))";
             Query query = queryManager.createQuery(sqlStatement, Query.JCR_SQL2);           
             QueryResult result = query.execute();
             NodeIterator nodeIter = result.getNodes();
             logger.info("step 4 NodeIterator size:"+nodeIter.getSize());
             String tmpPth;
             while ( nodeIter.hasNext() ) {
            	 logger.info("From the search");
                 Node node = nodeIter.nextNode();
                 tmpPth=node.getPath();
                 path.append(tmpPth+"\n");
                 logger.info(tmpPth);
             }
         }catch(Exception e){
             e.printStackTrace();
             logger.info("Exception :"+e);
         }
         resp.setContentType("text/plain");
         resp.getWriter().write("html:\n"+path.toString());
    }
}
