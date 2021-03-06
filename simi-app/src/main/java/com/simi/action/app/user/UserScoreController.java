package com.simi.action.app.user;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.com.duiba.credits.sdk.CreditConsumeParams;
import cn.com.duiba.credits.sdk.CreditNotifyParams;
import cn.com.duiba.credits.sdk.CreditTool;

import com.github.pagehelper.PageInfo;
import com.meijia.utils.DateUtil;
import com.meijia.utils.MobileUtil;
import com.meijia.utils.OrderNoUtil;
import com.meijia.utils.StringUtil;
import com.meijia.utils.TimeStampUtil;
import com.simi.action.app.BaseController;
import com.simi.common.ConstantMsg;
import com.simi.common.Constants;
import com.simi.po.model.order.OrderScore;
import com.simi.po.model.user.UserDetailScore;
import com.simi.po.model.user.Users;
import com.simi.service.async.UserMsgAsyncService;
import com.simi.service.async.UserScoreAsyncService;
import com.simi.service.order.OrderScoreService;
import com.simi.service.user.UserDetailScoreService;
import com.simi.service.user.UsersService;
import com.simi.vo.AppResultData;
import com.simi.vo.user.UserDetailScoreSearchVo;
import com.simi.vo.user.UserMsgSearchVo;

@Controller
@RequestMapping(value = "/app/user")
public class UserScoreController extends BaseController {

	@Autowired
	private UserDetailScoreService userDetailScoreService;

	@Autowired
	private UsersService userService;

	@Autowired
	private OrderScoreService orderScoreService;
	
	@Autowired
	private UserScoreAsyncService userScoreAsyncService;
	
	@Autowired
	private UserMsgAsyncService userMsgAsyncService;

