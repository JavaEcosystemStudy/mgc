package de.micromata.mgc.javafx.launcher.gui;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import org.apache.log4j.Logger;

import de.micromata.genome.util.bean.FieldMatchers;
import de.micromata.genome.util.bean.PrivateBeanUtils;
import de.micromata.genome.util.matcher.CommonMatchers;
import de.micromata.genome.util.runtime.GenericsUtils;
import de.micromata.genome.util.runtime.config.LocalSettingsConfigModel;
import de.micromata.genome.util.validation.ValMessage;
import de.micromata.mgc.javafx.FXEvents;
import de.micromata.mgc.javafx.ModelController;
import de.micromata.mgc.javafx.feedback.FeedbackPanel;
import javafx.scene.Node;
import javafx.scene.control.Control;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public abstract class AbstractModelController<M>extends AbstractController implements ModelController<M>
{
  private static final Logger LOG = Logger.getLogger(AbstractModelController.class);

  protected M model;

  protected void registerValMessageReceiver(Node node, FeedbackPanel feedback, Class<?> referenceType, String property)
  {
    FXEvents.get().registerValMessageReceiver(this, node, referenceType, property);
  }

  public void addToFeedback(ValMessage msg)
  {
    LOG.warn("No feedbackPanel defined in " + getClass().getSimpleName() + "; " + msg.getI18nkey());
  }

  protected void registerValMessage(String fieldName)
  {
    Class<?> type = GenericsUtils.getClassGenericTypeFromSuperClass(getClass(), 0, LocalSettingsConfigModel.class);
    Node node = (Node) PrivateBeanUtils.readField(this, fieldName);
    FXEvents.get().registerValMessageReceiver(this, node, getModel(), fieldName);
  }

  @Override
  public void registerValMessageReceivers()
  {
    Class<?> type = GenericsUtils.getClassGenericTypeFromSuperClass(getClass(), 0, LocalSettingsConfigModel.class);
    List<Field> fields = PrivateBeanUtils.findAllFields(getClass(),
        CommonMatchers.and(FieldMatchers.hasNotModifier(Modifier.STATIC), FieldMatchers.assignableTo(Control.class)));
    for (Field field : fields) {
      Control ctl = (Control) PrivateBeanUtils.readField(this, field);
      FXEvents.get().registerValMessageReceiver(this, ctl, getModel(), field.getName());
    }

  }

  @Override
  public void initializeWithModel()
  {

  }

  @Override
  public void fromModel()
  {
  }

  @Override
  public void toModel()
  {

  }

  @Override
  public M getModel()
  {
    return model;
  }

  @Override
  public void setModel(M model)
  {
    this.model = model;
  }

}