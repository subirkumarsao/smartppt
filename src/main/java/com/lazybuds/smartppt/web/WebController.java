package com.lazybuds.smartppt.web;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.lazybuds.smartppt.service.SessionService;

@Controller
public class WebController {
		
	private static final String SESSION_ID = "SESSION_ID";
	
	@Autowired
	SessionService sessionService;
	
	@RequestMapping(value="/service/ping",method=RequestMethod.GET)
	public @ResponseBody String ping(){
		return "OK";
	}
	
	@RequestMapping(value="/barcode",method=RequestMethod.GET)
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void barcode(HttpServletRequest request, HttpServletResponse response) throws IOException, WriterException{
		
		String sessionId = (String)request.getSession().getAttribute(SESSION_ID);
		if(sessionId==null){
			sessionId = sessionService.createSession();
			request.getSession().setAttribute(SESSION_ID, sessionId);
		}
		
		System.out.println(sessionId);
		
		OutputStream out = response.getOutputStream();
		String qrCodeData = sessionId;
		
		String charset = "UTF-8"; // or "ISO-8859-1"
		
		Map hintMap = new HashMap();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

		createQRCode(qrCodeData, out, charset, hintMap, 200, 200);
		
		out.close();
	}
	
	@RequestMapping(value="/service/checklink",method=RequestMethod.GET)
	public @ResponseBody String checkSessionLink(HttpServletRequest request){
		String sessionId = (String)request.getSession().getAttribute(SESSION_ID);
		
		Boolean value = Boolean.FALSE;
		if(sessionId!=null){
			value = sessionService.checkSessionLink(sessionId);	
		}
		
		return value.toString();
	}
	
	@RequestMapping(value="/service/link/{userId}/{sessionId}",method=RequestMethod.GET)
	public void linkUser(@PathVariable String userId,@PathVariable String sessionId){
		sessionService.linkSession(sessionId, userId);
	}
	
	/**
     * Upload single file using Spring Controller
	 * @throws IOException 
     */
    @RequestMapping(value = "/fileupload", method = RequestMethod.POST)
    public String uploadFileHandler(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws IOException {
 
    	String sessionId = (String)request.getSession().getAttribute(SESSION_ID);
		if(sessionId==null){
			return "Invalid Session";
		}
        if (!file.isEmpty()) {
        	 byte[] bytes = file.getBytes();
        	 sessionService.uploadFile(sessionId, bytes);
        }
		return "redirect:/slideshow.html";
    }
    
    @RequestMapping(value="service/slidestate",method=RequestMethod.GET)
    public @ResponseBody String getSlideState(HttpServletRequest request){
    	String sessionId = (String)request.getSession().getAttribute(SESSION_ID);
		if(sessionId==null){
			return null;
		}
    	return sessionService.getSlideState(sessionId);
    }
    
    @RequestMapping(value="service/slidestate/{sessionId}/{currentSlide}",method=RequestMethod.POST)
    public @ResponseBody String setSlideState(@PathVariable String sessionId, @PathVariable String currentSlide){
    	sessionService.setSlideState(sessionId, currentSlide);
    	return "success";
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void createQRCode(String qrCodeData, OutputStream stream,
			String charset, Map hintMap, int qrCodeheight, int qrCodewidth)
			throws WriterException, IOException {
		BitMatrix matrix = new MultiFormatWriter().encode(
				new String(qrCodeData.getBytes(charset), charset),
				BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
		
		MatrixToImageWriter.writeToStream(matrix, "jpeg", stream);
	}
}