package org.hawaiiframework.logging.model;

import static java.lang.String.format;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * A definition for a path and method.
 */
public class PathDefinition {

  private final PathMatcher matcher;
  private String pattern;
  private String method;

  /**
   * Default constructor.
   */
  public PathDefinition() {
    this.matcher = new AntPathMatcher();
  }

  /**
   * Constructor for the given {@code pattern}.
   */
  public PathDefinition(String pattern) {
    this(null, pattern);
  }

  /**
   * Constructor for the given {@code method} and {@code pattern}.
   */
  public PathDefinition(String method,
      String pattern) {
    this();
    this.pattern = pattern;
    this.method = method;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getPattern() {
    return pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  /**
   * Check whether the method and path matches this definition.
   */
  public boolean matches(String method,
      String path) {
    boolean matches = true;
    if (this.method != null) {
      matches = this.method.equals(method);
    }

    return matches && matcher.match(this.pattern, path);
  }

  @Override
  public String toString() {
    if (method != null) {
      return format("%s %s", method, pattern);
    }
    return pattern;
  }
}
