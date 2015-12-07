package de.micromata.genome.logging.spi.log4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

import de.micromata.genome.logging.GLog;
import de.micromata.genome.logging.LogAttribute;
import de.micromata.genome.logging.LogCategory;
import de.micromata.genome.logging.LogCategoryWrapper;
import de.micromata.genome.logging.LogLevel;
import de.micromata.genome.logging.adapter.GenomeLogAdapterHelper;

/**
 * Pass Log4J Logs into Genome Logging.
 *
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 */
public class GLogAppender extends AppenderSkeleton
{

  /**
   * The log adapter helper.
   */
  private GenomeLogAdapterHelper logAdapterHelper = new GenomeLogAdapterHelper();

  /**
   * Instantiates a new g log appender.
   */
  public GLogAppender()
  {

  }

  /**
   * Instantiates a new g log appender.
   *
   * @param autoRegister the auto register
   * @param thresHold the thres hold
   */
  public GLogAppender(boolean autoRegister, LogLevel thresHold)
  {
    Priority p = Log4JLogging.mapLoglevel(thresHold);
    setThreshold(p);
    if (autoRegister == true) {
      register();
    }
  }

  public void afterPropertiesSet() throws Exception
  {
    activateOptions();
  }

  /**
   * Register.
   */
  public void register()
  {
    Logger root = Logger.getRootLogger();
    // root.setLevel(Level.TRACE);
    // this.setThreshold(threshold)
    root.addAppender(this);
    if (root.getLevel().isGreaterOrEqual(getThreshold())) {
      root.setLevel(Level.toLevel(getThreshold().toInt()));
    }
  }

  /**
   * Creates the cat.
   *
   * @param logName the log name
   * @return the log category
   */
  private LogCategory createCat(String logName)
  {
    // String fqCat = logName;
    if (logAdapterHelper.getStripNamespaceList() != null) {
      for (String sn : logAdapterHelper.getStripNamespaceList()) {
        if (logName.startsWith(sn) == true) {
          logName = logName.substring(sn.length());
          break;
        }
      }
    }

    LogCategoryWrapper cat = new LogCategoryWrapper("L4J", logName);

    return cat;
  }

  /**
   * Gets the message.
   *
   * @param lmsg the lmsg
   * @param logAttributes the log attributes
   * @return the message
   */
  private String getMessage(Object lmsg, List<LogAttribute> logAttributes)
  {
    String msg = ObjectUtils.toString(lmsg);
    return msg;
  }

  /**
   * Map mdc.
   *
   * @param diag the diag
   * @param logAttributes the log attributes
   */
  private void mapMdc(Map<Object, Object> diag, List<LogAttribute> logAttributes)
  {

  }

  /**
   * Ignore cat.
   *
   * @param logName the log name
   * @return true, if successful
   */
  private boolean ignoreCat(String logName)
  {
    if (logAdapterHelper.getIgnoreNamespaceList() == null) {
      return false;
    }
    for (String ign : logAdapterHelper.getIgnoreNamespaceList()) {
      if (logName.startsWith(ign) == true) {
        return true;
      }
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void append(LoggingEvent event)
  {

    if (GenomeLogAdapterHelper.getRecursiveGuard().get() != null
        && GenomeLogAdapterHelper.getRecursiveGuard().get() == Boolean.TRUE) {
      return;
    }

    String logName = event.getLoggerName();
    if (ignoreCat(logName) == true) {
      return;
    }

    try {
      GenomeLogAdapterHelper.getRecursiveGuard().set(Boolean.TRUE);
      Level level = event.getLevel();
      LogLevel logLevel = Log4JLogging.mapLevelToLogLevel(level);

      LogCategory cat = createCat(logName);
      Object omsg = event.getMessage();
      List<LogAttribute> logAttributes = new ArrayList<LogAttribute>();
      String msg = getMessage(omsg, logAttributes);
      // long ts = event.getTimeStamp();

      // String msg = event.getRenderedMessage();
      Map<Object, Object> diag = event.getProperties();
      // String ndc = event.getNDC();
      mapMdc(diag, logAttributes);
      LogAttribute[] la = new LogAttribute[logAttributes.size()];
      la = logAttributes.toArray(la);
      GLog.doLog(logLevel, cat, msg, la);

    } finally {
      GenomeLogAdapterHelper.getRecursiveGuard().set(Boolean.FALSE);
    }

  }

  @Override
  public void close()
  {

  }

  @Override
  public boolean requiresLayout()
  {
    return false;
  }

  public void setIgnoreNamespaces(String ignoreNamespaces)
  {
    logAdapterHelper.setIgnoreNamespaces(ignoreNamespaces);
  }

  public void setStripNamespaces(String stripNamespaces)
  {
    logAdapterHelper.setStripNamespaces(stripNamespaces);
  }

}
