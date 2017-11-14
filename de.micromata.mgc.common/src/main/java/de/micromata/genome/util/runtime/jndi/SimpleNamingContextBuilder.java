//
// Copyright (C) 2010-2016 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.genome.util.runtime.jndi;

import java.util.Hashtable;

import java.util.Objects;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.NamingManager;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * TODO Not longer needed.
 * 
 * Simple implementation of a JNDI naming context builder.
 *
 * <p>
 * Mainly targeted at test environments, where each test case can configure JNDI appropriately, so that
 * {@code new InitialContext()} will expose the required objects. Also usable for standalone applications, e.g. for
 * binding a JDBC DataSource to a well-known JNDI location, to be able to use traditional J2EE data access code outside
 * of a J2EE container.
 *
 * <p>
 * There are various choices for DataSource implementations:
 * <ul>
 * <li>{@code SingleConnectionDataSource} (using the same Connection for all getConnection calls)
 * <li>{@code DriverManagerDataSource} (creating a new Connection on each getConnection call)
 * <li>Apache's Commons DBCP offers {@code org.apache.commons.dbcp.BasicDataSource} (a real pool)
 * </ul>
 *
 * <p>
 * Typical usage in bootstrap code:
 *
 * <pre class="code">
 * SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
 * DataSource ds = new DriverManagerDataSource(...);
 * builder.bind("java:comp/env/jdbc/myds", ds);
 * builder.activate();
 * </pre>
 *
 * Note that it's impossible to activate multiple builders within the same JVM, due to JNDI restrictions. Thus to
 * configure a fresh builder repeatedly, use the following code to get a reference to either an already activated
 * builder or a newly activated one:
 *
 * <pre class="code">
 * SimpleNamingContextBuilder builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
 * DataSource ds = new DriverManagerDataSource(...);
 * builder.bind("java:comp/env/jdbc/myds", ds);
 * </pre>
 *
 * Note that you <i>should not</i> call {@code activate()} on a builder from this factory method, as there will already
 * be an activated one in any case.
 *
 * <p>
 * An instance of this class is only necessary at setup time. An application does not need to keep a reference to it
 * after activation.
 * </p>
 * 
 * @author Juergen Hoeller
 * @author Rod Johnson
 */
public class SimpleNamingContextBuilder implements InitialContextFactoryBuilder
{

  /**
   * The Constant logger.
   */
  private static final Logger logger = Logger.getLogger(SimpleNamingContextBuilder.class);

  /**
   * An instance of this class bound to JNDI.
   */
  private static volatile SimpleNamingContextBuilder activated;

  /**
   * The initialized.
   */
  private static boolean initialized = false;

  /**
   * The Constant initializationLock.
   */
  private static final Object initializationLock = new Object();

  /**
   * The bound objects.
   */
  private final Hashtable<String, Object> boundObjects = new Hashtable<>();

  /**
   * Checks if a SimpleNamingContextBuilder is active.
   * 
   * @return the current SimpleNamingContextBuilder instance, or {@code null} if none
   */
  public static SimpleNamingContextBuilder getCurrentContextBuilder()
  {
    return activated;
  }

  /**
   * If no SimpleNamingContextBuilder is already configuring JNDI, create and activate one. Otherwise take the existing
   * activate SimpleNamingContextBuilder, clear it and return it.
   * <p>
   * This is mainly intended for test suites that want to reinitialize JNDI bindings from scratch repeatedly.
   *
   * @return an empty SimpleNamingContextBuilder that can be used to control JNDI bindings
   * @throws NamingException the naming exception
   */
  public static SimpleNamingContextBuilder emptyActivatedContextBuilder() throws NamingException
  {
    if (activated != null) {
      // Clear already activated context builder.
      activated.clear();
    } else {
      // Create and activate new context builder.
      SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
      // The activate() call will cause an assignment to the activated variable.
      builder.activate();
    }
    return activated;
  }

  /**
   * Register the context builder by registering it with the JNDI NamingManager. Note that once this has been done,
   * {@code new InitialContext()} will always return a context from this factory. Use the
   * {@code emptyActivatedContextBuilder()} static method to get an empty context (for example, in test methods).
   *
   * @throws IllegalStateException if there's already a naming context builder registered with the JNDI NamingManager
   * @throws NamingException the naming exception
   */
  public void activate() throws IllegalStateException, NamingException
  {
    logger.info("Activating simple JNDI environment");
    synchronized (initializationLock) {
      if (!initialized) {
        if (NamingManager.hasInitialContextFactoryBuilder()) {
          throw new IllegalStateException(
              "Cannot activate SimpleNamingContextBuilder: there is already a JNDI provider registered. " +
                  "Note that JNDI is a JVM-wide service, shared at the JVM system class loader level, " +
                  "with no reset option. As a consequence, a JNDI provider must only be registered once per JVM.");
        }
        NamingManager.setInitialContextFactoryBuilder(this);
        initialized = true;
      }
    }
    activated = this;
  }

