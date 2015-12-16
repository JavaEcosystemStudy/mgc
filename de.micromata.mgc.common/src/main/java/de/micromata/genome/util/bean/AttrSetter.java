/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   24.01.2009
// Copyright Micromata 24.01.2009
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.bean;

/**
 * Simply wrapper to set a property in a bean
 * 
 * @author roger@micromata.de
 * 
 */
@FunctionalInterface
public interface AttrSetter<BEAN, VAL>
{

  /**
   * Sets the attr.
   *
   * @param bean the bean
   * @param value the value
   */
  void set(BEAN bean, VAL value);
}
