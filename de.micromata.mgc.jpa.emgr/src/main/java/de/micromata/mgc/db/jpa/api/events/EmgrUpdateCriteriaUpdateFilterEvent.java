package de.micromata.mgc.db.jpa.api.events;

import de.micromata.mgc.db.jpa.CriteriaUpdate;
import de.micromata.mgc.db.jpa.api.IEmgr;

/**
 * Wrapps a de.micromata.genome.jpa.Emgr.update(CriteriaUpdate<T>).
 *
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * @param <E> the element type
 */
public class EmgrUpdateCriteriaUpdateFilterEvent<E>extends EmgrFilterEvent<Integer>
{

  /**
   * The update.
   */
  private CriteriaUpdate<E> update;

  /**
   * Instantiates a new emgr update criteria update filter event.
   *
   * @param emgr the emgr
   * @param update the update
   */
  public EmgrUpdateCriteriaUpdateFilterEvent(IEmgr<?> emgr, CriteriaUpdate<E> update)
  {
    super(emgr);
    this.update = update;
  }

  public CriteriaUpdate<E> getUpdate()
  {
    return update;
  }
}
