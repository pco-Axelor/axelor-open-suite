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
package com.axelor.apps.businessproduction.db.repo;

import com.axelor.apps.base.service.administration.SequenceService;
import com.axelor.apps.production.db.ManufOrder;
import com.axelor.apps.production.db.repo.ManufOrderManagementRepository;
import com.axelor.apps.production.service.manuforder.ManufOrderCreateBarcodeService;
import com.axelor.apps.production.service.operationorder.OperationOrderCreateBarcodeService;
import com.google.inject.Inject;

public class ManufOrderBusinessProductionManagementRepository
    extends ManufOrderManagementRepository {

  @Inject
  public ManufOrderBusinessProductionManagementRepository(
      SequenceService sequenceService,
      OperationOrderCreateBarcodeService operationOrderCreateBarcodeService,
      ManufOrderCreateBarcodeService manufOrderCreateBarcodeService) {
    super(sequenceService, operationOrderCreateBarcodeService, manufOrderCreateBarcodeService);
  }

  @Override
  public ManufOrder copy(ManufOrder entity, boolean deep) {
    entity = super.copy(entity, deep);
    entity.setTimesheetLine(null);
    return entity;
  }
}
