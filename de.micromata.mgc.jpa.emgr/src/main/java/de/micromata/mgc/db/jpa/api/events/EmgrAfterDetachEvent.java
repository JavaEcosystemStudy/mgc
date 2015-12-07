package de.micromata.mgc.db.jpa.api.events;

import de.micromata.mgc.db.jpa.api.IEmgr;

/**
 * Event will invoked, if an entity was detached from entitymanager.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class EmgrAfterDetachEvent extends EmgrForEntityObjectEvent
{

  /**
   * Instantiates a new emgr after detach event.
   *
   * @param emgr the emgr
   * @param entity the entity
   */
  public EmgrAfterDetachEvent(IEmgr<?> emgr, Object entity)
  {
    super(emgr, entity);
  }

}
