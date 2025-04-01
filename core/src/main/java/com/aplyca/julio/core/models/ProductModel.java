package com.aplyca.julio.core.models;

import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.wcm.core.components.models.Component;

@ConsumerType
public interface ProductModel extends Component  {

  public String getComponentName();
  
}
