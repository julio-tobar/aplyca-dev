package com.aplyca.julio.core.models.impl;

import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.commerce.common.CommerceHelper;
import com.adobe.cq.wcm.core.components.models.List;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.aplyca.julio.core.models.ProductGrid;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = {SlingHttpServletRequest.class}, 
                adapters = {List.class}, 
                resourceType = {"aplyca-julio/components/productgrid"})
@Exporter(name = "jackson", 
extensions = {"json"})
public class ProductGridImpl implements ProductGrid, List {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(ProductGridItemImpl.class);
  private static final String COMPONENT_NAME = "Product Grid Component";
  
  @Self
  @Via(type = ResourceSuperType.class)
  private List delegate;
  
  @ScriptVariable
  private PageManager pageManager;
  
  public Collection<ListItem> getListItems() {
    return (Collection<ListItem>)this.delegate.getListItems().stream().map(listItem -> {
          Page page = this.pageManager.getPage(listItem.getPath());
          if (page != null) {
            Product product = CommerceHelper.findCurrentProduct(page);
            if (product != null)
              return listItem; 
          } 
          return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
  }
  
  public boolean linkItems() {
    return this.delegate.linkItems();
  }
  
  public boolean showDescription() {
    return this.delegate.showDescription();
  }
  
  public boolean showModificationDate() {
    return this.delegate.showModificationDate();
  }
  
  public String getDateFormatString() {
    return this.delegate.getDateFormatString();
  }
  
  public String getExportedType() {
    return this.delegate.getExportedType();
  }

  @Override
  public String getComponentName() {
    return COMPONENT_NAME;
  }
}
