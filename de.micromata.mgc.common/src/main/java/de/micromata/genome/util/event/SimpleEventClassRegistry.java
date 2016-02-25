package de.micromata.genome.util.event;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.collections15.map.ReferenceMap;
import org.apache.log4j.Logger;

import de.micromata.genome.util.bean.PrivateBeanUtils;
import de.micromata.genome.util.runtime.ClassUtils;

/**
 * Event Registry.
 * 
 * @param <T> the generic type of the event registry.
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 */
public class SimpleEventClassRegistry extends AbstractMgcEventRegistry implements MgcEventClassRegistry

{

  private static final Logger LOG = Logger.getLogger(SimpleEventClassRegistry.class);

  /**
   * The listener map.
   */
  private Map<Class<? extends MgcEvent>, List<Reference<Class<? extends MgcEventListener<?>>>>> listenerMap = new HashMap<>();

  /**
   * Instantiates a new shpmtws config holder.
   * 
   * @param registryName the registryName
   */
  protected SimpleEventClassRegistry(String registryName)
  {
    super(registryName);
  }

  protected void addListener(Class<? extends MgcEvent> event, Class<? extends MgcEventListener<?>> listener)
  {
    Map<Class<? extends MgcEvent>, List<Reference<Class<? extends MgcEventListener<?>>>>> nmp = new ReferenceMap<>(
        ReferenceMap.WEAK,
        ReferenceMap.HARD);
    nmp.putAll(listenerMap);
    List<Reference<Class<? extends MgcEventListener<?>>>> list = nmp.get(event);
    if (list == null) {
      list = new ArrayList<>();
      nmp.put(event, list);
    }
    list.add(new WeakReference<Class<? extends MgcEventListener<?>>>(listener));
    listenerMap = nmp;

  }

  @Override
  public <EVENT extends MgcEvent, LISTENER extends MgcEventListener<EVENT>> void registerListener(
      Class<LISTENER> listenerClass)
  {
    Class<? extends MgcEvent> argType = ClassUtils.getGenericTypeArgument(listenerClass, MgcEvent.class);
    if (argType == null) {
      LOG.error("Cannot determine Event type from Listener class: " + listenerClass.getName());
      return;
    }
    addListener(argType, listenerClass);
  }

  public <EVENT extends MgcEvent, LISTENER extends MgcEventListener<EVENT>> void removeListener(
      Class<LISTENER> listenerClass)
  {
    Class<? extends MgcEvent> argType = ClassUtils.getGenericTypeArgument(listenerClass, MgcEvent.class);
    if (argType == null) {
      LOG.error("Cannot determine Event type from Listener class: " + listenerClass.getName());
      return;
    }
    removeListener(argType, listenerClass);
  }

  protected void removeListener(Class<? extends MgcEvent> event, Class<? extends MgcEventListener<?>> listener)
  {
    Map<Class<? extends MgcEvent>, List<Reference<Class<? extends MgcEventListener<?>>>>> nmp = new ReferenceMap<>(
        ReferenceMap.WEAK,
        ReferenceMap.HARD);
    nmp.putAll(listenerMap);
    List<Reference<Class<? extends MgcEventListener<?>>>> list = nmp.get(event);
    if (list == null) {
      return;
    }
    for (ListIterator<Reference<Class<? extends MgcEventListener<?>>>> lit = list.listIterator(); lit.hasNext();) {
      Reference<Class<? extends MgcEventListener<?>>> ref = lit.next();
      Class<? extends MgcEventListener<?>> cls = ref.get();
      if (cls == listener) {
        lit.remove();
      }
    }
    listenerMap = nmp;

  }

  /**
   * Gets the listener.
   * 
   * @param event the event
   * @return the listener
   */
  List<Class<? extends MgcEventListener<?>>> getListener(MgcEvent event)
  {
    List<Class<? extends MgcEventListener<?>>> ret = new ArrayList<>();
    for (Map.Entry<Class<? extends MgcEvent>, List<Reference<Class<? extends MgcEventListener<?>>>>> me : listenerMap
        .entrySet()) {
      if (me.getKey().isAssignableFrom(event.getClass()) == true) {
        for (Reference<Class<? extends MgcEventListener<?>>> ref : me.getValue()) {
          Class<? extends MgcEventListener<?>> listcls = ref.get();
          if (listcls != null) {
            ret.add(listcls);
          }
        }
      }
    }
    return ret;
  }

  @Override
  public void submitEvent(MgcEvent event)
  {
    dispatchEvent(event);

  }

  /**
   * Invoke events.
   * 
   * @param event the event
   */
  @Override
  public void dispatchEvent(MgcEvent event)
  {

    List<Class<? extends MgcEventListener<?>>> listeners = getListener(event);
    if (listeners == null || listeners.isEmpty() == true) {
      return;
    }
    for (Class<? extends MgcEventListener<?>> listener : listeners) {
      invokeListenerClass(listener, event);
    }
  }

  private void invokeListenerClass(Class<? extends MgcEventListener<?>> listenerClass, MgcEvent event)
  {
    MgcEventListener<?> listener = PrivateBeanUtils.createInstance(listenerClass);
    invokeListener(listener, event);
  }

}