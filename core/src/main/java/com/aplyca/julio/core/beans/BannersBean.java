package com.aplyca.julio.core.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BannersBean {
  private String title;
  private String subtitle;
  private String linkText;
  private String link;
  private String backgroundImage;
}
