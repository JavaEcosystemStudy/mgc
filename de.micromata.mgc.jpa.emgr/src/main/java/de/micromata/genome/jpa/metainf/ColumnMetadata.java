package de.micromata.genome.jpa.metainf;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * The Interface ColumnMetadata.
 *
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 */
public interface ColumnMetadata extends EmgrDbElement
{
  /**
   * Java Property name.
   * 
   * @return
   */
  String getName();

  /**
   * Gets the max length.
   *
   * @return the max length
   */
  int getMaxLength();

  /**
   * Comes form Column anotation
   * 
   * @return
   */
  boolean isUnique();

  /**
   * Checks if is nullable.
   *
   * @return true, if is nullable
   */
  boolean isNullable();

  /**
   * Checks if is insertable.
   *
   * @return true, if is insertable
   */
  boolean isInsertable();

  /**
   * Checks if is updatable.
   *
   * @return true, if is updatable
   */
  boolean isUpdatable();

  /**
   * Gets the column definition.
   *
   * @return the column definition
   */
  String getColumnDefinition();

  /**
   * Gets the precision.
   *
   * @return the precision
   */
  int getPrecision();

  /**
   * Gets the scale.
   *
   * @return the scale
   */
  int getScale();

  /**
   * Checks if is association.
   *
   * @return true, if is association
   */
  boolean isAssociation();

  /**
   * Checks if is collection.
   *
   * @return true, if is collection
   */
  boolean isCollection();

  /**
   * Annotation from getter and field.
   *
   * @return the annotations
   */
  List<Annotation> getAnnotations();
}