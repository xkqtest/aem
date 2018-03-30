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
package org.training.training.core.filters;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.engine.EngineConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.training.training.core.servlets.ResponseWrapper;

/**
 * Simple servlet filter component that logs incoming requests.
 */
@Component(service = Filter.class,
           property = {
                   Constants.SERVICE_DESCRIPTION + "=Demo to filter incoming requests",
                   EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
                   Constants.SERVICE_RANKING + ":Integer=-700"

           })
public class SlingFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,
                         final FilterChain filterChain) throws IOException, ServletException {

        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
        ResponseWrapper wrapper=new ResponseWrapper((HttpServletResponse) response);
     filterChain.doFilter(request, wrapper);
 
     String regEx1 = ".*algebra.*";
     String regEx2 = ".*trigo.*";
     
  
     String path=slingRequest.getPathInfo();
    // logger.info("info);
     
     if(null!=wrapper) {
    	 String result = wrapper.getResult();  
    	 String contentType=wrapper.getContentType();
    	 try {
	        if (null!=contentType&&(contentType.indexOf("JSON")>-1)||(contentType.indexOf("text")>-1)&&path.indexOf("traning")>-1) {
	        	if(path.matches(regEx1)) {
	        		result = result.replace("Lorem", "Algebraixx"); 
	        	}else if(path.matches(regEx2)) {
	        		result = result.replace("Lorem", "Trigonometrixx"); 
	        	}else {
	        		result = result.replace("Lorem", "Johnson"); 	
	        	}
	        	 
	            PrintWriter out = response.getWriter();  
	            out.write(result);  
	            out.flush();  
	            out.close(); 
	        }
    	 }catch(Exception e) {
    		 logger.error(e.getMessage());
    	 }
	        
     }


    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}