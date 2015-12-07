/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    lado@micromata.de
// Created   Feb 14, 2008
// Copyright Micromata Feb 14, 2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.util.text;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.types.Pair;

/**
 * Internet text protocoll header splitter.
 *
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 */
public class StandardHeaderSplitter
{

  /**
   * Split.
   *
   * @param text the text
   * @return the pair
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static Pair<String, Map<String, String>> split(String text) throws IOException
  {
    LineNumberReader lnr = new LineNumberReader(new StringReader(text));
    Map<String, String> headers = new HashMap<String, String>();
    String line = null;
    boolean leedingNewlines = true;
    while ((line = lnr.readLine()) != null) {
      if (StringUtils.isBlank(line)) {
        if (leedingNewlines == true) {// es kann sein, dass am Anfang die Newlines sind(wegen Code, etc)
          continue;
        } else {
          break;
        }
      }
      String key = StringUtils.substringBefore(line, ":");
      String value = StringUtils.substringAfter(line, ":");
      headers.put(StringUtils.trim(key), StringUtils.trim(value));
      leedingNewlines = false;
    }
    if (headers.size() == 0) {
      return new Pair<String, Map<String, String>>(text, headers);
    }
    return new Pair<String, Map<String, String>>(slurp(lnr), headers);
  }

  /**
   * Slurp.
   *
   * @param in the in
   * @return the string
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static String slurp(Reader in) throws IOException
  {
    StringBuilder sb = new StringBuilder();
    char[] b = new char[4096];
    for (int n; (n = in.read(b)) != -1;) {
      sb.append(new String(b, 0, n));
    }
    return sb.toString();
  }
}
