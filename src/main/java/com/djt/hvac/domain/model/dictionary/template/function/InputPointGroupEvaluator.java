//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.function;

import java.util.LinkedHashSet;
import java.util.Set;

import com.djt.hvac.domain.model.common.dsl.tagquery.TagQueryExpression;
import com.google.common.collect.Sets;

/**
 * 
 * @author tommyers
 *
 */
public final class InputPointGroupEvaluator {
  
  private InputPointGroupEvaluator() {}
  
  /**
   * Given a set of point names, enumerate all permutations and return the combinations
   * that are valid for the given point tuple constraint expression.
   * 
   * @param tupleConstraintExpression The tuple constraint to evaluate
   * @param requiredPointNames The set of required point names
   * 
   * @return The set of valid combinations of point group names for the given expression 
   */
  public static Set<Set<String>> generateValidInputPointGroupCombinations(
      String tupleConstraintExpression,
      Set<String> requiredPointNames) {
    
    try {

      Set<Set<String>> validInputPointGroupCombinations = new LinkedHashSet<>();
      if (tupleConstraintExpression != null && !tupleConstraintExpression.trim().isEmpty()) {

        TagQueryExpression tagQueryExpression = TagQueryExpression.parse(tupleConstraintExpression);
        
        Set<String> pointNames = tagQueryExpression.getTags();
        for (int i=0; i <= pointNames.size(); i++) {
          
          Set<Set<String>> combinations = Sets.combinations(pointNames, i);
          for (Set<String> combination: combinations) {
          
            Set<String> set = new LinkedHashSet<>();
            set.addAll(combination);
            if (tagQueryExpression.match(set)) {
              
              set.addAll(requiredPointNames);
              validInputPointGroupCombinations.add(set);  
            }
          }
        }
      }
      return validInputPointGroupCombinations;

    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid tupleConstraintExpression: ["
          + tupleConstraintExpression
          + "], error: ["
          + e.getMessage()
          + "]", e);
    }    
  }
  /*
  public static void main(String[] args) {
    
    try {

      Set<String> requiredPointNames = new HashSet<>();
      //requiredPointNames.add("ZoneTemp");
      //requiredPointNames.add("ZoneTempSp");

      // TODO: TDM: This solution gives us all possible combinations, but some we do not want/need...
      // [EffCoolSp, EffHeatSp] YES
      // [OccCoolSp, OccHeatSp] YES
      // [EffCoolSp, EffHeatSp, OccCoolSp] NO
      // [EffCoolSp, EffHeatSp, OccHeatSp] NO
      // [EffCoolSp, OccCoolSp, OccHeatSp] NO
      // [EffHeatSp, OccCoolSp, OccHeatSp] NO
      // [EffCoolSp, EffHeatSp, OccCoolSp, OccHeatSp] NO
      String tupleConstraintExpression = "(EffCoolSp && EffHeatSp) || (OccCoolSp && OccHeatSp)";
      
      Set<Set<String>> validInputPointGroupCombinations = InputPointGroupEvaluator.generateValidInputPointGroupCombinations(
          tupleConstraintExpression,
          requiredPointNames);
      
      System.err.println("tupleConstraintExpression: " + tupleConstraintExpression);
      System.err.println("number of valid combinations: " + validInputPointGroupCombinations.size());
      for (Set<String> ipgc: validInputPointGroupCombinations) {
        
        System.err.println(ipgc);
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  */
}
//@formatter:on