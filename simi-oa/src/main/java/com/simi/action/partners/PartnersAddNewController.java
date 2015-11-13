package com.simi.action.partners;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageInfo;
import com.meijia.utils.StringUtil;
import com.meijia.utils.common.extension.ArrayHelper;
import com.meijia.utils.common.extension.StringHelper;
import com.simi.action.BaseController;
import com.simi.models.TreeModel;
import com.simi.models.extention.TreeModelExtension;
import com.simi.oa.auth.AccountAuth;
import com.simi.oa.auth.AuthHelper;
import com.simi.oa.auth.AuthPassport;
import com.simi.oa.common.ConstantOa;
import com.simi.po.model.dict.DictCity;
import com.simi.po.model.dict.DictRegion;
import com.simi.po.model.partners.PartnerLinkMan;
import com.simi.po.model.partners.PartnerRefCity;
import com.simi.po.model.partners.PartnerRefRegion;
import com.simi.po.model.partners.PartnerRefServiceType;
import com.simi.po.model.partners.PartnerServiceType;
import com.simi.po.model.partners.Partners;
import com.simi.po.model.partners.SpiderPartner;
import com.simi.service.dict.CityService;
import com.simi.service.dict.RegionService;
import com.simi.service.partners.PartnerLinkManService;
import com.simi.service.partners.PartnerRefCityService;
import com.simi.service.partners.PartnerRefRegionService;
import com.simi.service.partners.PartnerServiceTypeService;
import com.simi.service.partners.PartnersService;
import com.simi.service.partners.SpiderPartnerService;
import com.simi.vo.partners.PartnerFormVo;
import com.simi.vo.partners.PartnersSearchVo;


/**
 * @description：
 * @author： kerryg
 * @date:2015年8月11日 
 */
@Controller
@RequestMapping(value = "/partnersAdd")
public class PartnersAddNewController extends BaseController{


	@Autowired
	private PartnersService partnersService;
	
	@Autowired
	private SpiderPartnerService spiderPartnerService;
	
	@Autowired
	private PartnerLinkManService partnerLinkManService;

	@Autowired
	private PartnerServiceTypeService partnerServiceTypeService;
	
	@Autowired
	private PartnerRefRegionService partnerRefRegionService;
	
	@Autowired
	private PartnerRefCityService partnerRefCityService;
	
	@Autowired
	private CityService cityService;
	
	@Autowired
	private RegionService regionService;

	/**
	 * 跳转到新增服务提供商的页面
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
   // @AuthPassport
	@RequestMapping(value = "/partnerAddNewForm", method = { RequestMethod.GET })
	public String spiderPartnerForm(Model model, HttpServletRequest request,
			HttpServletRequest response)  {
    	
    	Partners partners = partnersService.iniPartners();
    	PartnerFormVo partnerFormVo = new PartnerFormVo();
		try {
			BeanUtils.copyProperties(partnerFormVo, partners);
		}catch (Exception e) {
			e.printStackTrace();
		}
		//获得服务商的联系人
		List<PartnerLinkMan> linkMan = new ArrayList<PartnerLinkMan>();
		//保证至少有一个，默认为空的列表
		if (linkMan == null || linkMan.size() == 0) {
			PartnerLinkMan linkManVo = partnerLinkManService.initPartnerLinkMan();
			linkMan.add(linkManVo);
		}
		partnerFormVo.setLinkMan(linkMan);
    	/**
    	 * 包装partner为Vo
    	 */
    	PartnerFormVo partnerFormVoItem  = partnersService.selectPartnerFormVoByPartnerFormVo(partnerFormVo);
    	