  /**
   * Temporarily deactivate this context builder. It will remain registered with the JNDI NamingManager but will
   * delegate to the standard JNDI InitialContextFactory (if configured) instead of exposing its own bound objects.
   * <p>
   * Call {@code activate()} again in order to expose this context builder's own bound objects again. Such
   * activate/deactivate sequences can be applied any number of times (e.g. within a larger integration test suite
   * running in the same VM).
   * 
   * @see #activate()
   */
  public void deactivate()
  {
    logger.info("Deactivating simple JNDI environment");
    activated = null;
  }

  /**
   * Clear all bindings in this context builder, while keeping it active.
   */
  public void clear()
  {
    this.boundObjects.clear();
  }

  /**
   * Bind the given object under the given name, for all naming contexts that this context builder will generate.
   * 
   * @param name the JNDI name of the object (e.g. "java:comp/env/jdbc/myds")
   * @param obj the object to bind (e.g. a DataSource implementation)
   */
  public void bind(String name, Object obj)
  {
    if (logger.isInfoEnabled()) {
      logger.info("Static JNDI binding: [" + name + "] = [" + jndiObjectToString(obj) + "]");
    }
    this.boundObjects.put(name, obj);
  }

  /**
   * Simple InitialContextFactoryBuilder implementation, creating a new SimpleNamingContext instance.
   * 
   * @see SimpleNamingContext
   */
  @Override
  public InitialContextFactory createInitialContextFactory(Hashtable<?, ?> environment)
  {
    if (activated == null && environment != null) {
      Object icf = environment.get(Context.INITIAL_CONTEXT_FACTORY);
      if (icf != null) {
        Class<?> icfClass;
        if (icf instanceof Class) {
          icfClass = (Class<?>) icf;
        } else if (icf instanceof String) {
          icfClass = resolveClassName((String) icf, getClass().getClassLoader());
        } else {
          throw new IllegalArgumentException("Invalid value type for environment key [" +
              Context.INITIAL_CONTEXT_FACTORY + "]: " + icf.getClass().getName());
        }
        if (!InitialContextFactory.class.isAssignableFrom(icfClass)) {
          throw new IllegalArgumentException(
              "Specified class does not implement [" + InitialContextFactory.class.getName() + "]: " + icf);
        }
        try {
          return (InitialContextFactory) icfClass.newInstance();
        } catch (Throwable ex) {
          throw new IllegalStateException("Cannot instantiate specified InitialContextFactory: " + icf, ex);
        }
      }
    }

    // Default case...
    return new InitialContextFactory()
    {
      @Override
      @SuppressWarnings("unchecked")
      public Context getInitialContext(Hashtable<?, ?> environment)
      {
        return new SimpleNamingContext("", boundObjects, (Hashtable<String, Object>) environment);
      }
    };
  }

  /**
   * Resolve class name.
   *
   * @param className the class name
   * @param classLoader the class loader
   * @return the class
   * @throws IllegalArgumentException the illegal argument exception
   */
  public static Class<?> resolveClassName(String className, ClassLoader classLoader) throws IllegalArgumentException
  {
    try {
      return forName(className, classLoader);
    } catch (ClassNotFoundException ex) {
      throw new IllegalArgumentException("Cannot find class [" + className + "]", ex);
    } catch (LinkageError ex) {
      throw new IllegalArgumentException(
          "Error loading class [" + className + "]: problem with class file or dependent class.", ex);
    }
  }

  /**
   * For name.
   *
   * @param name the name
   * @param classLoader the class loader
   * @return the class
   * @throws ClassNotFoundException the class not found exception
   * @throws LinkageError the linkage error
   */
  public static Class<?> forName(String name, ClassLoader classLoader) throws ClassNotFoundException, LinkageError
  {
    return Class.forName(name);
  }

  /**
   * Jndi object to string.
   *
   * @param obj the obj
   * @return the string
   */
  public static String jndiObjectToString(Object obj)
  {
    if (obj instanceof BasicDataSource) {
      BasicDataSource bds = (BasicDataSource) obj;
      return "BasicDataSource: " + bds.getUsername() + "@" + bds.getUrl();
    } else {
      return Objects.toString(obj, StringUtils.EMPTY);
    }
  }

  public Hashtable<String, Object> getBoundObjects()
  {
    return boundObjects;
  }

}
