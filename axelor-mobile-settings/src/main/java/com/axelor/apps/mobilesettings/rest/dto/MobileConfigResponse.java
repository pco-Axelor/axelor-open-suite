/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2025 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.axelor.apps.mobilesettings.rest.dto;

import java.util.List;

public class MobileConfigResponse {

  protected String sequence;
  protected Boolean isAppEnabled;
  protected Boolean isCustomizeMenuEnabled;
  protected List<MobileMenuResponse> accessibleMenuList;

  public MobileConfigResponse(
      String sequence,
      Boolean isAppEnabled,
      Boolean isCustomizeMenuEnabled,
      List<MobileMenuResponse> accessibleMenuList) {
    this.sequence = sequence;
    this.isAppEnabled = isAppEnabled;
    this.isCustomizeMenuEnabled = isCustomizeMenuEnabled;
    this.accessibleMenuList = accessibleMenuList;
  }

  public String getSequence() {
    return sequence;
  }

  public Boolean getIsAppEnabled() {
    return isAppEnabled;
  }

  public Boolean getIsCustomizeMenuEnabled() {
    return isCustomizeMenuEnabled;
  }

  public List<MobileMenuResponse> getAccessibleMenuList() {
    return accessibleMenuList;
  }
}
