package com.djt.hvac.domain.model.dictionary.template.currentobjectexpression;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.exception.EntityDoesNotExistException;
import com.djt.hvac.domain.model.dictionary.template.currentobjectexpression.crawler.CurrentObjectExpressionPortfolioCrawler;
import com.djt.hvac.domain.model.function.AbstractAdFunctionInstanceEntity;
import com.djt.hvac.domain.model.function.AdFunctionInstanceEligiblePoint;
import com.djt.hvac.domain.model.nodehierarchy.PortfolioEntity;
import com.djt.hvac.domain.model.nodehierarchy.energyexchange.EnergyExchangeEntity;
import com.djt.hvac.domain.model.nodehierarchy.point.AbstractPointEntity;

/**
 * 
 * There are two scenarios:
 * <ol>
 *   <li>A current object expression is non-null/non-empty: Use the "portfolio crawler" to evaluate for points</li>
 *   <li>A current object expression is null/empty: Use the direct child points of the "this" node</li>
 * </ol> 
 * 
 * @author tmyers
 *
 */
public class CurrentObjectExpression {
  
  private CurrentObjectExpression() {
  }
  
  private static final Map<String, CurrentObjectExpressionPortfolioCrawler> PORTFOLIO_CRAWLERS = new HashMap<>();
  
  private static CurrentObjectExpressionPortfolioCrawler getCurrentObjectExpressionPortfolioCrawler(String currentObjectExpression) {
    
    CurrentObjectExpressionPortfolioCrawler crawler = PORTFOLIO_CRAWLERS.get(currentObjectExpression);
    if (crawler == null) {
      
      crawler = CurrentObjectExpressionPortfolioCrawler.create(currentObjectExpression);
      PORTFOLIO_CRAWLERS.put(currentObjectExpression, crawler);
    }
    return crawler;
  }

  /**
   * 
   * @param energyExchangeSystemNode The parent energy exchange system node to evaluate 
   *         the <code>currentObjectExpression</code> against
   *         
   * @param currentObjectExpression An expression conforming to the DSL specified below
   * 
   * @return The set of points from the evaluation of the given <code>energyExchangeSystemNode</code> and
   *         <code>currentObjectExpression</code> that implement the <code>RuleEligiblePoint</code>
   *         marker interface, as not all point types are supported for rules.
   * 
   *         see https://github.com/MadDogTechnology/current-object-expression-parser
   */
  public static Set<AdFunctionInstanceEligiblePoint> getAdFunctionInstanceEligiblePoints(
      EnergyExchangeEntity energyExchangeSystemNode,
      String currentObjectExpression) {

    // If no curr obj expr is given, then we default to the points for the given node.
    if (currentObjectExpression == null || currentObjectExpression.isEmpty()) {
      return energyExchangeSystemNode.getAdFunctionInstanceEligiblePoints();
    }
    
    // Otherwise, use the generic portfolio crawler (that uses the curr obj expr DSL).
    PortfolioEntity portfolio = energyExchangeSystemNode.getRootPortfolioNode(); 
    CurrentObjectExpressionPortfolioCrawler crawler = getCurrentObjectExpressionPortfolioCrawler(
        currentObjectExpression);
    
    Set<AdFunctionInstanceEligiblePoint> points = new TreeSet<>();
    for (Integer nodeId: crawler.getCurrentObjects(
        portfolio, 
        energyExchangeSystemNode.getPersistentIdentity())) {
      
      try {
        points.addAll(portfolio.getChildNode(nodeId).getAdFunctionInstanceEligiblePoints());
      } catch (EntityDoesNotExistException ednee) {
        throw new IllegalStateException("Could not evaluate current object expression: ["
            + currentObjectExpression
            + "] for energy exchange system node: ["
            + energyExchangeSystemNode
            + "].  Could not find node with id: ["
            + nodeId
            + "]", ednee);
      }
    }
    return points;
  }

