package com.aplyca.julio.core.models.impl;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceService;
import com.adobe.cq.commerce.api.CommerceSession;
import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.commerce.common.CommerceHelper;
import com.day.cq.commons.ImageResource;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = {SlingHttpServletRequest.class})
public class ProductGridItemImpl {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(ProductGridItemImpl.class);
  
  @SlingObject
  private ResourceResolver resourceResolver;
  
  @SlingObject
  private Resource resource;
  
  @Self
  private SlingHttpServletRequest request;
  
  @SlingObject
  private SlingHttpServletResponse response;
  
  @ScriptVariable
  private PageManager pageManager;
  
  private boolean exists;
  
  private String image;
  
  private String name;
  
  private String description;
  
  private String price;
  
  private String path;
  
  private ProductFilters filters;
  
  @PostConstruct
  private void initModel() {
    Page productPage = this.pageManager.getContainingPage(this.resource.getPath());
    Product currentProduct = CommerceHelper.findCurrentProduct(productPage);
    if (currentProduct == null) {
      this.exists = false;
      return;
    } 
    ImageResource imageResource = currentProduct.getImage();
    if (imageResource == null) {
      this.exists = false;
      return;
    } 
    this.exists = true;
    this.image = imageResource.getPath();
    try {
      CommerceService commerceService = (CommerceService)this.resource.adaptTo(CommerceService.class);
      if (commerceService != null) {
        CommerceSession commerceSession = commerceService.login(this.request, this.response);
        Product pimProduct = currentProduct.getPIMProduct();
        this.name = pimProduct.getTitle();
        this.description = pimProduct.getDescription();
        this.price = commerceSession.getProductPrice(pimProduct);
        this.path = productPage.getPath();
        this.filters = getProductFilters(pimProduct, commerceSession);
      } 
    } catch (CommerceException e) {
      LOGGER.error("Can't extract product information from the resource", (Throwable)e);
    } 
  }
  
  public boolean exists() {
    return this.exists;
  }
  
  public String getImage() {
    return this.image;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getDescription() {
    return this.description;
  }
  
  public String getPrice() {
    return this.price;
  }
  
  public String getPath() {
    return this.path;
  }
  
  public ProductFilters getFilters() {
    return this.filters;
  }
  
  private ProductFilters getProductFilters(Product product, CommerceSession commerceSession) throws CommerceException {
    String variationAxis = (String)product.getProperty("cq:productVariantAxes", String.class);
    ProductFilters productFilters = new ProductFilters();
    if (StringUtils.isNotEmpty(variationAxis)) {
      Iterator<Product> unorderedVariations = product.getVariants();
      while (unorderedVariations.hasNext()) {
        Product productVariation = unorderedVariations.next();
        ProductProperties variation = new ProductProperties(productVariation, commerceSession);
        if (StringUtils.isNotEmpty(variation.getColor()))
          productFilters.setColor(variation.getColor().toLowerCase()); 
        if (StringUtils.isNotEmpty(variation.getSize()))
          productFilters.setSize(variation.getSize()); 
        productFilters.setPrice(variation.getPrice());
      } 
    } else {
      String color = (String)product.getProperty("color", String.class);
      if (StringUtils.isNotEmpty(color))
        productFilters.setColor(color.toLowerCase()); 
      productFilters.setSize((String)product.getProperty("size", String.class));
      productFilters.setPrice(this.price);
    } 
    return productFilters;
  }
  
  public class ProductFilters {
    private Set<String> colors = new HashSet<>();
    
    private Set<String> sizes = new HashSet<>();
    
    private Set<String> prices = new HashSet<>();
    
    public Set<String> getColors() {
      return Collections.unmodifiableSet(this.colors);
    }
    
    public void setColor(String color) {
      this.colors.add(color);
    }
    
    public Set<String> getSizes() {
      return Collections.unmodifiableSet(this.sizes);
    }
    
    public void setSize(String size) {
      this.sizes.add(size);
    }
    
    public Set<String> getPrices() {
      return Collections.unmodifiableSet(this.prices);
    }
    
    public void setPrice(String price) {
      this.prices.add(price);
    }
  }
  
  public class ProductProperties {
    private Product product;
    
    private CommerceSession commerceSession;
    
    private String path;
    
    private Iterator<String> variants;
    
    private String sku;
    
    private String title;
    
    private String description;
    
    private String color;
    
    private String colorClass;
    
    private String size;
    
    private String price;
    
    private String summary;
    
    private String features;
    
    private String image;
    
    public ProductProperties(Product product, CommerceSession commerceSession) {
      this.product = product;
      this.commerceSession = commerceSession;
    }
    
    public String getPath() {
      if (this.path == null)
        this.path = this.product.getPath(); 
      return this.path;
    }
    
    public Iterator<String> getVariants() {
      if (this.variants == null)
        this.variants = this.product.getVariantAxes(); 
      return this.variants;
    }
    
    public String getSku() {
      if (this.sku == null)
        this.sku = this.product.getSKU(); 
      return this.sku;
    }
    
    public String getTitle() {
      if (this.title == null)
        this.title = this.product.getTitle(); 
      return this.title;
    }
    
    public String getDescription() {
      if (this.description == null)
        this.description = this.product.getDescription(); 
      return this.description;
    }
    
    public String getColor() {
      if (this.color == null)
        this.color = (String)this.product.getProperty("color", String.class); 
      return this.color;
    }
    
    public String getColorClass() {
      if (this.colorClass == null) {
        String color = getColor();
        if (color != null)
          this.colorClass = color.toLowerCase(); 
      } 
      return this.colorClass;
    }
    
    public String getSize() {
      if (this.size == null)
        this.size = (String)this.product.getProperty("size", String.class); 
      return this.size;
    }
    
    public String getPrice() {
      if (this.price == null)
        try {
          this.price = this.commerceSession.getProductPrice(this.product);
        } catch (CommerceException e) {
          ProductGridItemImpl.LOGGER.error("Error getting the product price: {}", (Throwable)e);
        }  
      return this.price;
    }
    
    public String getSummary() {
      if (this.summary == null)
        this.summary = (String)this.product.getProperty("summary", String.class); 
      return this.summary;
    }
    
    public String getFeatures() {
      if (this.features == null)
        this.features = (String)this.product.getProperty("features", String.class); 
      return this.features;
    }
    
    public String getImage() {
      if (this.image == null) {
        ImageResource imageResource = this.product.getImage();
        if (imageResource != null)
          this.image = imageResource.getFileReference(); 
      } 
      return this.image;
    }
  }
  
}

