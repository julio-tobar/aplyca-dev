package com.aplyca.julio.core.models;

import com.adobe.cq.wcm.core.components.models.Component;

import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public interface TextModel extends Component  {

  public String getComponentName();
  
  public String getText();
  
  public String getCompany();
}
