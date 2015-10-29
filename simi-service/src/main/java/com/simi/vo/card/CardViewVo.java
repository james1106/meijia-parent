package com.simi.vo.card;

import java.util.List;

import com.simi.po.model.card.CardAttend;
import com.simi.po.model.card.Cards;

public class CardViewVo extends Cards {

	private List<CardAttend> attends;
	
	private String createUserName;
	
	private String userName;
	
	private String userHeadImg;
	
	private int totalZan;
	
	private int totalComment;
	
	private List<CardZanViewVo> zanTop10;
	
	private String cardTypeName;
		
	private String addTimeStr;
	
	private String ticketFromCityName;
	
	private String ticketToCityName;
	
	public List<CardAttend> getAttends() {
		return attends;
	}

	public void setAttends(List<CardAttend> attends) {
		this.attends = attends;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public int getTotalZan() {
		return totalZan;
	}

	public void setTotalZan(int totalZan) {
		this.totalZan = totalZan;
	}

	public int getTotalComment() {
		return totalComment;
	}

	public void setTotalComment(int totalComment) {
		this.totalComment = totalComment;
	}

	public List<CardZanViewVo> getZanTop10() {
		return zanTop10;
	}

	public void setZanTop10(List<CardZanViewVo> zanTop10) {
		this.zanTop10 = zanTop10;
	}

	public String getCardTypeName() {
		return cardTypeName;
	}

	public void setCardTypeName(String cardTypeName) {
		this.cardTypeName = cardTypeName;
	}

	public String getAddTimeStr() {
		return addTimeStr;
	}

	public void setAddTimeStr(String addTimeStr) {
		this.addTimeStr = addTimeStr;
	}

	public String getTicketFromCityName() {
		return ticketFromCityName;
	}

	public void setTicketFromCityName(String ticketFromCityName) {
		this.ticketFromCityName = ticketFromCityName;
	}

	public String getTicketToCityName() {
		return ticketToCityName;
	}

	public void setTicketToCityName(String ticketToCityName) {
		this.ticketToCityName = ticketToCityName;
	}

	public String getUserHeadImg() {
		return userHeadImg;
	}

	public void setUserHeadImg(String userHeadImg) {
		this.userHeadImg = userHeadImg;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
