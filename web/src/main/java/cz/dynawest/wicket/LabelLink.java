package cz.dynawest.wicket;

import java.util.*;
import java.util.logging.*;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;


/**
 *
 * @author Ondrej Zizka
 */
public abstract class LabelLink extends Link {

   private IModel labelModel;

   public LabelLink(String id, IModel linkModel, IModel labelModel) {
      super(id, linkModel);
      this.labelModel = labelModel;
   }

   @Override
   protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
      replaceComponentTagBody(markupStream, openTag, labelModel.getObject().toString());
   }

}// class LabelLink