    	/**
    	 *  获得提供商所关联的服务类型 
    	 */
    	List<Long> checkedPartnerTypeIds = new ArrayList<Long>();
		List<Integer> checkedPartnerTypeIntegers = new ArrayList<Integer>();
		if(partnerFormVoItem.getChildList()!=null){
			List<PartnerServiceType> list = partnerFormVoItem.getChildList();
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				PartnerServiceType partnerServiceType = (PartnerServiceType) iterator.next();
				if(partnerServiceType !=null){
					checkedPartnerTypeIds.add(partnerServiceType.getId());
					checkedPartnerTypeIntegers.add(partnerServiceType.getId().intValue());
				}
			}
		}
		if(!model.containsAttribute("partners")){
			Long[] checkedAuthorityIdsArray=new Long[checkedPartnerTypeIds.size()];
			checkedPartnerTypeIds.toArray(checkedAuthorityIdsArray);
			partnerFormVo.setPartnerTypeIds(checkedAuthorityIdsArray);
			model.addAttribute("partners", partnerFormVo);
		}
    	String expanded = ServletRequestUtils.getStringParameter(request, "expanded", null);
		List<TreeModel> children=TreeModelExtension.ToTreeModels(partnerServiceTypeService.listChain((short) 0), null, checkedPartnerTypeIntegers, StringHelper.toIntegerList( expanded, ","));
		List<TreeModel> treeModels=new ArrayList<TreeModel>(Arrays.asList(new TreeModel(null,null,"根节点",false,false,false,children)));
		model.addAttribute("treeDataSource", JSONArray .fromObject(treeModels, new JsonConfig()).toString());

		/**
		 * 获取北,上,广,深等城市和地区字典信息
		 */
		List<Long> cityIds = new ArrayList<Long>();
		cityIds.add(2L);
		cityIds.add(3L);
		cityIds.add(74L);
		cityIds.add(200L);
		List<DictCity> dictCityList = cityService.selectByCityIds(cityIds);
		List<DictRegion> dictReigionList = regionService.selectByCityIds(cityIds);		
	
		model.addAttribute("dictCityList", dictCityList);
		model.addAttribute("dictReigionList", dictReigionList);
		model.addAttribute("partners", partnerFormVo);
		return "partners/partnerAddNewForm";
	}
    
	
	/**
	 * 新增服务提供商
	 *
	 * @param request
	 * @param model
	 * @param partners
	 * @param result
	 * @return
	 */
	//@AuthPassport
	@RequestMapping(value = "/savePartnerAddNewForm", method = { RequestMethod.POST })
	public String doPartnerForm(HttpServletRequest request, Model model,
			@ModelAttribute("partners") PartnerFormVo partners,
			
			BindingResult result) {
		model.addAttribute("requestUrl", request.getServletPath());
		model.addAttribute("requestQuery", request.getQueryString());
	//	Long spiderPartnerId = Long.valueOf(request.getParameter("spiderPartnerId"));
		Long partnerId = partners.getPartnerId();
		//SpiderPartner spiderPartner = spiderPartnerService.selectByPrimaryKey(spiderPartnerId);
		//根据采集服务商名称进行排重
	//	List<Partners> partnersList =  partnersService.selectByCompanyName(spiderPartner.getCompanyName());
		
		//获取登录的用户
    	AccountAuth accountAuth=AuthHelper.getSessionAccountAuth(request);

    	Partners partnersItem = partnersService.iniPartners();
    	if (partnerId == null) {
    		partnerId = 0L;
    	}

			try {
				BeanUtils.copyProperties(partnersItem, partners);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
			partnersService.insertSelective(partnersItem);
			
			/*SpiderPartner spiderPartner2 = spiderPartnerService.selectByPrimaryKey(partnersItem.getSpiderPartnerId());
			spiderPartner2.setStatus(partnersItem.getStatus());
			spiderPartner2.setAddr(partners.getAddr());
			spiderPartnerService.updateByPrimaryKey(spiderPartner2);*/
		//}
		
		/**
		 * 保存服务商选中的服务类型
		 */
		partnersService.savePartnerToPartnerType(partnersItem.getPartnerId(), ArrayHelper.removeArrayLongItem(partners.getPartnerTypeIds(), new Integer(0)));
	
		/**
		 * 操作partner_ref_region表更新
		 */
		//1、先删除原来的数据
	//	partnersService.deleteRegionByPartnerId(partnersItem.getPartnerId());
		String tempRegionId = request.getParameter("regionIdStr");
		if(!StringUtil.isEmpty(tempRegionId)){
			Long regionIdLong = 0L;
			String regionId[]= tempRegionId.split(",");
			//循环批量插入
			if(regionId!=null){
			for (int i = 0; i < regionId.length; i++) {
				if(!regionId[i].equals(",")){
				regionIdLong = Long.valueOf(regionId[i]);
				PartnerRefRegion partnerRefRegion = partnerRefRegionService.initPartnerRefRegion();
				partnerRefRegion.setPartnerId(partnersItem.getPartnerId());
				partnerRefRegion.setRegionId(regionIdLong);
				partnerRefRegionService.insertSelective(partnerRefRegion);
				}
				}
			}
		}
		/**
		 * 操作partner_ref_city表更新
		 */
		//1、先删除原来的数据
		//partnerRefCityService.deleteByPartnerId(partnersItem.getPartnerId());
		String tempCityId = request.getParameter("cityIdStr");
		if(!StringUtil.isEmpty(tempCityId)){
			String cityId[] = tempCityId.split(",");
			Long cityIdLong = 0L;
			//循环批量插入
			if(cityId!=null){
			for (int i = 0; i < cityId.length; i++) {
				if(!cityId[i].equals(",")){
					cityIdLong = Long.valueOf(cityId[i]);
					PartnerRefCity partnerRefCity = partnerRefCityService.initPartnerRefCity();
					partnerRefCity.setPartnerId(partnersItem.getPartnerId());
					partnerRefCity.setCityId(cityIdLong);
					partnerRefCityService.insertSelective(partnerRefCity);
				}
				}
			}
		}
		/**
		 * 操作partnerLinkMan表
		 */
		//第一步先删除
	//	partnerLinkManService.deleteByPartnerId(partnersItem.getPartnerId());
		String linkMan[] = request.getParameterValues("linkMan");
		String linkMobile[] = request.getParameterValues("linkMobile");
		String linkTel[] = request.getParameterValues("linkTel");
		String linkJob[] = request.getParameterValues("linkJob");
		
		String linkManItem = "";
		String linkMobileItem = "";
		String linkTelItem = "";
		String linkJobItem = "";
		if(linkMobile !=null){
		for (int i = 0; i < linkMobile.length; i++) {
			PartnerLinkMan record = partnerLinkManService.initPartnerLinkMan();
			
			linkManItem = linkMan[i];
			linkMobileItem = linkMobile[i];
			if (StringUtil.isEmpty(linkManItem) || StringUtil.isEmpty(linkMobileItem)) {
				continue;
			}
			
			if (!StringUtil.isEmpty(linkTel[i])) {
				linkTelItem = linkTel[i];
			}
			
			if (!StringUtil.isEmpty(linkJob[i])) {
				linkJobItem = linkJob[i];
			}			
			record.setPartnerId(partnersItem.getPartnerId());
			record.setLinkMan(linkManItem);
			record.setLinkMobile(linkMobileItem);
			record.setLinkTel(linkTelItem);
			record.setLinkJob(linkJobItem);
			partnerLinkManService.insertSelective(record);
			}
		}
		return "redirect:list";
	} 
	
}
