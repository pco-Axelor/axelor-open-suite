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
package com.axelor.apps.account.service.moveline;

import com.axelor.apps.account.db.AccountingBatch;
import com.axelor.apps.account.db.Move;
import com.axelor.apps.account.db.MoveLine;
import com.axelor.apps.base.AxelorException;
import com.axelor.apps.base.db.Batch;
import com.axelor.apps.base.db.Partner;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;

public interface MoveLineService {

  public MoveLine balanceCreditDebit(MoveLine moveLine, Move move);

  public void usherProcess(MoveLine moveLine);

  public void reconcileMoveLinesWithCacheManagement(List<MoveLine> moveLineList)
      throws AxelorException;

  public void reconcileMoveLines(List<MoveLine> moveLineList);

  void setIsSelectedBankReconciliation(MoveLine moveLine);

  boolean checkManageCutOffDates(MoveLine moveLine);

  boolean checkManageCutOffDates(MoveLine moveLine, int functionalOriginSelect);

  void applyCutOffDates(
      MoveLine moveLine, Move move, LocalDate cutOffStartDate, LocalDate cutOffEndDate);

  BigDecimal getCutOffProrataAmount(MoveLine moveLine, LocalDate moveDate);

  MoveLine computeCutOffProrataAmount(MoveLine moveLine, LocalDate moveDate);

  Batch validatePreviewBatch(List<Long> recordIdList, Long batchId, int actionSelect)
      throws AxelorException;

  void updatePartner(List<MoveLine> moveLineList, Partner partner, Partner previousPartner);

  List<MoveLine> getReconcilableMoveLines(List<Integer> moveLineIds);

  Map<List<Object>, Pair<List<MoveLine>, List<MoveLine>>> getPopulatedReconcilableMoveLineMap(
      List<MoveLine> moveLineList);

  Pair<List<MoveLine>, List<MoveLine>> findMoveLineLists(
      Pair<List<MoveLine>, List<MoveLine>> moveLineLists);

  void computeCutOffProrataAmount(AccountingBatch accountingBatch) throws AxelorException;
}
