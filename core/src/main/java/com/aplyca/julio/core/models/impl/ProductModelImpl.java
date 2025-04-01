package com.aplyca.julio.core.models.impl;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceService;
import com.adobe.cq.commerce.api.CommerceSession;

import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import com.adobe.granite.security.user.UserManagementService;
import com.day.cq.wcm.api.Page;
import javax.annotation.PostConstruct;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aplyca.julio.core.models.ProductItem;
import com.aplyca.julio.core.models.ProductModel;
import com.aplyca.julio.core.models.TextModel;
import com.aplyca.julio.core.models.handler.CommerceHandler;

@Model(adaptables = {SlingHttpServletRequest.class},
                      adapters = {ProductModel.class, ComponentExporter.class}, 
                      resourceType = {"aplyca-julio/components/product"})
@Exporter(name = "jackson", 
                    extensions = {"json"})
public class ProductModelImpl extends AbstractComponentImpl implements ProductModel {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(ProductModelImpl.class);
  private static final String COMPONENT_NAME = "Product Component";
  
  @SlingObject
  private Resource resource;
  
  @SlingObject
  private SlingHttpServletRequest request;
  
  @SlingObject
  private SlingHttpServletResponse response;
  
  @SlingObject
  private ResourceResolver resourceResolver;
  
  @ScriptVariable
  private Page currentPage;
  
  @Self
  private CommerceHandler commerceHandler;
  
  @OSGiService
  private UserManagementService ums;
  
  private CommerceService commerceService;
  
  private ProductItem productItem;
  
  private boolean isAnonymous;
  
  @PostConstruct
  private void initModel() {
    try {
      this.commerceService = (CommerceService)this.currentPage.getContentResource().adaptTo(CommerceService.class);
      if (this.commerceService != null) {
        Product product;
        CommerceSession commerceSession = this.commerceService.login(this.request, this.response);
        if (this.commerceHandler.isProductPageProxy()) {
          product = this.commerceHandler.getProduct();
        } else {
          product = (Product)this.resource.adaptTo(Product.class);
        } 
        if (product != null)
          this.productItem = new ProductItem(product, commerceSession, this.request, this.currentPage); 
      } 
    } catch (CommerceException e) {
      LOGGER.error("Can't extract product from page", (Throwable)e);
    } 
    String anonymousId = (this.ums != null) ? this.ums.getAnonymousId() : "anonymous";
    this.isAnonymous = (this.resourceResolver.getUserID() == null || anonymousId.equals(this.resourceResolver.getUserID()));
  }
  
  public ProductItem getProductItem() {
    return this.productItem;
  }
  
  public boolean hasVariants() {
    return (this.productItem != null && !this.productItem.getVariants().isEmpty());
  }
  
  public String getAddToCartUrl() {
    return this.commerceHandler.getAddToCardUrl();
  }
  
  public String getAddToSmartListUrl() {
    return this.commerceHandler.getAddToSmartListUrl();
  }
  
  public String getProductTrackingPath() {
    return this.commerceHandler.getProductTrackingPath();
  }
  
  public boolean isAnonymous() {
    return this.isAnonymous;
  }
  
  public String getComponentName() {
    return COMPONENT_NAME;
  }
}
