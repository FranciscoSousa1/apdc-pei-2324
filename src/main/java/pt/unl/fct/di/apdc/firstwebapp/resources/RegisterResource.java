package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Entity;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.DataValidation;
import pt.unl.fct.di.apdc.firstwebapp.util.RegisterData;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {

	/**
	 * Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());

	private final Gson g = new Gson();

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public RegisterResource() {
	} // Nothing to be done here

	@POST
	@Path("/v2")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doRegister(RegisterData data) {
		LOG.fine("Attempt to register user: " + data.username);
		String status = DataValidation.dataValidation(data);
		if (!status.contains("valid")) {
			return Response.status(Status.BAD_REQUEST).entity(status).build();
		}
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Entity user = datastore.get(userKey);
		if (user != null) {
			return Response.status(Status.FORBIDDEN).entity("User already exists").build();
		} else {
			user = Entity.newBuilder(userKey).set("username", data.username).set("password", data.password)
					.set("email", data.email).set("name", data.name).set("telephone", data.telephone)
					.set("visibility", data.visibility).set("ocupation", data.ocupation)
					.set("workplace", data.workplace).set("address", data.address).set("postalCode", data.postalCode)
					.set("taxIdentification", data.taxIdentification).set("role", "User").set("state", "INACTIVE")
					.build();
			datastore.put(user);
			return Response.ok().entity("Register succesful!").build();
		}
	}
}
