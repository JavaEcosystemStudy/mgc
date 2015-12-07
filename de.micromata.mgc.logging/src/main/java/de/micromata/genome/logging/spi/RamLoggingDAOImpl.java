/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   25.02.2008
// Copyright Micromata 25.02.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.logging.spi;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;

import de.micromata.genome.logging.BaseLogging;
import de.micromata.genome.logging.EndOfSearch;
import de.micromata.genome.logging.LogEntryCallback;
import de.micromata.genome.logging.LogWriteEntry;
import de.micromata.genome.util.types.Pair;

/**
 * Very basic Logging, collecting log entries into a List.
 * 
 * DO NOT USE THIS IN Production, because there will be a outofmemory very fast.
 * 
 * @author roger
 * 
 */
public class RamLoggingDAOImpl extends BaseLogging
{

  /**
   * The log entries.
   */
  public List<LogWriteEntry> logEntries = Collections.synchronizedList(new ArrayList<LogWriteEntry>());

  @Override
  public void doLogImpl(LogWriteEntry lwe)
  {
    if (lwe.getTimestamp() == 0) {
      lwe.setTimestamp(System.currentTimeMillis());
    }
    logEntries.add(lwe);
  }

  @Override
  public boolean supportsSearch()
  {
    return false;
  }

  @Override
  public boolean supportsFulltextSearch()
  {
    return false;
  }

  @Override
  public void selectLogsImpl(List<Object> logId, boolean masterOnly, LogEntryCallback callback) throws EndOfSearch
  {

  }

  @Override
  public void selectLogsImpl(Timestamp start, Timestamp end, Integer loglevel, String category, String msg,
      List<Pair<String, String>> logAttributes, int startRow, int maxRow, List<OrderBy> orderBy, boolean masterOnly,
      LogEntryCallback callback) throws EndOfSearch
  {

  }

  /**
   * Size.
   *
   * @return the int
   */
  public int size()
  {
    return logEntries.size();
  }

  public boolean isEmpty()
  {
    return logEntries.isEmpty();
  }

  /**
   * Clear.
   */
  public void clear()
  {
    logEntries.clear();
  }

  @Override
  public String formatLogId(Object logId)
  {
    return ObjectUtils.toString(logId);
  }

  @Override
  public Object parseLogId(String logId)
  {
    return logId;
  }

  public List<LogWriteEntry> getLogEntries()
  {
    return logEntries;
  }

  public void setLogEntries(List<LogWriteEntry> logEntries)
  {
    this.logEntries = logEntries;
  }

}
