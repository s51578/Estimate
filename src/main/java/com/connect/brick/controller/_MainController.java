package com.connect.brick.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.connect.brick.config.RequestMappingConstants;
import com.connect.brick.model.ConsReview;
import com.connect.brick.model.DpMaterial;
import com.connect.brick.model.DpPopular;
import com.connect.brick.service.ConsService;
import com.connect.brick.service.DisplayService;

@Controller
@ComponentScan("com.connect.brick.service")
public class _MainController {

	@Value("${upload.image.locations.path}")
	private String uploadImageLocationPath;

	@Autowired
	private DisplayService displayService;
	
	@Autowired 
	private ConsService consService;

//	@RequestMapping(value = RequestMappingConstants._INDEX_URL, method = RequestMethod.GET)
//	public String home(Model model, Authentication user) {
//		model.addAttribute("index",false);
//		return "index";
//	}

	@RequestMapping(value = RequestMappingConstants._INDEX_URL, method = RequestMethod.GET)
	public String home(Model model, Authentication user) {
		//model.addAttribute("index",false);
		
		return "redirect:" + RequestMappingConstants._GOOGLE_FORM;
	}
	
	@RequestMapping(value = RequestMappingConstants._MAIN_URL, method = RequestMethod.GET)
	public String main(Model model, Authentication user, HttpServletRequest request) {

		//List<Contents> contentses = displayService.getContentsAll();
		//List<Contents> storeRankingUpdateSortList = displayService.getContentsUpdateSortLimit4();
		List<DpMaterial> storeRankingUpdateSortList = displayService.getDpMaterialUpdateSortLimit4();
		//List<Recommand> recommandlist = displayService.getRecommandTop3();
		//List<Recommand> recommandlist = displayService.getRecommandAll();
		List<DpMaterial> mdPickList = displayService.getMdPickAll();
		List<DpPopular> popularFourlist = displayService.getPopularTop4();
		List<DpPopular> popularEightList = displayService.getPopularTop8();
		//List<Contents> salesPriceSortList = displayService.getContentsSalesPriceSortLimit4();
		List<DpMaterial> salesPriceSortList = displayService.getDpMaterialSalesPriceSortLimit4();
		List<ConsReview> consReviewLists = consService.getConsReviewAll();
		model.addAttribute("mdPickList", mdPickList);
		model.addAttribute("popularEightList", popularEightList);
		model.addAttribute("consreviewLists",consReviewLists);
		
		model.addAttribute("storeRankingPopularList", popularFourlist);
		model.addAttribute("storeRankingPriceSortList", salesPriceSortList);
		model.addAttribute("storeRankingUpdateSortList", storeRankingUpdateSortList);

		return "views/main";
	}
	
}