package com.djt.hvac.domain.model.nodehierarchy.visitor;

import java.util.List;
import java.util.Set;

import com.djt.hvac.domain.model.dictionary.enums.FunctionType;
import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateEntity;
import com.djt.hvac.domain.model.function.dto.AdFunctionInstanceDto;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.report.ReportInstanceEntity;

public final class PortfolioVisitor {

  private PortfolioVisitor() {}

  public static List<ReportInstanceEntity> evaluateReports(
      PortfolioEntity portfolio) {
    
    return new EvaluateReportsVisitor().evaluateReports(portfolio, null);
  }
  
  public static List<ReportInstanceEntity> evaluateReports(
      PortfolioEntity portfolio,
      ReportTemplateEntity reportTemplate) {
    
    return new EvaluateReportsVisitor().evaluateReports(portfolio, reportTemplate);
  }

  public static List<AdFunctionInstanceDto> findAdFunctionInstanceCandidates(
      PortfolioEntity portfolio) {
    
    return findAdFunctionInstanceCandidates(portfolio, null);
  }
  
  public static List<AdFunctionInstanceDto> findAdFunctionInstanceCandidates(
      PortfolioEntity portfolio, 
      FunctionType functionType) {
    
    return new FindAdFunctionInstanceCandidatesVisitor().findAdFunctionInstanceCandidates(portfolio, functionType, null);
  }
  
  public static List<AdFunctionInstanceDto> findAdFunctionInstanceCandidates(
      PortfolioEntity portfolio, 
      FunctionType functionType,
      Set<Integer> buildingIdsToProcess) {
    
    return new FindAdFunctionInstanceCandidatesVisitor().findAdFunctionInstanceCandidates(portfolio, functionType, buildingIdsToProcess);
  }
}