	// 跳转到兑吧商城接口
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "score_shop", method = RequestMethod.GET)
	public String scoreShop(@RequestParam("user_id") Long userId) {

		Users users = userService.selectByPrimaryKey(userId);

		// 判断是否为注册用户，非注册用户返回 999
		if (users == null) {
			return "";
		}

		CreditTool tool = new CreditTool("uxPf5BT5hfJ5NyUMehAoaFcs2EL", "2CByiHjX9U17oC6K73DDGqUBigtP");

		Map params = new HashMap();
		params.put("uid", userId.toString());
		params.put("credits", users.getScore().toString());
		// if(redirect!=null){
		// //redirect是目标页面地址，默认积分商城首页是：http://www.duiba.com.cn/chome/index
		// //此处请设置成一个外部传进来的参数，方便运营灵活配置
		// params.put("redirect",redirect);
		// }
		String url = tool.buildUrlWithSign("http://www.duiba.com.cn/autoLogin/autologin?", params);

		return "redirect:" + url;

	}

	// 兑吧积分消费的兑换接口

	// 兑吧处理结果通知接口.

	// 7. 我的积分明细接口

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "get_score", method = RequestMethod.GET)
	public AppResultData<Object> myScore(
			@RequestParam("user_id") Long userId, 
			@RequestParam(value = "page", required = false, defaultValue = "1") int page) {
		AppResultData<Object> result = new AppResultData<Object>(Constants.SUCCESS_0, ConstantMsg.SUCCESS_0_MSG, "");
		UserDetailScoreSearchVo searchVo = new UserDetailScoreSearchVo();
		searchVo.setUserId(userId);
		
		//最近30天.
		Date today = DateUtil.getNowOfDate();
		String startTimeStr = DateUtil.addDay(today , -30, Calendar.DATE, "yyyy-MM-dd 00:00:00");
		Long startTime = TimeStampUtil.getMillisOfDay(startTimeStr) / 1000;
		Long endTime = TimeStampUtil.getEndOfToday();
		
		searchVo.setStartTime(startTime);
		searchVo.setEndTime(endTime);
		PageInfo rs = userDetailScoreService.selectByListPage(searchVo, page, Constants.PAGE_MAX_NUMBER);
		List<UserDetailScore> list = rs.getList();
		result.setData(list);
		return result;
	}

	// 兑吧积分兑换通知接口，生成订单
	// 对应文档
	// http://docs.duiba.com.cn/tech_doc_book/server/consume_credits_api.html
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value = "notify_score", method = RequestMethod.GET)
	public Map notifyScore(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CreditTool tool = new CreditTool("uxPf5BT5hfJ5NyUMehAoaFcs2EL", "2CByiHjX9U17oC6K73DDGqUBigtP");
		
		CreditConsumeParams params= tool.parseCreditConsume(request);//利用tool来解析这个请求
		

		Map<String, String> resultMap = new HashMap<String, String>();
		
		String uid = params.getUid();//用户id
		Long credits = params.getCredits(); // 本次兑换扣除的积分
		String appKey = params.getAppKey(); // 接口appKey，应用的唯一标识
		Date timestamp = params.getTimestamp(); // 1970-01-01开始的时间戳，毫秒为单位。
		String description = params.getDescription(); // 本次积分消耗的描述(带中文，请用utf-8进行url解码)
		String orderNum = params.getOrderNum(); // 兑吧订单号(请记录到数据库中)
		String type  = params.getType(); // 兑换类型：alipay(支付宝), qb(Q币), coupon(优惠券), object(实物),
										// phonebill(话费), phoneflow(流量), virtual(虚拟商品),
										// turntable(大转盘),
										// singleLottery(单品抽奖)，hdtoolLottery(活动抽奖),manualLottery(手动开奖),gameLottery(游戏)
		Integer facePrice  = params.getFacePrice();     // 兑换商品的市场价值，单位是分，请自行转换单位
		Integer actualPrice  = params.getActualPrice(); // 此次兑换实际扣除开发者账户费用，单位为分
		String ip  = params.getIp(); 					// 用户ip，不保证获取到
		Boolean waitAudit  = params.isWaitAudit(); // 是否需要审核(如需在自身系统进行审核处理，请记录下此信息)
		String paramsStr = params.getParams(); // 详情参数，不同的类型，返回不同的内容，中间用英文冒号分隔。(支付宝类型带中文，请用utf-8进行解码)
//						// 实物商品：返回收货信息(姓名:手机号:省份:城市:区域:详细地址)、支付宝：返回账号信息(支付宝账号:实名)、话费：返回手机号、QB：返回QQ号
		String sign = ""; // MD5签名，详见签名规则
		
		
		
		if (StringUtil.isEmpty(uid)) {
			resultMap.put("status", "fail");
			resultMap.put("errorMessage", "用户ID为空");
			resultMap.put("bizId", "");
			resultMap.put("credits", "0");
			return resultMap;
		}

		Long userId = Long.valueOf(uid);
		Users u = userService.selectByPrimaryKey(userId);

		Integer score = u.getScore();
		Integer consumeScore = credits.intValue();
		if (score < consumeScore) {
			resultMap.put("status", "fail");
			resultMap.put("errorMessage", "积分不足");
			resultMap.put("bizId", "");
			resultMap.put("credits", "0");
			return resultMap;
		}

		String orderNo = "";
		orderNo = String.valueOf(OrderNoUtil.genOrderNo());

		OrderScore record = orderScoreService.initOrderScore();

		record.setOrderNo(orderNo);
		record.setUserId(userId);
		record.setMobile(u.getMobile());

		record.setCredits(consumeScore);
		record.setAppkey(appKey);
		record.setTimestamp(String.valueOf(timestamp.getTime()));
		record.setDescription(description);
		record.setOrdernum(orderNum);
		record.setType(type);
		record.setFaceprice(facePrice.toString());
		record.setActualprice(actualPrice.toString());
		record.setIp(ip);
		record.setWaitaudit(String.valueOf(waitAudit));
		record.setParams(paramsStr);
		record.setSign(sign);

		orderScoreService.insert(record);

		Integer restScore = score - consumeScore;
		resultMap.put("status", "ok");
		resultMap.put("errorMessage", "");
		resultMap.put("bizId", orderNo);
		resultMap.put("credits", restScore.toString());

		return resultMap;
	}

	// 订单兑换成功/失败消息的接收接口
	// 对应文档
	// http://docs.duiba.com.cn/tech_doc_book/server/notify_api.html
	@RequestMapping(value = "result_score.do", method = RequestMethod.GET)
	public void resultScore(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		CreditTool tool = new CreditTool("uxPf5BT5hfJ5NyUMehAoaFcs2EL", "2CByiHjX9U17oC6K73DDGqUBigtP");
		
		CreditNotifyParams params= tool.parseCreditNotify(request);
		
		
		
		String orderNum = params.getOrderNum();
		if (params.isSuccess()) {
			OrderScore record = orderScoreService.selectByOrderNum(orderNum);
			
			if (record != null && !record.getOrderStatus().equals((short)1)) {
				
				System.out.println("积分兑换成功");
				
				record.setOrderStatus((short) 1);
				record.setUpdateTime(TimeStampUtil.getNowSecond());
				orderScoreService.updateByPrimaryKeySelective(record);
				
				Long orderId = record.getOrderId();
				Long userId = record.getUserId();
				Integer score = record.getCredits();
				String desc = "积分兑换:" + record.getDescription();
				
				//积分消费
				userScoreAsyncService.consumeScore(userId, score, "duiba", orderId.toString(), desc);
				
				//首页用户消息
				userMsgAsyncService.newActionAppMsg(userId, orderId, "duiba", "积分兑换", desc, "");
			}
			
		} else {
			System.out.println("积分兑换失败.");
		}
		
		response.getOutputStream().write("ok".getBytes());
	}
	
	//获取用户的积分排名，在月度的积分排名
	@RequestMapping(value = "get_score_ranking", method = RequestMethod.GET)
	public AppResultData<Object> getUserScoreRanking(@RequestParam("user_id") Long userId) {
		AppResultData<Object> result = new AppResultData<Object>(Constants.SUCCESS_0, ConstantMsg.SUCCESS_0_MSG, "");
		
		UserDetailScoreSearchVo searchVo = new UserDetailScoreSearchVo();
		String startDay = DateUtil.getFirstDayOfMonth(DateUtil.getYear(), DateUtil.getMonth());
		String enDay = DateUtil.getLastDayOfMonth();
		
		Long startTime = TimeStampUtil.getMillisOfDayFull(startDay + " 00:00:00") / 1000;
		Long endTime = TimeStampUtil.getMillisOfDayFull(enDay + " 23:59:59")/1000;
		searchVo.setStartTime(startTime);
		searchVo.setEndTime(endTime);
		
		List<HashMap> rankList = userDetailScoreService.scoreRanking(searchVo);
		List<HashMap> topRanks = new ArrayList<HashMap>();
		int curRank = 0;
		for (int i = 0 ; i < rankList.size(); i++) {
			HashMap item = rankList.get(i);
			Long uId = Long.valueOf(item.get("user_id").toString());
			Integer score = Integer.valueOf(item.get("score").toString());
			String rowNumStr = item.get("rownum").toString();
			double rowNumDouble = Double.valueOf(rowNumStr);
			int rowNum = (int) Math.floor(rowNumDouble);
			Users u = userService.selectByPrimaryKey(uId);
			if (i < 10) {
				HashMap<String, String> vo = new HashMap<String, String>();
				vo.put("user_id", String.valueOf(uId));
				String name = u.getName();
				
				if (StringUtil.isEmpty(u.getName())) {
					name = MobileUtil.getMobileX(u.getMobile());
				}
				vo.put("name", name);
				vo.put("rownum", String.valueOf(rowNum));
				vo.put("score", String.valueOf(score));
				topRanks.add(vo);
			}
			
			if (uId.equals(userId)) {
				curRank = rowNum;
			}
		}
		
		Map<String, Object> datas = new HashMap<String, Object>();
		datas.put("top_ranks", topRanks);
		datas.put("cur_rank", curRank);
		result.setData(datas);

		return result;
	}
}
