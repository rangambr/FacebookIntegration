import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.*;
import com.facebook.api.*;
import java.io.PrintWriter;

public class FacebookIntegrationServ extends HttpServlet {

    String appapikey = new String("762-254419314641601-a7fb73627afdda0");
    String appsecret = new String("90e-4df45ba70be7040e6b2d95ce4e340834-7b47df6d957e");

    //Facebook loginPage to this application.  Parameter canvas=true shows the result in Facebook canvas
    String loginPage = "http://www.facebook.com/login.php?api_key="+"00b0049fc7cf0d1e4a4ba2ea3e55b269"+"&v=1.0&canvas=true";
    FacebookRestClient facebook;  // the facebook client, talks to REST Server
    PrintWriter servletOutput;  // output of servlet. HTML or FBML out
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.getWriter().print("Hello from Java!\n");
    }


    public void doPost(HttpServletRequest req,
			HttpServletResponse res)
	throws ServletException, IOException {

		servletOutput = res.getWriter(); // response is sent to ServletOutput
		res.setContentType( "text/html" );
		servletOutput.println("This is the doPost method");
		servletOutput.println("<br>");

		String user =null;
		String sessionKey=null;
		sessionKey = req.getParameter(FacebookParam.SESSION_KEY.toString());  // Session Key passed as request parameter
		if (sessionKey==null) { // If there is not session key, they user not logged in
			servletOutput.println("<fb:redirect url=" + loginPage + "/>");  // Facebook Redirect to login page
		}
		else{
			user = req.getParameter("fb_sig_user");  // get user as a string.  User info passed as request parameter
			servletOutput.println("User is " + user);  // displays numeric value
			servletOutput.println("<br>");
			facebook = new FacebookRestClient(appapikey, appsecret,sessionKey);  // create Facebook Json Rest Client
			servletOutput.println("Facebook Client created");
			servletOutput.println("<br>");

			// with Facebook client created, now Facebook API calls can be made
			// In this case, a call to FQL to get username

			try{
				String query = "SELECT name FROM user WHERE uid=" + user;
				org.json.JSONArray resultArray = (org.json.JSONArray)facebook.fql_query(query); // query return an object.  Casting it as a String
				servletOutput.println("User Name is " + resultArray);
			}
			catch( FacebookException ex )
			{
				servletOutput.println(">Error: Couldn't talk to Facebook> "  + ex );
			}
		} //else user is logged in
		servletOutput.close();
	}  // end doPost()

    public static void main(String[] args) throws Exception{
        Server server = new Server(Integer.valueOf(System.getenv("PORT")));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new FacebookIntegrationServ()),"/*");
        server.start();
        server.join();
    }
}
