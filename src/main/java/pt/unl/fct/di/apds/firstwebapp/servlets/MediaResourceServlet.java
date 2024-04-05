package pt.unl.fct.di.apds.firstwebapp.servlets;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class MediaResourceServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	/**
	 * Retrieves a file from GCS and returns it in the http response.
	 * If the request path is /gcs/Foo/Bar this will be interpreted as
	 * a request to read the GCS file named Bar in the bucked Foo.
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		//Download file from a specified bucket. The request must have the form /gcs/<bucket>/<object>
		Storage storage = StorageOptions.getDefaultInstance().getService();
		//Parse the request URL
		Path objectPath = Paths.get(req.getPathInfo());
		if (objectPath.getNameCount() != 2)
		{
			throw new IllegalArgumentException("The URL is not formed as expected. " + "Excepting /gcs/<bucket>/<object>");
		}
		//Get the bucket and the object names
		String bucketName = objectPath.getName(0).toString();
		String srcFilename = objectPath.getName(1).toString();
		Blob blob = storage.get(BlobId.of(bucketName, srcFilename));
		//Download object to the output stream. See Google's documentation.
		blob.downloadTo(resp.getOutputStream());
	}
	public void doPost(HttpServletRequest req, HttpServletResponse resp, InputStream content) throws IOException {
		//Upload file to specified bucket. The request must have the form /gcs/<bucket>/<object>
		Path objectPath = Paths.get(req.getPathInfo());
		if (objectPath.getNameCount() != 2)
		{
			throw new IllegalArgumentException("The URL is not formed as expected. " + "Excepting /gcs/<bucket>/<object>");
		}
		//Get the bucket and object from the URL
		String bucketName = objectPath.getName(0).toString();
		String srcFileName = objectPath.getName(1).toString();
		
		//Upload to Google Cloud Storage (See Google's documentation)
		Storage storage = StorageOptions.getDefaultInstance().getService();
		BlobId blobId = BlobId.of(bucketName, srcFileName);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(req.getContentType()).build();
		
		//The following is deprecated since it is better to upload directly to GCS from the client
		//Blob blob = storage.create(blobInfo, req.getInputStream());
		/*
		 * Como fazer ficheiro publico:
		 * BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setAcl(Collections.singletonList(Acl.newBuilder(Acl.User.ofAllUsers(),
					Acl.Role.READER).build())).setContentType(req.getContentType()).build();
		 */
		 Storage.BlobWriteOption precondition;
		    if (storage.get(bucketName, srcFileName) == null) {
		      // For a target object that does not yet exist, set the DoesNotExist precondition.
		      // This will cause the request to fail if the object is created before the request runs.
		      precondition = Storage.BlobWriteOption.doesNotExist();
		    } else {
		      // If the destination already exists in your bucket, instead set a generation-match
		      // precondition. This will cause the request to fail if the existing object's generation
		      // changes before the request runs.
		      precondition =
		          Storage.BlobWriteOption.generationMatch(
		              storage.get(bucketName, srcFileName).getGeneration());
		    }
		    storage.createFrom(blobInfo, content, precondition);
	}
}
