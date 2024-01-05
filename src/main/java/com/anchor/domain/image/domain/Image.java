package com.anchor.domain.image.domain;

import com.anchor.global.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Image extends BaseEntity {

  @Column
  private String url;

  private Image(String url) {
    this.url = url;
  }

  public static Image of(String url) {
    return new Image(url);
  }

}
