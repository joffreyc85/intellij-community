package com.intellij.uiDesigner.propertyInspector.properties;

import com.intellij.uiDesigner.RadComponent;
import com.intellij.uiDesigner.RadContainer;
import com.intellij.uiDesigner.core.AbstractLayout;

import java.awt.*;

/**
 * This is "synthetic" property of the RadContainer.
 * It represets margins of GridLayoutManager. <b>Note, that
 * this property exists only in RadContainer</b>.
 *
 * @see com.intellij.uiDesigner.core.GridLayoutManager#getMargin
 *
 * @author Anton Katilin
 * @author Vladimir Kondratyev
 */
public final class MarginProperty extends AbstractInsetsProperty{
  private static final Insets DEFAULT_INSETS = new Insets(0, 0, 0, 0);

  public MarginProperty(){
    super("margins");
  }

  public Object getValue(final RadComponent component){
    if(!(component instanceof RadContainer)){
      //noinspection HardCodedStringLiteral
      throw new IllegalArgumentException("component must be an instance of RadContainer: "+component);
    }
    final RadContainer container=(RadContainer)component;

    final AbstractLayout layoutManager=(AbstractLayout)container.getLayout();
    return layoutManager.getMargin();
  }

  protected void setValueImpl(final RadComponent component,final Object value) throws Exception{
    if(!(component instanceof RadContainer)){
      //noinspection HardCodedStringLiteral
      throw new IllegalArgumentException("component must be an instance of RadContainer: "+component);
    }
    final RadContainer container=(RadContainer)component;
    if(value==null){
      //noinspection HardCodedStringLiteral
      throw new IllegalArgumentException("value cannot be null");
    }

    final Insets insets=(Insets)value;
    final AbstractLayout layoutManager=(AbstractLayout)container.getLayout();
    layoutManager.setMargin(insets);
  }

  @Override public boolean isModified(final RadComponent component) {
    return !getValue(component).equals(DEFAULT_INSETS);
  }
}
