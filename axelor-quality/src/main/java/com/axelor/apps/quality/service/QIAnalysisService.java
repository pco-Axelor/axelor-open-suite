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
package com.axelor.apps.quality.service;

import com.axelor.apps.base.AxelorException;
import com.axelor.apps.base.db.Partner;
import com.axelor.apps.quality.db.QIActionDistribution;
import com.axelor.apps.quality.db.QIAnalysis;
import com.axelor.message.db.Template;
import java.io.IOException;
import java.util.List;
import javax.mail.MessagingException;

public interface QIAnalysisService {

  int setAdvancement(QIAnalysis qiAnalysis);

  List<QIActionDistribution> generateQIActionDistribution(QIAnalysis qiAnalysis)
      throws AxelorException, IOException;

  List<QIActionDistribution> generateQIActionDistributionForOthers(
      QIAnalysis qiAnalysis, Integer recepient, Partner recepientPartner)
      throws AxelorException, IOException;

  void sendQIActionDistributions(
      List<QIActionDistribution> qiActionDistributionList,
      Template qiActionDistributionMessageTemplate)
      throws ClassNotFoundException,
          InstantiationException,
          IllegalAccessException,
          AxelorException,
          IOException,
          MessagingException;
}
