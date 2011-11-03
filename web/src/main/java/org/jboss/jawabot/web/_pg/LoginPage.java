
package org.jboss.jawabot.web._pg;


import org.jboss.jawabot.web._base.BaseLayoutPage;
import java.io.Serializable;
import java.util.logging.*;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;


/**
 *
 * @author Ondrej Zizka
 */
public class LoginPage extends BaseLayoutPage
{
  private static final Logger log = Logger.getLogger( HomePage.class.getName() );


  private UserLoginInfo userLoginInfo = new UserLoginInfo();

  private Form              form;
  private TextField         tfUser;
  private PasswordTextField tfPass;


  /**
   * Const
   */
  public LoginPage( PageParameters parameters ) {
     super(parameters);
     //setVersioned(false);

     form = (Form) new Form("loginForm"){

         @Override protected void onSubmit() {
            LoginPage.this.getSession().setLoggedUser(tfUser.getValue());
         }

         @Override protected void onValidate() {
            //log.info( "LoginPage: onValidate" );
            //log.info("Pass1: "+tfPass.getValue());
         }
     };

     add( form );
     form.add( new FeedbackPanel("feedback") );
     form.add( tfUser = (TextField)         new TextField("user", new PropertyModel(this.userLoginInfo, "user")).setRequired(true));
     form.add( tfPass = (PasswordTextField) new PasswordTextField("pass", new PropertyModel(this.userLoginInfo, "pass")).setRequired(true));
     form.add( new AbstractFormValidator() {
         @Override public FormComponent<?>[] getDependentFormComponents() {
            return new FormComponent[]{tfUser, tfPass};
         }
         @Override public void validate(Form<?> form) {
            //log.info("Pass2: "+tfPass.getValue());
            if( ! "foo".equals( tfPass.getValue() ) )
               this.error(tfPass, "wrongPassword");
            // TODO: Real password validation.
         }
     });
     
  }

}// class HomePage




class UserLoginInfo implements Serializable {
   private String user;
   private String pass;

   public String getPass() {      return pass;   }
   public void setPass(String pass) {      this.pass = pass;   }
   public String getUser() {      return user;   }
   public void setUser(String user) {      this.user = user;   }
}

