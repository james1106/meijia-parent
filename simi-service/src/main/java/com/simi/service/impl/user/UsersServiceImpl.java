package com.simi.service.impl.user;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.meijia.utils.BeanUtilsExp;
import com.meijia.utils.DateUtil;
import com.meijia.utils.ImgServerUtil;
import com.meijia.utils.SmsUtil;
import com.meijia.utils.StringUtil;
import com.meijia.utils.TimeStampUtil;
import com.meijia.utils.huanxin.EasemobIMUsers;
import com.simi.common.Constants;
import com.simi.po.dao.user.UserRef3rdMapper;
import com.simi.po.dao.user.UserRefSecMapper;
import com.simi.po.dao.user.UsersMapper;
import com.simi.po.model.admin.AdminAccount;
import com.simi.po.model.user.UserCoupons;
import com.simi.po.model.user.UserPushBind;
import com.simi.po.model.user.UserRef3rd;
import com.simi.po.model.user.UserRefSec;
import com.simi.po.model.user.Users;
import com.simi.po.model.xcloud.XcompanyStaff;
import com.simi.service.admin.AdminAccountService;
import com.simi.service.async.UserMsgAsyncService;
import com.simi.service.async.UsersAsyncService;
import com.simi.service.card.CardService;
import com.simi.service.dict.DictCouponsService;
import com.simi.service.order.OrderQueryService;
import com.simi.service.user.UserCouponService;
import com.simi.service.user.UserFriendService;
import com.simi.service.user.UserPushBindService;
import com.simi.service.user.UserRef3rdService;
import com.simi.service.user.UserRefSecService;
import com.simi.service.user.UsersService;
import com.simi.service.xcloud.XcompanyStaffService;
import com.simi.vo.UserFriendSearchVo;
import com.simi.vo.UserSearchVo;
import com.simi.vo.UsersSearchVo;
import com.simi.vo.card.CardSearchVo;
import com.simi.vo.user.UserIndexVo;
import com.simi.vo.user.UserViewVo;
import com.simi.vo.xcloud.UserCompanySearchVo;

@Service
public class UsersServiceImpl implements UsersService {

	@Autowired
	private UsersMapper usersMapper;

	@Autowired
	private UserCouponService userCouponService;

	@Autowired
	private OrderQueryService orderQueryService;

	@Autowired
	private UserRef3rdMapper userRef3rdMapper;

	@Autowired
	private UserRef3rdService userRef3rdService;

	@Autowired
	private UserRefSecService userRefSecService;

	@Autowired
	private AdminAccountService adminAccountService;

	@Autowired
	private DictCouponsService couponService;

	@Autowired
	private UserRefSecMapper userRefSecMapper;

	@Autowired
	private CardService cardService;

	@Autowired
	private UserFriendService userFriendService;

	@Autowired
	private UserPushBindService userPushBindService;

	@Autowired
	private UsersAsyncService userAsyncService;
	
	@Autowired
	private UserMsgAsyncService userMsgAsyncService;	

	@Autowired
	private XcompanyStaffService xcompanyStaffService;

	/**
	 * 新用户注册流程 1. 注册用户 2. 赠送金额
	 */
	@Override
	public Users genUsers(String introduction, String mobile, String name, short addFrom) {
		Users u = selectByMobile(mobile);
		if (u == null) {// 验证手机号是否已经注册，如果未注册，则自动注册用户，
			u = this.initUsers();
			u.setMobile(mobile);
			u.setAddFrom(addFrom);
			u.setName(name);
			u.setUserType((short) 2);
			u.setIntroduction(introduction);
			this.insertSelective(u);

			// 检测用户所在地，异步操作
			userAsyncService.userMobileCity(u.getId());

			// 新用户注册通知运营人员
			userAsyncService.newUserNotice(u.getId());

			// 默认加固定客服用户为好友
			userAsyncService.addDefaultFriends(u.getId());
			
			// 发送默认欢迎消息
			userMsgAsyncService.newUserMsg(u.getId());
		}
		return u;
	}

	@Override
	public Users genUser(String mobile, String name, short addFrom) {
		Users u = selectByMobile(mobile);
		if (u == null) {// 验证手机号是否已经注册，如果未注册，则自动注册用户，
			u = this.initUsers();
			u.setMobile(mobile);
			u.setAddFrom(addFrom);
			u.setName(name);
			this.insertSelective(u);

			// 检测用户所在地，异步操作
			userAsyncService.userMobileCity(u.getId());

			// 新用户注册通知运营人员
			userAsyncService.newUserNotice(u.getId());

			// 默认加固定客服用户为好友
			userAsyncService.addDefaultFriends(u.getId());
		}
		return u;
	}

