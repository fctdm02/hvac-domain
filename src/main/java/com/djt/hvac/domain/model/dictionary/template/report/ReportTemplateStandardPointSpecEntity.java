//@formatter:off
package com.djt.hvac.domain.model.dictionary.template.report;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.djt.hvac.domain.model.common.validation.SimpleValidationMessage;
import com.djt.hvac.domain.model.common.validation.SimpleValidationMessage.MessageType;
import com.djt.hvac.domain.model.dictionary.TagEntity;
import com.djt.hvac.domain.model.dictionary.container.DictionaryContext;
import com.djt.hvac.domain.model.dictionary.container.NodeTagTemplatesContainer;
import com.djt.hvac.domain.model.dictionary.energyexchange.AbstractEnergyExchangeTypeEntity;
import com.djt.hvac.domain.model.dictionary.template.nodetag.PointTemplateEntity;

public class ReportTemplateStandardPointSpecEntity extends AbstractReportTemplatePointSpecEntity {
  private static final long serialVersionUID = 1L;
  
  private final Set<TagEntity> tags;

  private transient Set<String> _haystackTags;

  public ReportTemplateStandardPointSpecEntity(
      Integer persistentIdentity,
      ReportTemplateEquipmentSpecEntity parentEquipmentSpec,
      String name,
      boolean isRequired,
      boolean isArray,
      String currentObjectExpression,
      String errorMessage,
      Integer requiredDataTypeId,
      Set<TagEntity> tags) {
    super(
        persistentIdentity,
        parentEquipmentSpec,
        name,
        isRequired,
        isArray,
        currentObjectExpression,
        errorMessage,
        requiredDataTypeId);
    requireNonNull(tags, "tags cannot be null");
    this.tags = tags;
  }

  public Set<TagEntity> getTags() {
    return tags;
  }

  public List<String> getTagNames() {

    List<String> tagNames = new ArrayList<>();
    Iterator<TagEntity> iterator = tags.iterator();
    while (iterator.hasNext()) {

      TagEntity tag = iterator.next();
      tagNames.add(tag.getName());
    }
    return tagNames;
  }

  public Set<String> getHaystackTags() {

    if (_haystackTags == null) {
      _haystackTags = new TreeSet<>();
      Iterator<TagEntity> iterator = tags.iterator();
      while (iterator.hasNext()) {
        _haystackTags.add(iterator.next().getName());
      }
    }
    return _haystackTags;
  }

  @Override
  public void validateSimple(List<SimpleValidationMessage> simpleValidationMessages) {

    AbstractEnergyExchangeTypeEntity energyExchangeType = getParentEquipmentSpec().getEnergyExchangeType();
    if (energyExchangeType != null) {

      NodeTagTemplatesContainer nodeTagTemplatesContainer = DictionaryContext.getNodeTagTemplatesContainer();
      Set<PointTemplateEntity> pointTemplates = nodeTagTemplatesContainer.getEnergyExchangeLevelPointTemplatesForEnergyExchangeType(energyExchangeType);

      if (pointTemplates != null
          && !pointTemplates.isEmpty()
          && getCurrentObjectExpression() != null
          && getCurrentObjectExpression().trim().isEmpty()) {

        boolean foundPointTemplate = false;
        Iterator<PointTemplateEntity> iterator = pointTemplates.iterator();
        while (iterator.hasNext()) {

          PointTemplateEntity pointTemplate = iterator.next();

          if (pointTemplate.getName().equals(getName())) {

            Set<String> thisHaystackTags = getHaystackTags();
            Set<String> pointTemplateHaystackTags = pointTemplate.getNormalizedTagsAsSet();
            if (!pointTemplateHaystackTags.equals(thisHaystackTags)) {

              simpleValidationMessages.add(new SimpleValidationMessage(
                  MessageType.ERROR,
                  this.getNaturalIdentity(),
                  "tags",
                  "Do not match tags for point template of parent report spec energy exchange type: ["
                      + energyExchangeType
                      + "] whose hay stack tags are: "
                      + pointTemplateHaystackTags
                  ));
            }
            foundPointTemplate = true;
            break;
          }
        }
        if (!foundPointTemplate) {

          simpleValidationMessages.add(new SimpleValidationMessage(
              MessageType.ERROR,
              this.getNaturalIdentity(),
              "name",
              "No point template found with name: ["
                  + getName()
                  + "]  for parent report spec energy exchange type: ["
                  + energyExchangeType
                  + "]"
              ));
        }
      }
    }
  }
}
//@formatter:on