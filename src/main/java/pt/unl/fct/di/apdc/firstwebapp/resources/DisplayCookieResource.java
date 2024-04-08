package pt.unl.fct.di.apdc.firstwebapp.resources;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
@Path("/cookie")
public class DisplayCookieResource {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/info")
    public String getCookieInfo(@CookieParam ("session::apdc") Cookie cookie) {
		NewCookie theCookie = new NewCookie(cookie);
		String theValue = cookie.getValue();
		String[] valueSplit = theValue.split("\\.");
		DateFormat obj = new SimpleDateFormat("dd MMM yyyy HH:mm:ss"); 
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(valueSplit[2]));
        String date = obj.format(calendar.getTime());
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(Long.parseLong(valueSplit[2]) + Long.parseLong(valueSplit[3])); 
        String expireDate = obj.format(calendar2.getTime());
        return "Name of Cookie: " + theCookie.getName() + "<br>Cookie ID: " + valueSplit[1] + "<br >Username associated with the Cookie: " + valueSplit[0] +"<br>Creation Date of Cookie: " + date +"<br>Expire Date of Cookie: " + expireDate;
    }
}