	@Override
	public List<Users> selectByAll() {
		return usersMapper.selectByAll();
	}

	@Override
	public UserViewVo getUserViewByUserId(Long userId) {
		Users user = usersMapper.selectByPrimaryKey(userId);

		UserViewVo userInfo = new UserViewVo();
		if (user == null) {
			return userInfo;
		}

		BeanUtilsExp.copyPropertiesIgnoreNull(user, userInfo);

		String seniorRange = "";
		Date seniorEndDate = orderQueryService.getSeniorRangeDate(userId);

		if (!(seniorEndDate == null)) {
			seniorRange = "截止" + DateUtil.formatDate(seniorEndDate);
		}
		userInfo.setSeniorRange(seniorRange);

		return userInfo;
	}

	@Override
	public UserIndexVo getUserIndexVoByUserId(Users user, Users viewUser) {

		UserIndexVo vo = new UserIndexVo();

		vo.setId(viewUser.getId());
		vo.setSex(viewUser.getSex());
		vo.setHeadImg(getHeadImg(viewUser));
		vo.setProvinceName(viewUser.getProvinceName());
		vo.setUserType(viewUser.getUserType());
		vo.setName(viewUser.getName());
		vo.setRestMoney(new BigDecimal(0));
		vo.setMobile(viewUser.getMobile());
		vo.setScore(viewUser.getScore());
		if (user.getId().equals(viewUser.getId())) {
			vo.setRestMoney(viewUser.getRestMoney());
		}

		UserRef3rd userRef3rd = userRef3rdService.selectByUserIdForIm(viewUser.getId());

		if (userRef3rd != null) {
			vo.setImUserName(userRef3rd.getUsername());
		}
		vo.setPoiDistance("");

		// 计算卡片的个数
		vo.setTotalCard(0);
		CardSearchVo searchVo = new CardSearchVo();
		searchVo.setCardFrom((short) 0);
		searchVo.setUserId(viewUser.getId());

		PageInfo pageInfo = cardService.selectByListPage(searchVo, 1, Constants.PAGE_MAX_NUMBER);
		if (pageInfo != null) {
			Long totalCard = pageInfo.getTotal();
			vo.setTotalCard(totalCard.intValue());
		}

		// 计算优惠劵个数
		vo.setTotalCoupon(0);
		List<UserCoupons> list = userCouponService.selectByUserId(viewUser.getId());
		if (!list.isEmpty()) {

			UserCoupons item = null;
			List<Long> couponsIds = new ArrayList<Long>();
			Long now = TimeStampUtil.getNow();
			for (int i = 0; i < list.size(); i++) {
				item = list.get(i);
				// 已经使用过的
				// 优惠券已经过期的，都不显示
				if (item.getIsUsed().equals((short) 0) && item.getExpTime() > (now / 1000) || item.getExpTime() == 0) {
					couponsIds.add(item.getCouponId());
				} else {
					list.remove(i);
				}
			}
			vo.setTotalCoupon(list.size());

		}

		// 计算好友个数
		vo.setTotalFriends(0);
		UserFriendSearchVo searchVo1 = new UserFriendSearchVo();
		searchVo1.setUserId(viewUser.getId());
		PageInfo userFriendPage = userFriendService.selectByListPage(searchVo1, 1, Constants.PAGE_MAX_NUMBER);
		if (userFriendPage != null) {
			Long totalFriends = userFriendPage.getTotal();
			vo.setTotalFriends(totalFriends.intValue());
		}
		return vo;
	}

	@Override
	public int updateByPrimaryKeySelective(Users user) {
		return usersMapper.updateByPrimaryKeySelective(user);
	}

