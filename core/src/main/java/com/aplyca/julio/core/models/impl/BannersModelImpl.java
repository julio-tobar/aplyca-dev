package com.aplyca.julio.core.models.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.jcr.RepositoryException;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import javax.jcr.Node;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.ComponentDataBuilder;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import com.aplyca.julio.core.beans.BannersBean;
import com.aplyca.julio.core.models.BannersModel;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;
import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jdk.internal.org.jline.utils.Log;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = {SlingHttpServletRequest.class}, 
                adapters = {BannersModel.class, ComponentExporter.class}, 
                resourceType = {"aplyca-julio/components/banners"})
@Exporter(name = "jackson", 
                extensions = {"json"})
public class BannersModelImpl extends AbstractComponentImpl implements BannersModel {
  
  private final Logger LOG = LoggerFactory.getLogger(getClass());
  private static final String COMPONENT_NAME = "Banners Component";
  private static final String CONTENT_FRAGMENT_MODEL = "/conf/aplyca-julio/settings/dam/cfm/models/aplyca-banners";

  @Self
  private SlingHttpServletRequest request;
  
  @ScriptVariable
  private Resource resource;
  
  @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
  @JsonIgnore
  @Nullable
  private Style currentStyle;

  @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
  @Nullable
  private String contentFragmentPath;
  
  @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
  @Nullable
  private String id;
  
  private int quantity = 6;
  
  @PostConstruct
  private void initModel() {
    if ((this.currentStyle != null) && (this.currentStyle.get("quantity") != null)) {
      this.quantity = (int) this.currentStyle.get("quantity", Integer.class);
    }
  }
  
  public String getComponentName() {
    return COMPONENT_NAME;
  }

  public String getContentFragmentPath() {
    return contentFragmentPath;
  }
  
  public String getId() {
    return this.id;
  }
  
  public int getQuantity() {
    return this.quantity;
  }
  
  public List<BannersBean> getBanners() {
    List<BannersBean> bannersBean = null;
    try {
      /* perform query */
      // get query manager
      ResourceResolver resourceResolver = resource.getResourceResolver();
      Session session = resourceResolver.adaptTo(Session.class);
      QueryManager queryManager = session.getWorkspace().getQueryManager();
      // create query
      String queryStmt = "SELECT asset.* "
          + " FROM [dam:Asset] as asset INNER JOIN [dam:AssetContent] as jcrContent ON ISCHILDNODE(jcrContent, asset)  INNER JOIN [nt:unstructured] as data ON ISCHILDNODE (data, jcrContent)"
          + " WHERE ISDESCENDANTNODE(asset, '" + contentFragmentPath + "')"
          + " AND data.[cq:model] = '" + CONTENT_FRAGMENT_MODEL + "'";
      Query query = queryManager.createQuery(queryStmt, Query.JCR_SQL2);
      // execute query
      QueryResult result = query.execute();

      /* Get the nodes from the query result */
      NodeIterator nodeIterator = result.getNodes();
      int quantity = (int) (this.quantity < nodeIterator.getSize() ? this.quantity : nodeIterator.getSize());
      bannersBean = new ArrayList<BannersBean>();
      BannersBean bannersBeanObj = null;
      while (nodeIterator.hasNext() && (quantity > 0)) {
          bannersBeanObj = new BannersBean();
          Node node = nodeIterator.nextNode();
          // get the content fragment
          Resource resource = resourceResolver.getResource(node.getPath());
          ContentFragment contentFragment = resource.adaptTo(ContentFragment.class);
          // get the elements in the CF
          Iterator<ContentElement> contentElementIterator = contentFragment.getElements();
          while (contentElementIterator.hasNext()) {
            ContentElement contentElement= contentElementIterator.next();
            /* populate bean. */
            /* It is not a best practice to have the been implement specific property names (ie: title, subtitle, etc) */
            /* it is best practice to use a valueMap where the name and the value can be setup*/
            switch (contentElement.getName()) {
              case "title": bannersBeanObj.setTitle(contentElement.getContent()); break;
              case "subtitle": bannersBeanObj.setSubtitle(contentElement.getContent()); break;
              case "link": bannersBeanObj.setLink(contentElement.getContent()); break;
              case "linkText": bannersBeanObj.setLinkText(contentElement.getContent()); break;
              case "backgroundImage": bannersBeanObj.setBackgroundImage(contentElement.getContent()); break;
            }
          }
          bannersBean.add(bannersBeanObj);
          quantity --;
      }
    } catch (RepositoryException e) {
      e.printStackTrace();
    }
    return bannersBean;
  }

 
  @NotNull
  protected ComponentData getComponentData() {
    return ((ComponentDataBuilder)((ComponentDataBuilder)DataLayerBuilder.extending(super.getComponentData()).asComponent()
     .withId(this::getId)))
      .build();
  }
}