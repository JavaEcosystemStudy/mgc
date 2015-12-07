package de.micromata.genome.jpa;

import java.util.Date;

import javax.persistence.EntityManager;

/**
 * Basic interface to an Entitymanager.
 *
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * @param <E> the element type
 */
public interface IEmgr<E extends IEmgr<?>>
{

  EntityManager getEntityManager();

  /**
   * The factory, created this emgr.
   *
   * @return the emgr factory
   */
  EmgrFactory<E> getEmgrFactory();

  /**
   * A virtual now.
   *
   * @return the date
   */
  @Deprecated
  Date now();
}