  /**
   * 
   * @param energyExchangeSystemNode The parent energy exchange system node to evaluate 
   *         the <code>currentObjectExpression</code> against
   *         
   * @param currentObjectExpression An expression conforming to the DSL specified below
   * 
   * @return The set of points from the evaluation of the given <code>energyExchangeSystemNode</code> and
   *         <code>currentObjectExpression</code>.
   * 
   *         see https://github.com/MadDogTechnology/current-object-expression-parser
   */
  public static Set<AbstractPointEntity> getPoints(
      EnergyExchangeEntity energyExchangeSystemNode,
      String currentObjectExpression) {
    
    // If no curr obj expr is given, then we default to the points for the given node.
    if (currentObjectExpression == null || currentObjectExpression.isEmpty()) {
      return energyExchangeSystemNode.getChildPoints();
    }
    
    // Otherwise, use the generic portfolio crawler (that uses the curr obj expr DSL).
    PortfolioEntity portfolio = energyExchangeSystemNode.getRootPortfolioNode(); 
    CurrentObjectExpressionPortfolioCrawler crawler = getCurrentObjectExpressionPortfolioCrawler(
        currentObjectExpression);
    
    Set<AbstractPointEntity> points = new TreeSet<>();
    for (Integer nodeId: crawler.getCurrentObjects(
        portfolio,
        energyExchangeSystemNode.getPersistentIdentity())) {
      
      try {
        points.addAll(portfolio.getChildNode(nodeId).getChildPoints());
      } catch (EntityDoesNotExistException ednee) {
        throw new IllegalStateException("Could not evaluate current object expression: ["
            + currentObjectExpression
            + "] for energy exchange system node: ["
            + energyExchangeSystemNode
            + "]. Could not find node with id: ["
            + nodeId
            + "]", ednee);
      }
    }
    return points;
  }
  
  /**
   * 
   * @param energyExchangeSystemNode The parent energy exchange system node to evaluate 
   *         the <code>currentObjectExpression</code> against
   *         
   * @param currentObjectExpression An expression conforming to the DSL specified below
   * 
   * @return The set of AD function instances from the evaluation of the given 
   *         <code>currentObjectExpression</code> against the given 
   *         <code>energyExchangeSystemNode</code>.
   * 
   *         see https://github.com/MadDogTechnology/current-object-expression-parser
   */
  public static Set<AbstractAdFunctionInstanceEntity> getAdFunctionInstances(
      EnergyExchangeEntity energyExchangeSystemNode,
      String currentObjectExpression) {
    
    // If no curr obj expr is given, then we default to the points for the given node.
    if (currentObjectExpression == null || currentObjectExpression.isEmpty()) {
      return energyExchangeSystemNode.getAdFunctionInstances();
    }
    
    // Otherwise, use the generic portfolio crawler (that uses the curr obj expr DSL).
    PortfolioEntity portfolio = energyExchangeSystemNode.getRootPortfolioNode(); 
    CurrentObjectExpressionPortfolioCrawler crawler = getCurrentObjectExpressionPortfolioCrawler(
        currentObjectExpression);
    
    Set<AbstractAdFunctionInstanceEntity> adFunctionInstances = new TreeSet<>();
    for (Integer nodeId: crawler.getCurrentObjects(
        portfolio,
        energyExchangeSystemNode.getPersistentIdentity())) {
      
      try {
        adFunctionInstances.addAll(portfolio.getEnergyExchangeSystemNode(nodeId).getAdFunctionInstances());
      } catch (EntityDoesNotExistException ednee) {
        throw new IllegalStateException("Could not evaluate current object expression: ["
            + currentObjectExpression
            + "] for energy exchange system node: ["
            + energyExchangeSystemNode
            + "]. Could not find node with id: ["
            + nodeId
            + "]", ednee);
      }
    }
    return adFunctionInstances;
  }  
}
