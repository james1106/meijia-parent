<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="../shared/taglib.jsp"%>

<html>
<head>

<!--common css for all pages-->
<%@ include file="../shared/importCss.jsp"%>
<link href="<c:url value='/assets/css/xcloud.css'/>" rel="stylesheet" />
</head>

<body >
	<!--header start-->
	<%@ include file="../shared/pageSampleHeader.jsp"%>
	<!--header end-->

	<div class="am-g tr-yunpan">
		<div class="tr-yunpan-bg-container am-container am-container-collapse">
			<div data-am-widget="slider"
				class="tr-yunpan-bg-slider am-hide-sm-only am-slider am-slider-a1 am-no-layout"
				data-am-slider="{animation:'slide',directionNav:false,pauseOnHover: false}">

				<div style="overflow: hidden; position: relative;" class="am-viewport">
					<ul style="width: 800%; transition-duration: 0s; transform: translate3d(-1000px, 0px, 0px);"
						class="am-slides">
						<li style="width: 1000px; float: left; display: block;" aria-hidden="true" class="clone"><img
							draggable="false" src="<c:url value='/assets/img/netdisk-slider-p2.png'/>"></li>
						<li class="am-active-slide" style="width: 1000px; float: left; display: block;"><img
							draggable="false" src="<c:url value='/assets/img/netdisk-slider-p1.png'/>"></li>
						<li class="" style="width: 1000px; float: left; display: block;"><img draggable="false"
							src="<c:url value='/assets/img/netdisk-slider-p2.png'/>"></li>
						<li aria-hidden="true" class="clone" style="width: 1000px; float: left; display: block;"><img
							draggable="false" src="<c:url value='/assets/img/netdisk-slider-p1.png'/>"></li>
					</ul>
				</div>
				<ol class="am-control-nav am-control-paging">
					<li><a class="am-active">1</a><i></i></li>
					<li><a class="">2</a><i></i></li>
				</ol>
			</div>
		</div>
		<div class="tr-yunpan-container am-container am-container-collapse am-vertical-align">
			<div class="am-vertical-align-middle am-g am-g-collapse" style="width: 99%;">

				<div
					class="tr-yunpan-register-container am-container am-u-md-8 am-u-md-push-2 am-u-sm-12">
					<form:form method="post" modelAttribute="contentModel" id="company-reg-form"
						class="am-form am-container am-padding-xl am-padding-bottom am-animation-reverse" >
						<form:hidden path="companyId" />
						<div class="am-g am-container">
							<h2 align="center"><strong>注册您的账号</strong></h2><i class="am-icon am-icon-info-circle"></i> 若您用手机号在菠萝HR的APP注册过，可直接用手机短信验证码方式登录
							<form:errors path="userName" class="am-alert am-alert-danger center"></form:errors>
						</div>
						<div class="am-g am-padding-sm">

							<div class="am-form-group am-input-group">
								<span class="am-input-group-label"><i class="am-icon am-icon-phone"></i></span>
								<form:input path="userName" class="am-form-field js-pattern-mobile" autocomplete="off"
									data-validation-message="请填写正确的手机号" placeholder="手机号" required="required" />
							</div>
							
							<div class="am-form-group am-input-group">
								<span class="am-input-group-label"><i class="am-icon am-icon-suitcase"></i></span> 
									<input
									type="text" id="sms_token" name="sms_token" class="am-form-field js-pattern-sms_token js-ajax-validate"
									placeholder="短信验证码" required="required" data-validation-message="验证失败" /> 
									<span class="am-input-group-btn"> 
										<button id="btn_sms_token" class="am-btn am-btn-warning" type="button">获取验证码</button>
									</span>
							</div>
							
							<div class="am-form-group am-input-group">
								<span class="am-input-group-label"><i class="am-icon am-icon-lock"></i></span>
								<form:password path="password" class="am-form-field am-radius" placeholder="密码" required="required"
									minlength="6" data-validation-message="密码必须为6以上的字符或数字" />
								<span class="am-input-group-label"><i class="am-icon am-icon-lock"></i></span> <input
									id="confirmPassword" class="am-form-field am-radius" placeholder="确认密码" required="required"
									data-equal-to="#password" data-validation-message="请确认前后输入密码一致" type="password">
							</div>
							<!--
							<div class="am-form-group am-input-group">
								<span class="am-input-group-label"><i class="am-icon am-icon-lock"></i></span> <input
									id="confirmPassword" class="am-form-field am-radius" placeholder="确认密码" required="required"
									data-equal-to="#password" data-validation-message="请确认前后输入密码一致" type="password">
							</div>
							-->
							<div class="am-form-group am-input-group">
								<span class="am-input-group-label"><i class="am-icon am-icon-male"></i></span> 

								<form:input path="companyName" class="am-form-field am-radius" placeholder="输入公司全称,若在APP创建过企业,可直接短信验证码登录" maxLength="64" required="required" />	
							</div>

							<div class="am-form-group am-input-group">
								<span class="am-input-group-label"><i class="am-icon am-icon-male"></i></span> 

								<form:input path="shortName" class="am-form-field am-radius"  placeholder="输入公司简称,若在APP创建过企业,可直接短信验证码登录" maxLength="32" required="required" />	
							</div>
							
							<button type="button"  class="am-btn am-btn-success am-btn-block am-radius"
								id="reg-submit">注 册</button>
							<a href="/xcloud">
								<h4 align="center">已有账号？ 立即登录</h4>
							</a>
						</div>
					</form:form>
				</div>

			</div>
		</div>
	</div>
	
	<!--footer start-->
	<%@ include file="../shared/pageFooter.jsp"%>
	<!--footer end-->

	<!-- js placed at the end of the document so the pages load faster -->
	<!--common script for all pages-->
	<%@ include file="../shared/importJs.jsp"%>

	<!--script for this page-->

	<script src="<c:url value='/assets/js/countdown.js'/>" type="text/javascript"></script>
	<script src="<c:url value='/assets/js/xcloud/common/validate-methods.js'/>" type="text/javascript"></script>
	<script src="<c:url value='/assets/js/xcloud/home/register.js'/>" type="text/javascript"></script>
</body>
</html>
