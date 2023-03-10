package com.connect.brick.controller;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.connect.brick.component.EmailComponent;
import com.connect.brick.component.ValidComponent;
import com.connect.brick.config.RequestMappingConstants;
import com.connect.brick.model.DpMaterial;
import com.connect.brick.model.Estimate;
import com.connect.brick.model.EstimateDoc;
import com.connect.brick.model.SubMaterial;
import com.connect.brick.model.material.MaterialContents;
import com.connect.brick.service.AccountService;
import com.connect.brick.service.DisplayService;
import com.connect.brick.service.EstimateService;
import com.connect.brick.service.MaterialService;

@Controller
public class EstimateController {

	@Autowired
    private MaterialService materialService;
	
	@Autowired
    private DisplayService displayService;
	
	@Autowired
    private EstimateService estimateService;
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	private EmailComponent emailComponent;
	
	@Autowired
	private ValidComponent validComponent;
	
	@RequestMapping(value=RequestMappingConstants._ESTIMATE_SEND_EMAIL, method=RequestMethod.POST)
    public String orderdetail(Model model, HttpServletRequest request,
    		@RequestParam(name="contents") String contents,
    		@RequestParam(name="address") String address,
    		@RequestParam(name="orderNo") Long orderNo) {
		
		try {
			emailComponent.gmailSend(address, "[??????] ?????? ???????????? ????????? ??????????????????", contents);
			
			//Order order = estimateService.getOrderByNo(orderNo);
			
			//estimateService._send_Email_from_order(order);
			
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "redirect:"+RequestMappingConstants._MAIN_URL;
	}
	
	@RequestMapping(value=RequestMappingConstants._ESTIMATE_DOC_LOGIN + "{num}", method=RequestMethod.GET)
    public String login(Model model, HttpServletRequest request,
    		@PathVariable("num") Long num) {

		boolean isEstimateNo = estimateService.isEstimateDocByNo(num);
		
		if(!isEstimateNo) {
			model.addAttribute("msg", "???????????? ?????????????????? ?????????????????????.");
			return "redirect:"+ RequestMappingConstants._ERROR_LOAD + HttpStatus.NOT_FOUND.value();
		}
		
		model.addAttribute("num", num);
		model.addAttribute("whapleStatusBar", true);
		
		return "views/service/estimate/estimate_login";
	}
	
	@RequestMapping(value=RequestMappingConstants._ESTIMATE_DOC, method= {RequestMethod.POST, RequestMethod.GET})
    public String cert(Model model, HttpServletRequest request,
    		@RequestParam(name = "num", required=false) Long num,
    		@RequestParam(name = "pwd", required=false) String pwd) {
		
		if(num==null || pwd==null) {
			model.addAttribute("msg", "????????? ???????????????.");
			return "redirect:"+ RequestMappingConstants._ERROR_LOAD + HttpStatus.BAD_REQUEST.value();
		}
		
//		byte[] decode = null;
//		
//		try {
//			decode = Base64.getUrlDecoder().decode(encodeNum);
//		} catch (Exception e) {
//			model.addAttribute("msg", "????????? URL?????????");
//			return "redirect:"+ RequestMappingConstants._ERROR_LOAD + HttpStatus.BAD_REQUEST.value();
//		}
//		
//		Long decodeLong = Long.valueOf( new String(decode) );
		boolean isEstimateNo = estimateService.isEstimateDocByNo(num);
		
		if(!isEstimateNo) {
			model.addAttribute("msg", "???????????? ?????????????????? ?????????????????????.");
			return "redirect:"+ RequestMappingConstants._ERROR_LOAD + HttpStatus.NOT_FOUND.value();
		}
		
		EstimateDoc estimateDoc = estimateService.getEstimateDocByNo(num);
		
		String phone = null;
		
		try {
			
			phone = estimateDoc.getEstimate().getOrder().getCustomer().getPhoneNum();
			
			if(phone == null)
				throw new Exception();
				
		} catch (Exception e) {
			model.addAttribute("msg", "??????????????? ?????????????????????. - ???????????? ??????");
			return "redirect:"+ RequestMappingConstants._ERROR_LOAD + HttpStatus.INTERNAL_SERVER_ERROR.value();
		}
		
		try {
			
			boolean check = validComponent.isCheckLoginByPhoneNum(phone, pwd);
			
			if(!check) {
				model.addAttribute("msg", "??????????????? ???????????? ????????????");
				return "redirect:"+ RequestMappingConstants._ERROR_LOAD + HttpStatus.UNAUTHORIZED.value();
			}	
			
		} catch (NumberFormatException e) {
			model.addAttribute("msg", "????????? ?????????????????????");
			return "redirect:"+ RequestMappingConstants._ERROR_LOAD + HttpStatus.CONFLICT.value();
		} catch (Exception e) {
			model.addAttribute("msg", "??????????????? ?????????????????????");
			return "redirect:"+ RequestMappingConstants._ERROR_LOAD + HttpStatus.INTERNAL_SERVER_ERROR.value();
		}
		
		LocalDateTime laterTime = estimateDoc.getRegDate();
		//laterTime??? ???????????? regDate??? ????????????
		LocalDateTime oneMonthLater = laterTime.plusDays(30);
		//oneMonthLater??? laterTime??? 30?????? ?????? ????????? ???????????? ?????????.
		model.addAttribute("oneMonthLater", oneMonthLater);
		model.addAttribute("estimateDoc", estimateDoc);
		
		return "views/service/estimate/estimate_doc";
	}
	
	
}