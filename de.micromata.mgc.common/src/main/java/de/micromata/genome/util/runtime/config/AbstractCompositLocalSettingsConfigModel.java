package de.micromata.genome.util.runtime.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import de.micromata.genome.util.bean.FieldMatchers;
import de.micromata.genome.util.bean.PrivateBeanUtils;
import de.micromata.genome.util.matcher.CommonMatchers;
import de.micromata.genome.util.runtime.LocalSettings;
import de.micromata.genome.util.validation.ValContext;

/**
 * Using fields and nested of this instance to cast.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public abstract class AbstractCompositLocalSettingsConfigModel extends AbstractLocalSettingsConfigModel
    implements CastableLocalSettingsConfigModel
{
  @Override
  public void fromLocalSettings(LocalSettings localSettings)
  {
    super.fromLocalSettings(localSettings);
    List<Field> found = PrivateBeanUtils.findAllFields(getClass(),
        CommonMatchers.and(FieldMatchers.hasNotModifier(Modifier.STATIC),
            FieldMatchers.assignableTo(LocalSettingsConfigModel.class)));
    for (Field field : found) {
      LocalSettingsConfigModel nested = (LocalSettingsConfigModel) PrivateBeanUtils.readField(this, field);
      nested.fromLocalSettings(localSettings);
    }
  }

  @Override
  public LocalSettingsWriter toProperties(LocalSettingsWriter writer)
  {
    super.toProperties(writer);
    List<Field> found = PrivateBeanUtils.findAllFields(getClass(),
        CommonMatchers.and(FieldMatchers.hasNotModifier(Modifier.STATIC),
            FieldMatchers.assignableTo(LocalSettingsConfigModel.class)));
    for (Field field : found) {
      LocalSettingsConfigModel nested = (LocalSettingsConfigModel) PrivateBeanUtils.readField(this, field);

      nested.toProperties(writer);
    }
    return writer;
  }

  @Override
  public void validate(ValContext ctx)
  {
    List<Field> found = PrivateBeanUtils.findAllFields(getClass(),
        CommonMatchers.and(FieldMatchers.hasNotModifier(Modifier.STATIC),
            FieldMatchers.assignableTo(LocalSettingsConfigModel.class)));
    for (Field field : found) {
      LocalSettingsConfigModel nested = (LocalSettingsConfigModel) PrivateBeanUtils.readField(this, field);
      ValContext sctx = ctx.createSubContext(nested, "");
      nested.validate(sctx);
    }

  }

  @Override
  public <T extends LocalSettingsConfigModel> T castTo(Class<T> other)
  {
    List<Field> found = PrivateBeanUtils.findAllFields(getClass(),
        CommonMatchers.and(FieldMatchers.hasNotModifier(Modifier.STATIC),
            FieldMatchers.assignableTo(other)));
    if (found.isEmpty() == false) {
      return (T) PrivateBeanUtils.readField(this, found.get(0));
    }
    found = PrivateBeanUtils.findAllFields(getClass(),
        CommonMatchers.and(FieldMatchers.hasNotModifier(Modifier.STATIC),
            FieldMatchers.assignableTo(CastableLocalSettingsConfigModel.class)));
    for (Field f : found) {
      CastableLocalSettingsConfigModel ct = (CastableLocalSettingsConfigModel) PrivateBeanUtils.readField(this, f);
      T ret = ct.castTo(other);
      if (ret != null) {
        return ret;
      }
    }
    return null;
  }
}