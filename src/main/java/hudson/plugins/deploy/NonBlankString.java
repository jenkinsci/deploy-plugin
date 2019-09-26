package hudson.plugins.deploy;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Represents a string that cannot be blank / empty / null
 * @author Julien Béti - julien.beti@cosium.com
 * @since 1.16
 */
public abstract class NonBlankString implements Serializable {
  private String value;
  private NonBlankString(){}
  protected NonBlankString(String value) {
    if(StringUtils.isBlank(value)) {
      throw new IllegalArgumentException("Value of non blank string can't be blank");
    }
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
