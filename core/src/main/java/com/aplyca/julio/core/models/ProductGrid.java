package com.aplyca.julio.core.models;

import com.adobe.cq.wcm.core.components.models.Component;

import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public interface ProductGrid extends Component  {

  public String getComponentName();
  
}
