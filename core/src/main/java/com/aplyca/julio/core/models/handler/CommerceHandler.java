package com.aplyca.julio.core.models.handler;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.commerce.common.CommerceHelper;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

@Model(adaptables = {SlingHttpServletRequest.class})
public class CommerceHandler {
  private static final String ADD_CART_ENTRY_SELECTOR = ".weretail.addcartentry.html";
  
  private static final String ADD_SMARTLIST_ENTRY_SELECTOR = ".commerce.smartlist.management.html";
  
  private static final String ADD_SELECTOR = ".add.html";
  
  private static final String PN_ADD_TO_CART_REDIRECT = "addToCartRedirect";
  
  private static final String PN_CART_ERROR_REDIRECT = "cartErrorRedirect";
  
  private static final String REQ_ATTR_CQ_COMMERCE_PRODUCT = "cq.commerce.product";
  
  private static final String PN_PRODUCT_DATA = "productData";
  
  @SlingObject
  private Resource resource;
  
  @Self
  private SlingHttpServletRequest request;
  
  @SlingObject
  private ResourceResolver resourceResolver;
  
  @ScriptVariable
  private Style currentStyle;
  
  @RequestAttribute(name = "cq.commerce.cartPage", injectionStrategy = InjectionStrategy.OPTIONAL)
  private String cartPage;
  
  @RequestAttribute(name = "cq.commerce.cartObject", injectionStrategy = InjectionStrategy.OPTIONAL)
  private String cartObject;
  
  @RequestAttribute(name = "cq.commerce.prodNotFoundPage", injectionStrategy = InjectionStrategy.OPTIONAL)
  private String productNotFound;
  
  @RequestAttribute(name = "cq.commerce.product", injectionStrategy = InjectionStrategy.OPTIONAL)
  private Product product;
  
  private String addToCardUrl;
  
  private String addToSmartListUrl;
  
  private Page currentPage;
  
  private String redirectUrl;
  
  private String errorRedirectUrl;
  
  private boolean productPageProxy = false;
  
  private String productTrackingPath;
  
  @PostConstruct
  private void initHandler() throws CommerceException {
    PageManager pageManager = (PageManager)this.resourceResolver.adaptTo(PageManager.class);
    this.currentPage = pageManager.getContainingPage(this.resource);
    this.addToCardUrl = this.currentPage.getPath() + ".weretail.addcartentry.html";
    this.addToSmartListUrl = this.currentPage.getPath() + ".commerce.smartlist.management.html";
    this.redirectUrl = CommerceHelper.mapPathToCurrentLanguage(this.currentPage, (String)this.currentStyle
        .get("addToCartRedirect", ""));
    this.errorRedirectUrl = CommerceHelper.mapPathToCurrentLanguage(this.currentPage, (String)this.currentStyle
        .get("cartErrorRedirect", ""));
    if (StringUtils.isEmpty(this.redirectUrl) && StringUtils.isNotEmpty(this.cartObject)) {
      this.redirectUrl = this.cartPage;
      this.errorRedirectUrl = this.productNotFound;
      this.addToCardUrl = this.cartObject + ".add.html";
    } 
    if (StringUtils.isEmpty(this.redirectUrl) || StringUtils.equals(this.redirectUrl, "."))
      this.redirectUrl = this.currentPage.getPath(); 
    if (StringUtils.isEmpty(this.errorRedirectUrl))
      this.errorRedirectUrl = this.currentPage.getPath(); 
    if (StringUtils.isNotBlank(this.addToCardUrl))
      this.addToCardUrl = this.resourceResolver.map((HttpServletRequest)this.request, this.addToCardUrl); 
    if (StringUtils.isNotBlank(this.addToSmartListUrl))
      this.addToSmartListUrl = this.resourceResolver.map((HttpServletRequest)this.request, this.addToSmartListUrl); 
    if (this.product == null) {
      this.product = (Product)this.resource.adaptTo(Product.class);
    } else {
      this.productPageProxy = true;
    } 
    if (this.product != null) {
      this.productTrackingPath = (String)this.product.getProperty("productData", String.class);
      if (StringUtils.isEmpty(this.productTrackingPath))
        this.productTrackingPath = this.product.getPagePath(); 
      setRequestAttributes();
    } 
  }
  
  private void setRequestAttributes() {
    this.request.setAttribute("cq.commerce.product", this.product);
  }
  
  public String getAddToCardUrl() {
    return this.addToCardUrl;
  }
  
  public String getAddToSmartListUrl() {
    return this.addToSmartListUrl;
  }
  
  public String getRedirectUrl() {
    return this.redirectUrl;
  }
  
  public String getErrorRedirectUrl() {
    return this.errorRedirectUrl;
  }
  
  public boolean isProductPageProxy() {
    return this.productPageProxy;
  }
  
  public Product getProduct() {
    return this.product;
  }
  
  public String getProductTrackingPath() {
    return this.productTrackingPath;
  }
}
