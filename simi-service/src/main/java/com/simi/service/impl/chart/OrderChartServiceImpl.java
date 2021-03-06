package com.simi.service.impl.chart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.simi.po.dao.order.OrderPricesMapper;
import com.simi.po.dao.order.OrdersMapper;
import com.simi.service.chart.OrderChartService;
import com.simi.vo.chart.ChartSearchVo;

/**
 *
 * @author :hulj
 * @Date : 2015年8月31日上午10:38:25
 * @Description: 
 *		运营平台--
 *				市场统计图表
 */
@Service
public class OrderChartServiceImpl implements OrderChartService {

	@Autowired
	private OrdersMapper orderMapper;
	
	@Autowired
	private OrderPricesMapper orderPriceMapper;

	
	/**
	 * 订单数求和
	 * 
	 */
	@Override
	public int statTotalOrder(ChartSearchVo chartSearchVo) {	
		int totalOrder = 0;
		
		totalOrder = orderMapper.statTotalOrder(chartSearchVo);
		return totalOrder;
	}
	
	
	

	
	
}