	@Override
	public PageInfo searchVoListPage(UserSearchVo searchVo, int pageNo, int pageSize) {

		HashMap<String, Object> conditions = new HashMap<String, Object>();
		String mobile = searchVo.getMobile();
		Long secId = searchVo.getSecId();
		List<Long> userIdList = new ArrayList<Long>();

		if (mobile != null && !mobile.isEmpty()) {
			conditions.put("mobile", mobile.trim());
		}
		if (secId != null) {
			List<UserRefSec> list = userRefSecService.selectBySecId(secId);
			for (Iterator iterator = list.iterator(); iterator.hasNext();) {
				UserRefSec userRefSec = (UserRefSec) iterator.next();
				userIdList.add(userRefSec.getUserId());
			}
		}
		if (userIdList != null && userIdList.size() > 0) {
			conditions.put("userIdList", userIdList);
		}

		if (searchVo.getUserType() != null) {
			conditions.put("userType", searchVo.getUserType());
		}

		if (searchVo.getIsApproval() != null) {
			conditions.put("isApproval", searchVo.getIsApproval());
		}

		PageHelper.startPage(pageNo, pageSize);
		List<Users> list = usersMapper.selectByListPage(conditions);
		PageInfo result = new PageInfo(list);
		return result;
	}

	/**
	 * 获取用户账号详情接口
	 */
	@Override
	public UserViewVo getUserInfo(Long userId) {
		UserViewVo vo = new UserViewVo();
		Users u = usersMapper.selectByPrimaryKey(userId);

		if (u == null) {
			return vo;
		}

		BeanUtilsExp.copyPropertiesIgnoreNull(u, vo);
		vo.setUserId(u.getId());

		if (StringUtil.isEmpty(vo.getName())) {
			vo.setName(vo.getMobile());
		}

		UserRefSec userRefSec = userRefSecMapper.selectByUserId(userId);

		// 获取用户与绑定的秘书的环信IM账号
		Map imRobot = this.getImRobot(u);
		vo.setImRobotUsername(imRobot.get("username").toString());
		vo.setImRobotNickname(imRobot.get("nickname").toString());
		vo.setSecId(0L);
		if (userRefSec != null) {

			Users secUser = usersMapper.selectByPrimaryKey(userRefSec.getSecId());
			UserRef3rd userRef3rd = userRef3rdService.selectByUserIdForIm(userRefSec.getSecId());

			if (userRef3rd == null) {

			}

			if (userRef3rd != null) {
				vo.setImSecUsername(userRef3rd.getUsername());
				vo.setImSecNickname(secUser.getName());
				vo.setSecId(userRefSec.getSecId());
			}
		} else {
			vo.setImSecUsername("");
			vo.setImSecNickname("");
		}

		vo.setIsSenior((short) 0);
		String seniorRange = "";

		Date seniorEndDate = orderQueryService.getSeniorRangeDate(userId);

		if (!(seniorEndDate == null)) {

			String endDateStr = DateUtil.formatDate(seniorEndDate);
			String nowStr = DateUtil.getToday();
			if (DateUtil.compareDateStr(nowStr, endDateStr) >= 0) {
				vo.setIsSenior((short) 1);
				seniorRange = "截止" + endDateStr;
			} else {
				seniorRange = "已过期";
			}

		}

		vo.setSeniorRange(seniorRange);

		// 用户环信IM信息
		UserRef3rd userRef3rd = this.genImUser(u);
		if (userRef3rd.getUsername().length() > 0) {
			vo.setImUsername(userRef3rd.getUsername());
			vo.setImPassword(userRef3rd.getPassword());
		}

		// 用户绑定推送设备信息
		vo.setClientId("");
		UserPushBind userPushBind = userPushBindService.selectByUserId(userId);
		if (userPushBind != null)
			vo.setClientId(userPushBind.getClientId());

		// 用户是否为某个公司的职员
		vo.setHasCompany((short) 0);
		vo.setCompanyId(0L);
		vo.setCompanyCount(0);

		UserCompanySearchVo searchVo = new UserCompanySearchVo();
		searchVo.setUserId(userId);
		searchVo.setStatus((short) 1);
		List<XcompanyStaff> companyList = xcompanyStaffService.selectBySearchVo(searchVo);

		if (!companyList.isEmpty()) {
			vo.setHasCompany((short) 1);
			vo.setCompanyCount(companyList.size());
			if (companyList.size() == 1) {
				XcompanyStaff item = companyList.get(0);
				vo.setCompanyId(item.getCompanyId());
			}
		}

		return vo;
	}

