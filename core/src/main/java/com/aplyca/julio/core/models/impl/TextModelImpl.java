package com.aplyca.julio.core.models.impl;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.ComponentDataBuilder;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import com.aplyca.julio.core.models.TextModel;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jdk.internal.org.jline.utils.Log;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = {SlingHttpServletRequest.class}, 
                adapters = {TextModel.class, ComponentExporter.class}, 
                resourceType = {"aplyca-julio/components/text"})
@Exporter(name = "jackson", 
                extensions = {"json"})
public class TextModelImpl extends AbstractComponentImpl implements TextModel {
  
  private final Logger LOG = LoggerFactory.getLogger(getClass());
  private static final String COMPONENT_NAME = "Text Component";
    
  @SlingObject
  private SlingHttpServletRequest request;
  
  @ScriptVariable
  private Resource resource;
  
  @ScriptVariable
  private PageManager pageManager;
  
  @ScriptVariable
  private Page currentPage;
  
  @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
  @JsonIgnore
  @Nullable
  private Style currentStyle;
  
  @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL, name = "text")
  @Nullable
  private String text;
  
  @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL, name = "id")
  @Nullable
  private String id;
  
  private String company;
  
  @PostConstruct
  private void initModel() {
    if (this.currentStyle != null) {
      this.company = (String) this.currentStyle.get("company", String.class);
    }
  }
  
  public String getComponentName() {
    return COMPONENT_NAME;
  }

  public String getText() {
    return this.text;
  }

  public String getd() {
    return this.id;
  }
  
  public String getCompany() {
    return this.company;
  }
    
  @NotNull
  protected ComponentData getComponentData() {
    return ((ComponentDataBuilder)((ComponentDataBuilder)DataLayerBuilder.extending(super.getComponentData()).asComponent()
      .withText(this::getText)))
      .build();
  }
}