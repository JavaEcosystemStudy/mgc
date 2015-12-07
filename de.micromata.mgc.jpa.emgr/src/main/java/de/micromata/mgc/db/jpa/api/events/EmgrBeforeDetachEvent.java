package de.micromata.mgc.db.jpa.api.events;

import de.micromata.mgc.db.jpa.api.IEmgr;

/**
 * Event will be invoked before entity will be detached.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class EmgrBeforeDetachEvent extends EmgrForEntityObjectEvent
{

  /**
   * Instantiates a new emgr before detach event.
   *
   * @param emgr the emgr
   * @param entity the entity
   */
  public EmgrBeforeDetachEvent(IEmgr<?> emgr, Object entity)
  {
    super(emgr, entity);
  }

}
