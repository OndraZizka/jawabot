


function Logout(){

  try{
    var agt = navigator.userAgent.toLowerCase();
    if (agt.indexOf("msie") != -1) {
      // IE clear HTTP Authentication
      document.execCommand("ClearAuthenticationCache");
    }
    else {
      // Let's create an xmlhttp object
      var xmlhttp = createXMLObject();

      // Let's get the force page to logout for mozilla 
      xmlhttp.open("GET",".force_logout_offer_login_mozilla",true,"logged_out","");
      xmlhttp.send("");
      xmlhttp.abort();
    }
    // Let's redirect the user to the main webpage.
    window.location = "./";
  }
  catch( e ) {
    // There was an error
    alert("there was an error");
  }

}// Logout() 
  


function createXMLObject() {
  var xmlhttp = null;

  try {
    if( window.XMLHttpRequest )       xmlhttp = new XMLHttpRequest();
    else if( window.ActiveXObject )   xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
  }catch( e ) { }

  return xmlhttp;
}

