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
package com.axelor.apps.contract.web;

import com.axelor.apps.account.service.analytic.AnalyticAttrsService;
import com.axelor.apps.account.service.analytic.AnalyticGroupService;
import com.axelor.apps.base.ResponseMessageType;
import com.axelor.apps.base.service.exception.TraceBackService;
import com.axelor.apps.contract.db.Contract;
import com.axelor.apps.contract.db.ContractLine;
import com.axelor.apps.contract.db.ContractVersion;
import com.axelor.apps.contract.db.repo.ContractLineRepository;
import com.axelor.apps.contract.db.repo.ContractRepository;
import com.axelor.apps.contract.model.AnalyticLineContractModel;
import com.axelor.apps.contract.service.ContractLineContextToolService;
import com.axelor.apps.contract.service.ContractLineService;
import com.axelor.apps.contract.service.ContractLineViewService;
import com.axelor.apps.contract.service.ContractYearEndBonusService;
import com.axelor.apps.contract.service.attributes.ContractLineAttrsService;
import com.axelor.apps.contract.service.record.ContractLineRecordSetService;
import com.axelor.apps.supplychain.service.AnalyticLineModelService;
import com.axelor.apps.supplychain.service.analytic.AnalyticAttrsSupplychainService;
import com.axelor.db.mapper.Mapper;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.utils.helpers.ContextHelper;
import com.google.inject.Singleton;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public class ContractLineController {

  protected Contract getContractFromContext(ActionRequest request) {
    Contract contract = ContextHelper.getContextParent(request.getContext(), Contract.class, 2);

    ContractVersion contractVersion =
        ContextHelper.getContextParent(request.getContext(), ContractVersion.class, 1);
    if (contractVersion == null) {
      contract = ContextHelper.getContextParent(request.getContext(), Contract.class, 1);
    } else if (contract == null) {
      contract =
          Optional.of(contractVersion)
              .map(ContractVersion::getContract)
              .orElse(contractVersion.getNextContract());
    }

    return contract;
  }

  protected ContractVersion getContractVersionFromContext(ActionRequest request) {
    return ContextHelper.getContextParent(request.getContext(), ContractVersion.class, 1);
  }

  public void computeTotal(ActionRequest request, ActionResponse response) {
    ContractLine contractLine = request.getContext().asType(ContractLine.class);
    ContractLineService contractLineService = Beans.get(ContractLineService.class);

    try {
      Contract contract = this.getContractFromContext(request);
      contractLine = contractLineService.computeTotal(contractLine, contract);
      contractLineService.computeAnalytic(contract, contractLine);
      response.setValues(contractLine);
    } catch (Exception e) {
      response.setValues(contractLineService.reset(contractLine));
    }
  }

  public void createAnalyticDistributionWithTemplate(
      ActionRequest request, ActionResponse response) {
    try {
      ContractLine contractLine = request.getContext().asType(ContractLine.class);
      ContractVersion contractVersion = this.getContractVersionFromContext(request);
      Contract contract = this.getContractFromContext(request);

      AnalyticLineContractModel analyticLineContractModel =
          new AnalyticLineContractModel(contractLine, contractVersion, contract);
      Beans.get(ContractLineRecordSetService.class)
          .setCompanyExTaxTotal(analyticLineContractModel, contractLine);

      Beans.get(AnalyticLineModelService.class)
          .createAnalyticDistributionWithTemplate(analyticLineContractModel);

      response.setValue(
          "analyticMoveLineList", analyticLineContractModel.getAnalyticMoveLineList());
    } catch (Exception e) {
      TraceBackService.trace(response, e, ResponseMessageType.ERROR);
    }
  }

  public void setAxisDomains(ActionRequest request, ActionResponse response) {
    try {
      ContractLine contractLine = request.getContext().asType(ContractLine.class);
      ContractVersion contractVersion = this.getContractVersionFromContext(request);
      Contract contract = this.getContractFromContext(request);

      AnalyticLineContractModel analyticLineContractModel =
          new AnalyticLineContractModel(contractLine, contractVersion, contract);
      response.setAttrs(
          Beans.get(AnalyticGroupService.class)
              .getAnalyticAxisDomainAttrsMap(
                  analyticLineContractModel, analyticLineContractModel.getCompany()));
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  public void createAnalyticAccountLines(ActionRequest request, ActionResponse response) {
    try {
      ContractLine contractLine = request.getContext().asType(ContractLine.class);
      ContractVersion contractVersion = this.getContractVersionFromContext(request);
      Contract contract = this.getContractFromContext(request);

      AnalyticLineContractModel analyticLineContractModel =
          new AnalyticLineContractModel(contractLine, contractVersion, contract);

      if (Beans.get(AnalyticLineModelService.class)
          .analyzeAnalyticLineModel(
              analyticLineContractModel, analyticLineContractModel.getCompany())) {
        response.setValue(
            "analyticMoveLineList", analyticLineContractModel.getAnalyticMoveLineList());
      }
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  public void manageAxis(ActionRequest request, ActionResponse response) {
    try {
      Map<String, Map<String, Object>> attrsMap = new HashMap<>();
      Contract contract = this.getContractFromContext(request);

      if (contract == null
          || Beans.get(ContractYearEndBonusService.class).isYebContract(contract)) {
        return;
      }

      Beans.get(AnalyticAttrsService.class)
          .addAnalyticAxisAttrs(contract.getCompany(), null, attrsMap);
      response.setAttrs(attrsMap);
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  public void printAnalyticAccounts(ActionRequest request, ActionResponse response) {
    try {
      ContractLine contractLine = request.getContext().asType(ContractLine.class);
      ContractVersion contractVersion = this.getContractVersionFromContext(request);
      Contract contract = this.getContractFromContext(request);

      AnalyticLineContractModel analyticLineContractModel =
          new AnalyticLineContractModel(contractLine, contractVersion, contract);

      if (Beans.get(ContractYearEndBonusService.class).isYebContract(contract)) {
        return;
      }

      response.setValues(
          Beans.get(AnalyticGroupService.class)
              .getAnalyticAccountValueMap(
                  analyticLineContractModel, analyticLineContractModel.getCompany()));
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  public void setAnalyticDistributionPanelHidden(ActionRequest request, ActionResponse response) {
    try {
      ContractLine contractLine = request.getContext().asType(ContractLine.class);
      ContractVersion contractVersion = this.getContractVersionFromContext(request);
      Contract contract = this.getContractFromContext(request);

      AnalyticLineContractModel analyticLineContractModel =
          new AnalyticLineContractModel(contractLine, contractVersion, contract);
      Map<String, Map<String, Object>> attrsMap = new HashMap<>();

      if (Beans.get(ContractYearEndBonusService.class).isYebContract(contract)) {
        return;
      }

      Beans.get(AnalyticAttrsSupplychainService.class)
          .addAnalyticDistributionPanelHiddenAttrs(analyticLineContractModel, attrsMap);
      response.setAttrs(attrsMap);
    } catch (Exception e) {
      TraceBackService.trace(response, e, ResponseMessageType.ERROR);
    }
  }

  public void fillDefault(ActionRequest request, ActionResponse response) {
    ContractLineService contractLineService = Beans.get(ContractLineService.class);
    ContractLine contractLine = new ContractLine();

    try {
      contractLine = request.getContext().asType(ContractLine.class);

      ContractVersion contractVersion =
          request.getContext().getParent().asType(ContractVersion.class);
      if (contractVersion != null) {
        contractLine = contractLineService.fillDefault(contractLine, contractVersion);
      }
      response.setValues(contractLine);
    } catch (Exception e) {
      response.setValues(contractLineService.reset(contractLine));
    }
  }

  public void checkFromDate(ActionRequest request, ActionResponse response) {
    ContractLine contractLine = request.getContext().asType(ContractLine.class);
    ContractVersion contractVersion =
        request.getContext().getParent().asType(ContractVersion.class);
    if (contractVersion != null
        && contractVersion.getSupposedActivationDate() != null
        && contractLine.getFromDate() != null
        && contractVersion.getSupposedActivationDate().isAfter(contractLine.getFromDate())) {
      response.setValue("fromDate", contractVersion.getSupposedActivationDate());
    }
  }

  public void hidePanels(ActionRequest request, ActionResponse response) {
    ContractVersion contract = request.getContext().getParent().asType(ContractVersion.class);
    response.setAttr("datePanel", "hidden", !contract.getIsPeriodicInvoicing());
    response.setAttr("pricesPerYearPanel", "hidden", !contract.getIsPeriodicInvoicing());
  }

  public void hideIsToRevaluate(ActionRequest request, ActionResponse response) {
    ContractVersion contractVersion =
        request.getContext().getParent().asType(ContractVersion.class);
    Object contractId = request.getContext().getParent().get("_xContractId");
    Contract contract = null;
    if (contractId != null) {
      contract = Beans.get(ContractRepository.class).find(((Integer) contractId).longValue());
    }
    response.setAttr(
        "isToRevaluate",
        "hidden",
        Beans.get(ContractLineViewService.class).hideIsToRevaluate(contract, contractVersion));
  }

  public void setScaleAndPrecision(ActionRequest request, ActionResponse response) {
    ContractLine contractLine = request.getContext().asType(ContractLine.class);

    if (contractLine != null) {
      Contract contract = this.getContractFromContext(request);

      response.setAttrs(
          Beans.get(ContractLineAttrsService.class).setScaleAndPrecision(contract, ""));
    }
  }

  public void setProductRequired(ActionRequest request, ActionResponse response) {
    Contract contract =
        Beans.get(ContractLineContextToolService.class).getContract(request.getContext());
    response.setAttr(
        "product",
        "required",
        contract.getContractTypeSelect() == ContractRepository.TYPE_FRAMEWORK);
  }

  public void computeProductDomain(ActionRequest request, ActionResponse response) {
    Contract contract =
        Beans.get(ContractLineContextToolService.class).getContract(request.getContext());
    response.setAttr(
        "product", "domain", Beans.get(ContractLineService.class).computeProductDomain(contract));
  }

  public void emptyLine(ActionRequest request, ActionResponse response) {
    ContractLine contractLine = request.getContext().asType(ContractLine.class);
    if (contractLine.getTypeSelect() != ContractLineRepository.TYPE_NORMAL) {
      Map<String, Object> newContractLine = Mapper.toMap(new ContractLine());
      newContractLine.put("qty", BigDecimal.ZERO);
      newContractLine.put("id", contractLine.getId());
      newContractLine.put("version", contractLine.getVersion());
      newContractLine.put("typeSelect", contractLine.getTypeSelect());
      response.setValues(newContractLine);
    }
  }
}
