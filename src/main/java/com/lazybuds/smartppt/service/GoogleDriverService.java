package com.lazybuds.smartppt.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Delete;
import com.google.api.services.drive.Drive.Files.Insert;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

@Component
public class GoogleDriverService {

	private static final String APPLICATION_PRESENTATION = "application/vnd.openxmlformats-officedocument.presentationml.presentation";

	private static final String GOOGLE_APPS_FOLDER = "application/vnd.google-apps.folder"; 
	
	/** Application name. */
	private static final String APPLICATION_NAME = "Drive API Java Quickstart";

	/** Directory to store user credentials for this application. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(
			System.getProperty("user.home"),
			".credentials/drive-java-quickstart");

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/** Global instance of the scopes required by this quickstart. */
	private static final List<String> SCOPES = Arrays
			.asList(DriveScopes.DRIVE_FILE);

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	public List<String> lisFiles() throws IOException {
		Drive service = getDriveService();
		List<String> filesList = new ArrayList<String>();

		FileList result = service.files().list().setMaxResults(10).execute();
		List<File> files = result.getItems();
		if (files == null || files.size() == 0) {
		} else {
			for (File file : files) {
				filesList.add(file.getId());
			}
		}
		return filesList;
	}
	
	public String createFolder(String folderName) throws IOException {
		
		Drive service = getDriveService();
		File folder = new File();
		
		folder.setTitle(folderName);
		folder.setShared(true);
		folder.setMimeType(GOOGLE_APPS_FOLDER);
		Insert result = service.files().insert(folder);
		File resultFolder = result.execute();
		
		return resultFolder.getId();
	}
	
	public String uploadFile(byte[] data,String name,String parentFolderId) throws IOException{
		
		Drive service = getDriveService();
		File file = new File();
		
		file.setTitle(name);
		
		file.setParents(new ArrayList<ParentReference>());
		
		ParentReference parentReference = new ParentReference();
		parentReference.setId(parentFolderId);
		
		file.getParents().add(parentReference);
		
		file.setShared(true);
		
		file.setMimeType(APPLICATION_PRESENTATION);
		
		String fileId = null;
		{
			java.io.File fileContent = new java.io.File(name);
			FileOutputStream fileOutputStream = new FileOutputStream(fileContent);
			fileOutputStream.write(data);
			fileOutputStream.close();
			
			FileContent mediaContent = new FileContent(APPLICATION_PRESENTATION, fileContent);
			Insert result = service.files().insert(file,mediaContent);
			result.setConvert(Boolean.TRUE);
			File resultFile = result.execute();
			fileId = resultFile.getId();
			fileContent.delete();
		}

		return fileId;
	}
	
	public String insertFile(String filePath,String parentFolderId) throws IOException{
		Drive service = getDriveService();
		File file = new File();
		
		file.setTitle(filePath);
		
		file.setParents(new ArrayList<ParentReference>());
		
		ParentReference parentReference = new ParentReference();
		parentReference.setId(parentFolderId);
		
		file.getParents().add(parentReference);
		
		file.setShared(true);
		
		file.setMimeType(APPLICATION_PRESENTATION);
		
		java.io.File fileContent = new java.io.File(filePath);
		FileContent mediaContent = new FileContent(APPLICATION_PRESENTATION, fileContent);
		
		Insert result = service.files().insert(file,mediaContent);
		File resultFile = result.execute();
		
		return resultFile.getId();
	}
	
	public void deleteFileFolder(String fileId) throws IOException{
		Drive service = getDriveService();
		Delete delete = service.files().delete(fileId);
		delete.execute();
	}

	public static Drive getDriveService() throws IOException {
		Credential credential = authorize();
		return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME).build();
	}

	public static Credential authorize() throws IOException {
		// Load client secrets.
		InputStream in = GoogleDriverService.class
				.getResourceAsStream("/google-auth.json");
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
				JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
				.setDataStoreFactory(DATA_STORE_FACTORY)
				.setAccessType("offline").build();
		Credential credential = new AuthorizationCodeInstalledApp(flow,
				new LocalServerReceiver()).authorize("user");
		System.out.println("Credentials saved to "
				+ DATA_STORE_DIR.getAbsolutePath());
		System.out.println(credential.getAccessToken());
		return credential;
	}
}
