<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="../shared/taglib.jsp"%>

<html>
<head>

<!--common css for all pages-->
<%@ include file="../shared/importCss.jsp"%>

<!--css for this page-->

<link href="<c:url value='/assets/js/zTree/css/awesomeStyle/awesome.css'/>" rel="stylesheet">
<link href="<c:url value='/assets/js/amazeui.datatables/amazeui.datatables.min.css'/>"
	rel="stylesheet">


</head>

<body>
	<!--header start-->
	<%@ include file="../shared/pageHeader.jsp"%>
	<!--header end-->

	<div class="am-cf admin-main">
		<!-- sidebar start -->
		<div class="admin-sidebar am-offcanvas" id="admin-offcanvas">
			<div class="am-cf am-padding">
				<div class="am-fl am-cf">
					<strong class="am-text-primary am-text-lg">部门列表</strong>
				</div>
			</div>
			<hr />
			<ul class="am-list admin-sidebar-list">
				<li><%@ include file="../shared/dept-tree.jsp"%></li>
			</ul>


		</div>
		<!-- sidebar end -->

		<!-- content start -->
		<div class="admin-content">
			<div class="am-cf am-padding">
				<div class="am-fl am-cf">
					<strong class="am-text-primary am-text-lg">通讯录</strong>
				</div>
			</div>

			<hr />

			<div class="am-u-md-11">
				<div class="doc-example">
					<div id="sorting-chinese_wrapper"
						class="dataTables_wrapper am-datatable am-form-inline dt-amazeui no-footer">
						<div class="am-g am-datatable-hd">
							<div class="am-u-sm-6">
								<div class="dataTables_length am-form-group am-datatable-length" id="sorting-chinese_length">
									<label>显示 <select name="sorting-chinese_length" aria-controls="sorting-chinese"
										class="am-form-select am-input-sm"><option value="10">10</option>
											<option value="25">25</option>
											<option value="50">50</option>
											<option value="100">100</option></select> 项结果
									</label>
								</div>
							</div>
							<div class="am-u-sm-6">
								<div id="sorting-chinese_filter" class="dataTables_filter am-datatable-filter">
									<label>搜索：<input type="search" class="am-form-field am-input-sm"
										aria-controls="sorting-chinese"></label>
								</div>
							</div>

						</div>
						<div class="am-g">
							<div class="am-u-sm-12">
								<table id="list-table"
									class="am-table am-table-bordered am-table-striped dataTable no-footer" role="grid"
									aria-describedby="sorting-chinese_info">
									<thead>
										<tr>
											<th>工号</th>
											<th>姓名</th>
											<th>性别</th>
											<th>手机</th>
											<th>出生年月</th>
											<th>户口所在地</th>
										</tr>
									</thead>

								</table>
							</div>
						</div>

					</div>
				</div>
			</div>

		</div>
	</div>
	</div>
	<!-- content end -->

	</div>

	<!--footer start-->
	<%@ include file="../shared/pageFooter.jsp"%>
	<!--footer end-->

	<!-- js placed at the end of the document so the pages load faster -->
	<!--common script for all pages-->
	<%@ include file="../shared/importJs.jsp"%>

	<!--script for this page-->
	<script src="<c:url value='/assets/js/zTree/js/jquery.ztree.core-3.5.js'/>"></script>
	<script src="<c:url value='/assets/js/zTree/js/jquery.ztree.excheck-3.5.js'/>"></script>
	<script src="<c:url value='/assets/js/zTree/js/jquery.ztree.exedit-3.5.js'/>"></script>
	<script src="<c:url value='/assets/js/xcloud/common/dept-tree.js'/>"></script>
	<script src="<c:url value='/assets/js/xcloud/staffs/staff-list.js'/>"></script>
	<script src="<c:url value='/assets/js/amazeui.datatables/amazeui.datatables.min.js'/>"></script>
	<script src="<c:url value='/assets/js/amazeui.datatables/dataTables.responsive.min.js'/>"></script>


	<%-- <script  src="<c:url value='/assets/bootstrap-datepicker/js/bootstrap-datepicker.min.js'/>"></script>
	    <script  src="<c:url value='/assets/bootstrap-datepicker/locales/bootstrap-datepicker.zh-CN.min.js'/>"></script> --%>
	<%-- <script src="<c:url value='/assets/jquery-validation/dist/jquery.validate.min.js'/>" ></script>
	    <script src="<c:url value='/js/vendor/jquery.dataTables.min.js'/>" ></script> --%>
</body>
</html>
