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

package de.micromata.genome.db.jpa.tabattr.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.micromata.genome.db.jpa.tabattr.api.AttrGroup;
import de.micromata.genome.db.jpa.tabattr.api.EntityWithTimeableAttr;
import de.micromata.genome.db.jpa.tabattr.api.TimeableAttrRow;
import de.micromata.genome.db.jpa.tabattr.api.TimeableService;

/**
 * Standard implementation for TimeableService.
 */
public class TimeableServiceImpl<PK extends Serializable, T extends TimeableAttrRow<PK>> implements TimeableService<PK, T>
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(TimeableServiceImpl.class);

  @Override
  public T getAttrRowForDate(final List<T> attrRows, final AttrGroup group, final Date date)
  {
    Predicate<T> filterPredicate;

    switch (group.getType()) {
      case PERIOD:
        // filter all attrRows without a start time and where the given date is equal or after the rows date
        filterPredicate = row -> (row.getStartTime() == null || date.compareTo(row.getStartTime()) >= 0);
        break;

      case INSTANT_OF_TIME:
        // do not select a row by default
        return null;

      default:
        throw new IllegalArgumentException("The Type " + group.getType() + " is not supported.");
    }

    return attrRows
        .stream()
        .filter(filterPredicate)
        .findFirst()
        .orElse(null);
  }

  @Override
  public List<T> getTimeableAttrRowsForGroup(final EntityWithTimeableAttr<PK, T> entity, final AttrGroup group)
  {
    return getTimeableAttrRowsForGroupName(entity, group.getName());
  }

  @Override
  public List<T> getTimeableAttrRowsForGroupName(final EntityWithTimeableAttr<PK, T> entity, final String groupName)
  {
    if (groupName == null) {
      return Collections.emptyList();
    }

    return entity
        .getTimeableAttributes()
        .stream()
        .filter(row -> groupName.equals(row.getGroupName()))
        .collect(Collectors.toList());
  }

  @Override
  public List<T> sortTimeableAttrRowsByDateDescending(List<T> attrRows)
  {
    return attrRows
        .stream()
        .sorted((row1, row2) -> (row1.getStartTime() == null || row2.getStartTime() == null) ? -1 : row2.getStartTime().compareTo(row1.getStartTime()))
        .collect(Collectors.toList());
  }

}
