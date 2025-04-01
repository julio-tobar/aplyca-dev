package com.aplyca.julio.core.models;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceSession;
import com.adobe.cq.commerce.api.Product;
import com.day.cq.commons.ImageResource;
import com.day.cq.wcm.api.Page;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductItem {
  private static final Logger LOGGER = LoggerFactory.getLogger(ProductItem.class);
  
  private static final String PN_FEATURES = "features";
  
  private static final String PN_SUMMARY = "summary";
  
  private String path;
  
  private String pagePath;
  
  private String sku;
  
  private String title;
  
  private String description;
  
  private String price;
  
  private String summary;
  
  private String features;
  
  private String imageUrl;
  
  private String thumbnailUrl;
  
  private List<ProductItem> variants = new ArrayList<>();
  
  private List<String> variantAxes = new ArrayList<>();
  
  private Map<String, String> variantAxesMap = new LinkedHashMap<>();
  
  public ProductItem(Product product, CommerceSession commerceSession, SlingHttpServletRequest request, Page currentPage) {
    this(product, commerceSession, request, currentPage, null);
  }
  
  private ProductItem(Product product, CommerceSession commerceSession, SlingHttpServletRequest request, Page currentPage, ProductItem baseProductItem) {
    ResourceResolver resourceResolver = request.getResourceResolver();
    this.path = product.getPath();
    this.pagePath = product.getPagePath();
    if (StringUtils.isNotBlank(this.pagePath))
      this.pagePath = resourceResolver.map((HttpServletRequest)request, this.pagePath); 
    Locale currentLocale = currentPage.getLanguage(false);
    this.sku = product.getSKU();
    this.title = product.getTitle(currentLocale.getLanguage());
    this.description = product.getDescription(currentLocale.getLanguage());
    this.summary = (String)product.getProperty("summary", currentLocale.getLanguage(), String.class);
    this.features = (String)product.getProperty("features", currentLocale.getLanguage(), String.class);
    ImageResource image = product.getImage();
    this.imageUrl = (image != null) ? image.getFileReference() : null;
    if (StringUtils.isNotBlank(this.imageUrl))
      this.imageUrl = resourceResolver.map((HttpServletRequest)request, this.imageUrl); 
    this.thumbnailUrl = product.getThumbnailUrl(120);
    if (StringUtils.isNotBlank(this.thumbnailUrl))
      this.thumbnailUrl = resourceResolver.map((HttpServletRequest)request, this.thumbnailUrl); 
    if (commerceSession != null)
      try {
        this.price = commerceSession.getProductPrice(product);
      } catch (CommerceException e) {
        LOGGER.error("Error getting the product price: {}", (Throwable)e);
      }  
    if (baseProductItem == null) {
      String[] productVariantAxes = (String[])product.getProperty("cq:productVariantAxes", String[].class);
      if (productVariantAxes != null)
        setVariantAxes(productVariantAxes); 
      populateAllVariants(product, commerceSession, request, currentPage);
    } else {
      populateVariantAxesValues(baseProductItem.variantAxes, product);
    } 
  }
  
  private void populateAllVariants(Product product, CommerceSession commerceSession, SlingHttpServletRequest request, Page currentPage) {
    try {
      Iterator<Product> productVariants = product.getVariants();
      while (productVariants.hasNext()) {
        ProductItem variant = new ProductItem(productVariants.next(), commerceSession, request, currentPage, this);
        this.variants.add(variant);
      } 
      if (this.variants.isEmpty())
        this.variants.add(this); 
    } catch (CommerceException e) {
      LOGGER.error("Error getting the product variants: {}", (Throwable)e);
    } 
  }
  
  private void populateVariantAxesValues(List<String> variantAxes, Product product) {
    for (String variantAxis : variantAxes) {
      String value = (String)product.getProperty(variantAxis, String.class);
      if (value != null && !this.variantAxesMap.containsKey(variantAxis))
        this.variantAxesMap.put(variantAxis, value); 
    } 
  }
  
  private void setVariantAxes(String[] variantAxes) {
    if (variantAxes != null)
      for (String axis : variantAxes)
        this.variantAxes.add(axis.trim());  
  }
  
  public String getPath() {
    return this.path;
  }
  
  public String getPagePath() {
    return this.pagePath;
  }
  
  public String getSku() {
    return this.sku;
  }
  
  public String getTitle() {
    return this.title;
  }
  
  public String getDescription() {
    return this.description;
  }
  
  public String getPrice() {
    return this.price;
  }
  
  public String getSummary() {
    return this.summary;
  }
  
  public String getFeatures() {
    return this.features;
  }
  
  public String getImageUrl() {
    return this.imageUrl;
  }
  
  public String getThumbnailUrl() {
    return this.thumbnailUrl;
  }
  
  public List<ProductItem> getVariants() {
    return Collections.unmodifiableList(this.variants);
  }
  
  public String getVariantValueForAxis(String axis) {
    return this.variantAxesMap.get(axis);
  }
  
  public String getVariantAxesMapJson() {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    this.variantAxesMap.entrySet().forEach(e -> builder.add((String)e.getKey(), (String)e.getValue()));
    return builder.build().toString();
  }
  
  public Map<String, Collection<String>> getVariantsAxesValues() {
    if (this.variants.isEmpty() || this.variantAxes.isEmpty())
      return Collections.emptyMap(); 
    Map<String, Collection<String>> map = new LinkedHashMap<>();
    for (String axis : this.variantAxes) {
      for (ProductItem variant : this.variants) {
        String axisValue = variant.variantAxesMap.get(axis);
        if (axisValue != null) {
          Collection<String> set = map.get(axis);
          if (set == null) {
            set = new LinkedHashSet<>();
            map.put(axis, set);
          } 
          if (!set.contains(axisValue))
            set.add(axisValue); 
        } 
      } 
    } 
    return map;
  }
}