	/**
	 * 获取用户账号详情接口
	 */
	@Override
	public List<UserViewVo> getUserInfos(List<Long> userIds, Users secUser, UserRef3rd userRef3rd) {
		List<UserViewVo> result = new ArrayList<UserViewVo>();
		List<Users> userList = usersMapper.selectByUserIds(userIds);

		List<UserRef3rd> userRef3rds = userRef3rdMapper.selectByUserIds(userIds);

		Users u = null;
		for (int i = 0; i < userList.size(); i++) {

			UserViewVo vo = new UserViewVo();
			u = userList.get(i);

			BeanUtilsExp.copyPropertiesIgnoreNull(u, vo);

			vo.setUserId(u.getId());

			if (StringUtil.isEmpty(vo.getName())) {
				vo.setName(vo.getMobile());
			}

			// 获取用户与绑定的秘书的环信IM账号
			Map imRobot = this.getImRobot(u);
			vo.setImRobotUsername(imRobot.get("username").toString());
			vo.setImRobotNickname(imRobot.get("nickname").toString());

			vo.setImSecUsername(userRef3rd.getUsername());
			vo.setImSecNickname(secUser.getName());
			vo.setSecId(secUser.getId());

			vo.setIsSenior((short) 0);
			String seniorRange = "";

			Date seniorEndDate = orderQueryService.getSeniorRangeDate(u.getId());

			if (!(seniorEndDate == null)) {

				String endDateStr = DateUtil.formatDate(seniorEndDate);
				String nowStr = DateUtil.getToday();
				if (DateUtil.compareDateStr(nowStr, endDateStr) >= 0) {
					vo.setIsSenior((short) 1);
					seniorRange = "截止" + endDateStr;
				} else {
					seniorRange = "已过期";
				}

			}

			// 去掉已经到期的用户
			if (vo.getIsSenior().equals((short) 0))
				continue;

			vo.setSeniorRange(seniorRange);

			for (UserRef3rd item : userRef3rds) {
				if (item.getUserId().equals(u.getId())) {
					vo.setImUsername(item.getUsername());
					vo.setImPassword(item.getPassword());
				}
			}
			result.add(vo);

		}

		return result;
	}

	/**
	 * 查询用户与管家绑定的环信账号 1. 如果用户没有购买过管家卡，则为默认Constans.YGGJ_AMEI 2.
	 * 如果用户购买过管家卡，则为真人管家绑定的环信IM账号
	 */
	@Override
	public Map<String, String> getSeniorImUsername(Users user) {

		Map<String, String> map = new HashMap<String, String>();
		if (user == null) {
			return map;
		}
		// 先找出真人管家的admin_id
		Long userId = user.getId();

		UserRefSec userRefSec = userRefSecMapper.selectByUserId(userId);
		if (userRefSec == null) {
			return map;
		}

		Long adminId = userRefSec.getSecId();
		AdminAccount adminAccount = adminAccountService.selectByPrimaryKey(adminId);

		String seniorImUsername = adminAccount.getImUsername();
		String seniorImNickname = adminAccount.getNickname();
		map.put("seniorImUsername", seniorImUsername);
		map.put("seniorImNickname", seniorImNickname);
		return map;
	}

	/**
	 * 获得环信机器人账号 后续需要根据用户的性别来进行判断，目前都统一为女性.
	 */
	@Override
	public Map<String, String> getImRobot(Users user) {

		Map<String, String> map = new HashMap<String, String>();
		map.put("username", Constants.ROBOT_FEMALE_USERNAME);
		map.put("nickname", Constants.ROBOT_FEMALE_NICKNAME);
		return map;
	}

	@Override
	public List<Users> selectUsersHaveOrdered(List<String> mobiles) {
		return usersMapper.selectByMobiles(mobiles);
	}

	@Override
	public List<Users> selectUsersNoOrdered(List<String> mobiles) {
		return usersMapper.selectNotInMobiles(mobiles);
	}

	@Override
	public Users selectByOpenidAndThirdType(String openid, String thirdType) {
		Map<String, Object> conditions = new HashMap<String, Object>();
		if (openid != null && !openid.isEmpty()) {
			conditions.put("openid", openid);
		}
		if (thirdType != null && !thirdType.isEmpty()) {
			conditions.put("thirdType", thirdType);
		}
		return usersMapper.selectByOpenidAnd3rdType(conditions);
	}

