package de.micromata.genome.chronos.manager;

import java.util.ServiceLoader;

import de.micromata.genome.chronos.ChronosConfigurationService;

/**
 * Provides empty jobs/scheduler.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class DefaultChronosConfigurationServiceImpl implements ChronosConfigurationService
{
  SchedulerManager schedulerManager = null;

  @Override
  public SchedulerManager getScheduleManager()
  {
    if (schedulerManager != null) {
      return schedulerManager;
    }
    synchronized (this) {
      if (schedulerManager != null) {
        return schedulerManager;
      }
      schedulerManager = new SchedulerManager();
      init(schedulerManager);
      return schedulerManager;
    }

  }

  protected void init(SchedulerManager manager)
  {
    ServiceLoader<ChronosSchedulerInitService> services = ServiceLoader.load(ChronosSchedulerInitService.class);
    for (ChronosSchedulerInitService initService : services) {
      initService.initSchedulerManager(manager);
    }

  }
  // TODO MGC
  //  @Override
  //  public SchedulerManager getSchedulerManager()
  //  {
  //    DynBeanConfig dbc = getChronosBeanConfig();
  //    if (dbc == null) {
  //      return null;
  //    }
  //    SchedulerManager manager = (SchedulerManager) dbc.getBean("schedulerManager");
  //    manager.init();
  //    return manager;
  //  }
  //
  //  private static DynBeanConfig getChronosBeanConfig()
  //  {
  //    GenomeDaoManager gdm = GenomeDaoManager.get();
  //    return (DynBeanConfig) gdm.getTimependingDAO().getVersionResource(null, "CONTEXT_CHRONOS", false);
  //  }
}
