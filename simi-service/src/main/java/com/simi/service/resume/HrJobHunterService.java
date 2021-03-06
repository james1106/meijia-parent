package com.simi.service.resume;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.simi.po.model.resume.HrJobHunter;
import com.simi.vo.resume.JobHunterVo;
import com.simi.vo.resume.ResumeChangeSearchVo;

/**
 *
 * @author :hulj
 * @Date : 2016年4月28日下午4:31:28
 * @Description: 
 */
public interface HrJobHunterService {
	
	int deleteByPrimaryKey(Long id);

    int insert(HrJobHunter record);

    HrJobHunter selectByPrimaryKey(Long id);

    int updateByPrimaryKey(HrJobHunter record);
    
    List<HrJobHunter> selectBySearchVo(ResumeChangeSearchVo searchVo);

    PageInfo selectByListPage(ResumeChangeSearchVo searchVo, int pageNo, int pageSize);
	
    HrJobHunter initHrJobHunter();
     
    JobHunterVo initHunterVo();
    
    JobHunterVo  transToHunterVo(HrJobHunter hunter);
}
