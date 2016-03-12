package com.simi.service.impl.op;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.simi.service.op.AppToolsService;
import com.simi.service.op.OpChannelService;
import com.simi.vo.po.AppToolsVo;
import com.simi.po.dao.op.AppToolsMapper;
import com.simi.po.dao.op.UserAppToolsMapper;
import com.simi.po.model.op.AppTools;
import com.simi.po.model.op.UserAppTools;
import com.meijia.utils.BeanUtilsExp;
import com.meijia.utils.TimeStampUtil;

@Service
public class AppToolsServiceImpl implements AppToolsService {

	@Autowired
	private AppToolsMapper appToolsMapper;
	
	@Autowired
	private UserAppToolsMapper userAppToolsMapper;
	
	@Autowired
	private OpChannelService opChannelService;		


	@Override
	public AppTools initAppTools() {

		    AppTools record = new AppTools();
		    
		    record.settId(0L);
		    record.setNo((short)0L);
		    record.setName("");
		    record.setLogo("");
		    record.setAppType("");
		    record.setMenuType("");
		    record.setOpenType("");
		    record.setUrl("");
		    record.setAction("");
		    record.setParams("");
		    record.setIsDefault((short)0L);
		    record.setIsDel((short)0L);
		    record.setIsPartner((short)0L);
		    record.setIsOnline((short)0);
		    record.setAppProvider("");
		    record.setAppDescribe("");
		    record.setAuthUrl("");
		    record.setAddTime(TimeStampUtil.getNow()/1000);
			return record;
		}


	@Override
	public AppTools selectByPrimaryKey(Long tId) {
		
		return appToolsMapper.selectByPrimaryKey(tId);
	}


	@Override
	public int updateByPrimaryKeySelective(AppTools record) {
		
		return appToolsMapper.updateByPrimaryKeySelective(record);
	}


	@Override
	public int insertSelective(AppTools record) {
		
		return appToolsMapper.insertSelective(record);
	}


	@Override
	public int deleteByPrimaryKey(Long tId) {

		return appToolsMapper.deleteByPrimaryKey(tId);
	}


	@SuppressWarnings("rawtypes")
	@Override
	public PageInfo selectByListPage(int pageNo, int pageSize) {

		PageHelper.startPage(pageNo, pageSize);
		
		List<AppTools> list = appToolsMapper.selectByListPage();
		
		PageInfo result = new PageInfo(list);
		
		return result;
	}


	@Override
	public List<AppTools> selectByAppType(String appType) {
		
		return appToolsMapper.selectByAppType(appType);
	}
	
	@Override
	public AppTools selectByAction(String action) {
		
		return appToolsMapper.selectByAction(action);
	}

	@Override
	public List<AppTools> selectByAppTypeAndStatus(String appType) {
		
		return appToolsMapper.selectByAppTypeAndStatus(appType);
	}

	@Override
	public AppToolsVo getAppToolsVo(AppTools item,Long userId) {
		
		AppToolsVo vo = new AppToolsVo();
		BeanUtilsExp.copyPropertiesIgnoreNull(item, vo);
		
		
		//添加时间返回‘yyyy-mm-dd’
		Long addTime = item.getAddTime()*1000;
		vo.setAddTimeStr(TimeStampUtil.timeStampToDateStr(addTime, "yyyy-MM-dd"));
		
		//应用状态
		UserAppTools userAppTools = userAppToolsMapper.selectByUserIdAndTid(userId,item.gettId());
		if (userAppTools != null) {
		vo.setStatus(userAppTools.getStatus());	
		}
		vo.setUserId(userId);
		return vo;
	}


	@Override
	public List<AppTools> selectByAppTypeAll(String appType) {

		return appToolsMapper.selectByAppTypeAll(appType);
	}


	@Override
	public PageInfo selectByListPage(String appType, int pageNo, int pageSize,Long userId) {
		
		PageHelper.startPage(pageNo, pageSize);
		
		//List<AppToolsVo> appToolsVoList = new ArrayList<AppToolsVo>();
		List<AppTools> list = appToolsMapper.selectByAppTypeAll(appType);
		if (!list.isEmpty()) {
			/*for (AppTools item : list) {
				AppToolsVo vo = getAppToolsVo(item, userId);
				appToolsVoList.add(vo);
			}*/
			for (int i = 0; i < list.size(); i++) {
				AppTools appTools = list.get(i);
				AppToolsVo vo = getAppToolsVo(appTools, userId);
				/*if (vo.getStatus() == null){
					vo.setStatus((short)0);
				}*/
				list.set(i, vo);
			}
		}
		
		PageInfo result = new PageInfo(list);
		return result;
	}
	
}
