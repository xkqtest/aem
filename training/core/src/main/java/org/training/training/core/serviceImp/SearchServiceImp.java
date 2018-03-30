package org.training.training.core.serviceImp;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.training.training.core.SearchService;


public class SearchServiceImp implements SearchService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private Session session;

    @Reference
    private   ResourceResolverFactory resloverFactory;

    @Reference private SlingRepository  repository;

    @Override
    public String getResult() {
        String title;
        String path=null;
        Map<String, Object> param = new HashMap<String, Object>();
        param.put(ResourceResolverFactory.SUBSERVICE, "readService");
        ResourceResolver resourceResolver=null;
        try {
            LOGGER.info("step 1");
            resourceResolver=resloverFactory.getServiceResourceResolver(param);
            session = resourceResolver.adaptTo(Session.class);
            javax.jcr.query.QueryManager queryManager = session.getWorkspace().getQueryManager();
            //String sqlStatement = "SELECT * FROM [cq:PageContent] WHERE CONTAINS(author, 'Sunil')";
            LOGGER.info("step 2");
            String sql1="select * from cq:page where jcr:path like '%/content/geometrixx%' and contain(*,'industry ')";
            javax.jcr.query.Query query = queryManager.createQuery(sql1,"JCR-SQL2");
            javax.jcr.query.QueryResult result = query.execute();
            javax.jcr.NodeIterator nodeIter = result.getNodes();
            LOGGER.info("step 3"+nodeIter.getSize());
            while ( nodeIter.hasNext() ) {
                LOGGER.info("From the search");
                javax.jcr.Node node = nodeIter.nextNode();
                 path=node.getPath();
            /*    String content=
                title = node.getProperty("jcr:title").getString();
                author = node.getProperty("author").getString();*/
               // LOGGER.info(title);
                LOGGER.info(path);
            }
            return path;
        }catch(Exception e){
            e.printStackTrace();
            LOGGER.info("Exception :"+e);
        }
        return "No Value found";
    }

    @Override
    public void addProperty() {

    }
}
