package com.simi.po.dao.partners;

import java.util.List;

import com.simi.po.model.partners.PartnerRefRegion;

public interface PartnerRefRegionMapper {
    int insert(PartnerRefRegion record);

    int insertSelective(PartnerRefRegion record);
    
    List<PartnerRefRegion> selectByPartnerId(Long partnerId);
    
    int deleteByPartnerId(Long partnerId);
}