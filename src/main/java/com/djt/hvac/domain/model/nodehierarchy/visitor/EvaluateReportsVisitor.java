package com.djt.hvac.domain.model.nodehierarchy.visitor;

import java.util.ArrayList;
import java.util.List;

import com.djt.hvac.domain.model.dictionary.template.report.ReportTemplateEntity;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.building.BuildingEntity;
import com.djt.hvac.domain.model.report.ReportEvaluator;
import com.djt.hvac.domain.model.report.ReportInstanceEntity;

public class EvaluateReportsVisitor {
  
  //private static final Logger LOGGER = LoggerFactory.getLogger(EvaluateReportsVisitor.class);

  // Used for debugging.
  //public static Integer DEBUG_BUILDING_ID;
  //public static Integer DEBUG_REPORT_TEMPLATE_ID;
  
  List<ReportInstanceEntity> evaluateReports(
      PortfolioEntity portfolio,
      ReportTemplateEntity reportTemplate) {
    
    if (!portfolio.getParentCustomer().loadReportInstances) {
      
      throw new IllegalStateException("Cannot evaluate reports on a portfolio that hasn't had report instances loaded");
    }
    
    List<ReportInstanceEntity> changedReportInstances = new ArrayList<>();
    
    for (BuildingEntity building: portfolio.getChildBuildings()) {
      
      //if (DEBUG_BUILDING_ID != null && building.getPersistentIdentity().equals(DEBUG_BUILDING_ID)) {
      //  if (LOGGER.isDebugEnabled()) {
      //    LOGGER.debug("DEBUG_BUILDING_ID");
      //  }
      //}
      
      for (ReportInstanceEntity reportInstance: building.getReportInstances())  {
        
        //if (DEBUG_REPORT_TEMPLATE_ID != null && reportInstance.getReportTemplate().getPersistentIdentity().equals(DEBUG_REPORT_TEMPLATE_ID)) {
        //  if (LOGGER.isDebugEnabled()) {
        //    LOGGER.debug("DEBUG_REPORT_TEMPLATE_ID");
        //  }
        //}
        
        if ((reportTemplate == null || reportTemplate.equals(reportInstance.getReportTemplate())) && !reportInstance.isIgnored()) {

          ReportEvaluator.evaluate(
              building.getAllDescendantEnergyExchangeSystemNodesByType(), 
              reportInstance,
              null);
         
          if (reportInstance.getIsModified()) {
            
            changedReportInstances.add(reportInstance);
          }
          
          building.resetAllDescendantEquipment();
        }
      }      
    }
    return changedReportInstances;
  }
}
