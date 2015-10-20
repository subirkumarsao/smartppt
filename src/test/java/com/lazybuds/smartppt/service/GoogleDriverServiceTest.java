package com.lazybuds.smartppt.service;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import junit.framework.Assert;


public class GoogleDriverServiceTest {
	
	
	GoogleDriverService googleDriverService = new GoogleDriverService();
	
	public void googleServiceCleanTest() throws IOException{
		List<String> files = googleDriverService.lisFiles();
		
		for(String fileId:files){
			googleDriverService.deleteFileFolder(fileId);
		}
		files = googleDriverService.lisFiles();
		Assert.assertTrue(files.isEmpty());
	}
	
	
	public void googleServiceTest() throws Exception {
		String folderId = googleDriverService.createFolder("TestFolder");
		
		URL url = GoogleDriverServiceTest.class.getClassLoader().getResource("Test.txt");
		
		String fileId = googleDriverService.insertFile(url.getFile(), folderId);
		
		googleDriverService.deleteFileFolder(fileId);
		googleDriverService.deleteFileFolder(folderId);
	}
	
	public void createFolderTest() throws IOException{
		 String id = googleDriverService.createFolder("smartppt");
		 Assert.assertNotNull(id);
		 System.out.println(id);
	}
}
