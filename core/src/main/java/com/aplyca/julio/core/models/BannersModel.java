package com.aplyca.julio.core.models;

import com.adobe.cq.wcm.core.components.models.Component;
import com.aplyca.julio.core.beans.BannersBean;

import java.util.List;

import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public interface BannersModel extends Component  {
  
  public String getComponentName();

  public String getContentFragmentPath();
  
  public List<BannersBean> getBanners();
  
  public int getQuantity();
}