	/**
	 * 第三方登录，注册绑定环信账号
	 */
	@Override
	public UserRef3rd genImUser(Users user) {
		UserRef3rd record = new UserRef3rd();
		Long userId = user.getId();
		UserRef3rd userRef3rd = userRef3rdMapper.selectByUserIdForIm(userId);
		if (userRef3rd != null) {
			return userRef3rd;
		}

		String uuid = "";
		// 如果不存在则新增.并且存入数据库
		String username = "simi-user-" + user.getId().toString();
		String defaultPassword = com.meijia.utils.huanxin.comm.Constants.DEFAULT_PASSWORD;

		// 1. 先去环信查找是否有用户:
		ObjectNode getIMUsersByPrimaryKeyNode = EasemobIMUsers.getIMUsersByPrimaryKey(username);

		JsonNode statusCode = getIMUsersByPrimaryKeyNode.get("statusCode");
		if (statusCode.toString().equals("404")) {
			ObjectNode datanode = JsonNodeFactory.instance.objectNode();
			datanode.put("username", username);
			datanode.put("password", defaultPassword);
			if (user.getName() != null && user.getName().length() > 0) {
				datanode.put("nickname", user.getName());
			}
			ObjectNode createNewIMUserSingleNode = EasemobIMUsers.createNewIMUserSingle(datanode);

			JsonNode entity = createNewIMUserSingleNode.get("entities");
			uuid = entity.get(0).get("uuid").toString();
		} else {
			JsonNode entity = getIMUsersByPrimaryKeyNode.get("entities");
			uuid = entity.get(0).get("uuid").toString();
		}

		// username = entity.get(0).get("username").toString();

		record.setId(0L);
		record.setUserId(userId);
		record.setRefType(Constants.IM_PROVIDE);
		record.setMobile(user.getMobile());
		record.setUsername(username);
		record.setPassword(defaultPassword);
		record.setRefPrimaryKey(uuid);
		record.setAddTime(TimeStampUtil.getNowSecond());
		userRef3rdMapper.insert(record);
		return record;
	}

	@Override
	public List<Users> selectByUserIds(List<Long> ids) {
		return usersMapper.selectByUserIds(ids);
	}

	@Override
	public List<Users> selectByUserType(Short userType) {
		return usersMapper.selectByUserType(userType);
	}

	@Override
	public Long insert(Users record) {

		return usersMapper.insert(record);
	}

	@Override
	public Users selectUserByIdCard(String idCard) {
		return usersMapper.selectUserByIdCard(idCard);
	}

	@Override
	public PageInfo selectByIsAppRoval(int pageNo, int pageSize) {

		PageHelper.startPage(pageNo, pageSize);
		List<Users> list = usersMapper.selectByIsAppRoval();
		PageInfo result = new PageInfo(list);
		return result;
	}

	@Override
	public PageInfo selectByIsAppRovalYes(int pageNo, int pageSize) {

		PageHelper.startPage(pageNo, pageSize);
		List<Users> list = usersMapper.selectByIsAppRovalYes();
		PageInfo result = new PageInfo(list);
		return result;
	}

	@Override
	public Users selectByPrimaryKey(Long id) {
		return usersMapper.selectByPrimaryKey(id);
	}

	@Override
	public Long insertSelective(Users u) {
		// TODO Auto-generated method stub
		return usersMapper.insertSelective(u);
	}

	@Override
	public Users selectByMobile(String mobile) {
		return usersMapper.selectByMobile(mobile);
	}

	@Override
	public List<Users> selectByListPage(UsersSearchVo usersSearchVo, int pageNo, int pageSize) {
		PageHelper.startPage(pageNo, pageSize);
		List<Users> lists = usersMapper.selectVoByListPage(usersSearchVo);
		return lists;
	}

	@Override
	public Users initUsers() {
		Users u = new Users();
		u.setId(0L);
		u.setMobile("");
		u.setProvinceName("");
		u.setThirdType(" ");
		u.setOpenid(" ");
		u.setName("");
		u.setRealName("");
		u.setBirthDay(new Date());
		u.setIdCard("");
		u.setDegreeId(0L);
		u.setMajor("");
		u.setSex(" ");
		u.setHeadImg(" ");
		u.setQrCode("");
		u.setIntroduction("");
		u.setLevel((short) 0);
		u.setWorkStart("");
		u.setWorkEnd("");
		u.setIsDoor((short) 0);
		u.setRestMoney(new BigDecimal(0));
		u.setUserType((short) 0);
		u.setIsApproval((short) 0);
		u.setAddFrom((short) 0);
		u.setScore(0);
		u.setAddTime(TimeStampUtil.getNow() / 1000);
		u.setUpdateTime(TimeStampUtil.getNow() / 1000);
		return u;
	}

