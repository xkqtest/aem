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

import java.awt.Dimension;
import java.io.IOException;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import com.day.cq.commons.ImageHelper;
import com.day.cq.commons.ImageResource;
import com.day.cq.commons.SlingRepositoryException;
import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.commons.AbstractImageServlet;
import com.day.cq.wcm.foundation.Image;
import com.day.image.Layer;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@Component(service=Servlet.class,
property={
        Constants.SERVICE_DESCRIPTION + "=Image Servlet",
        "sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.resourceTypes =" + "sling/servlet/default",
        "sling.servlet.selectors=" + "ud",
        "sling.servlet.extensions=" + "jpg"
})


public class ImageServlet extends AbstractImageServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;



    /**
     * {@inheritDoc}
     *
     * Override default ImageResource creation to support assets
     */
    @Override
    protected ImageResource createImageResource(Resource resource) {
        return new Image(resource);
    }

    
    @Override
    protected void writeLayer(SlingHttpServletRequest req,
                              SlingHttpServletResponse resp,
                              ImageContext c, Layer layer)
            throws IOException, RepositoryException {
        try {
        	layer.write(getImageType(), getImageQuality(), resp.getOutputStream());
        }catch(Exception e) {
        	e.printStackTrace();
        	 return;
        }

    	 
       // resp.flushBuffer();resp
    }
    
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException{
     Session session = null;
     try {   
       String extension = request.getRequestPathInfo().getExtension();
       String imagePath = request.getRequestPathInfo().getResourcePath().substring(0, request.getRequestPathInfo().getResourcePath().indexOf("."));
      
       String type = getImageType(extension);
          if (type == null) {
            response.sendError(404, "Image type not supported");
            return;
          }
          response.setContentType(type);
        
          ImageContext context = new ImageContext(request, type);     
          session = (Session)request.getResourceResolver().adaptTo(Session.class);
        
          Resource resource = context.request.getResourceResolver().getResource(imagePath+"."+extension);
          Asset asset = resource.adaptTo(Asset.class);
        
          Layer layer = ImageHelper.createLayer(session.getNode(imagePath+"."+extension).getSession(), asset.getOriginal().getPath());
               

          if (layer != null) {
        	  layer.rotate(180);
         // layer.rotati (maxWidth, maxHeight, true);
              applyDiff(layer, context);
            }     
          writeLayer(request, response, context, layer);
        
     } catch (RepositoryException e) {
          throw new SlingRepositoryException(e);
       }finally{
        if(session != null)
         session.logout();
       }
     }
    
    protected Layer createLayer(ImageContext paramImageContext) throws RepositoryException, IOException {
     return null;
    }
    
     @Override 
     protected String getImageType()
     {
       return "image/jpg";
     }
    @Override
    protected String getImageType(String ext)
    {
      if ("png".equals(ext))
        return "image/png";
      if ("gif".equals(ext))
        return "image/gif";
      if (("jpg".equals(ext)) || ("jpeg".equals(ext))) {
        return "image/jpg";
      }
      return null;
    }
}
