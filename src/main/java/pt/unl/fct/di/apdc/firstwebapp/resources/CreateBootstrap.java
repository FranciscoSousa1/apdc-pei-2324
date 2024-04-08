package pt.unl.fct.di.apdc.firstwebapp.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreException;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

@Path("/bootstrap")
public class CreateBootstrap {
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	@POST
	@Path("/creation")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createBootstrap() {
		String bootstrapName = "user001SU";
		String bootstrapPassword = "supremepassword01";
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(bootstrapName);
		Entity user = null;
		try {
			user = datastore.get(userKey);
		} catch (DatastoreException e) {
		}
		if (user == null) {
			user = Entity.newBuilder(userKey).set("username", bootstrapName)
					.set("password", DigestUtils.sha512Hex(bootstrapPassword)).set("role", "SU").set("state", "INACTIVE").build();
			datastore.add(user);
			return Response.ok().entity("Creation of Super User Succesful!").build();
		}
		return Response.status(Status.FORBIDDEN).entity("The SU user already exists.").build();
	}

}