	// 运营人员收到新秘书注册的短信提醒
	@Override
	public Boolean userOrderAmPushSms(Users users) {
		String name = users.getName();
		String nameMobile = users.getMobile();
		Long addTime = users.getAddTime();
		String addTimeStr = TimeStampUtil.timeStampToDateStr(addTime * 1000);

		// List<AdminAccount> adminAccounts = adminAccountService.selectByAll();
		// 查出所有运营部的人员（roleId=3）
		Long roleId = 3L;
		List<AdminAccount> adminAccounts = adminAccountService.selectByRoleId(roleId);
		List<String> mobileList = new ArrayList<String>();
		for (AdminAccount item : adminAccounts) {
			if (!StringUtil.isEmpty(item.getMobile())) {
				mobileList.add(item.getMobile());
			}
		}
		String[] content = new String[] { name, nameMobile, addTimeStr };
		for (int i = 0; i < mobileList.size(); i++) {

			HashMap<String, String> sendSmsResult = SmsUtil.SendSms(mobileList.get(i), Constants.SEC_REGISTER_ID, content);
			System.out.println(sendSmsResult + "00000000000000");
		}
		return true;
	}

	// 秘书审核通过后给助理发送短信提醒
	@Override
	public Boolean userSecToUserPushSms(Users users) {

		String name = users.getName();
		String mobile = users.getMobile();
		String url = "";

		String[] content = new String[] { name, url };
		HashMap<String, String> sendSmsResult = SmsUtil.SendSms(mobile, Constants.SEC_REGISTER_USER_ID, content);
		System.out.println(sendSmsResult + "00000000000000");

		return true;
	}

	@Override
	public List<Users> selectByListPageYes(UsersSearchVo usersSearchVo, int pageNo, int pageSize) {
		PageHelper.startPage(pageNo, pageSize);
		List<Users> lists = usersMapper.selectVoByListPageYes(usersSearchVo);
		return lists;
	}

	/**
	 * 获得用户头像的方法
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getHeadImg(Users u) {
		String headImg = "";

		if (!StringUtil.isEmpty(u.getHeadImg().trim()))
			return u.getHeadImg();

		if (StringUtil.isEmpty(u.getName().trim()))
			return Constants.DEFAULT_HEAD_IMG;

		String name = u.getName();
		name = name.replace(" ", "");
		String headImgWord = "";
		if (StringUtil.isContainChinese(name)) {
			if (name.length() > 2) {
				headImgWord = name.substring(name.length() - 2, name.length());
			} else {
				headImgWord = name;
			}
		} else {
			if (name.length() > 2) {
				headImgWord = name.substring(0, 2);
			} else {
				headImgWord = name;
			}
		}

		if (StringUtil.isEmpty(headImgWord))
			return Constants.DEFAULT_HEAD_IMG;

		byte[] imgUrlBytes = null;
		if (StringUtil.isContainChinese(name)) {
			imgUrlBytes = ImgServerUtil.ImgYin(headImgWord, Constants.DEFAULT_HEAD_IMG_BACK, -82, 22);
		} else {
			imgUrlBytes = ImgServerUtil.ImgYin(headImgWord, Constants.DEFAULT_HEAD_IMG_BACK, -40, 20);
		}

		if (imgUrlBytes == null)
			return Constants.DEFAULT_HEAD_IMG;
		String url = Constants.IMG_SERVER_HOST + "/upload/";

		String sendResult = ImgServerUtil.sendPostBytes(url, imgUrlBytes, "jpg");

		ObjectMapper mapper = new ObjectMapper();

		HashMap<String, Object> o = null;
		try {
			o = mapper.readValue(sendResult, HashMap.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(o.toString());
		String ret = o.get("ret").toString();

		HashMap<String, String> info = (HashMap<String, String>) o.get("info");

		String imgUrl = Constants.IMG_SERVER_HOST + "/" + info.get("md5").toString();
		imgUrl = ImgServerUtil.getImgSize(imgUrl, "100", "100");
		u.setHeadImg(imgUrl);
		headImg = imgUrl;
		updateByPrimaryKeySelective(u);

		return headImg;
	}

}
