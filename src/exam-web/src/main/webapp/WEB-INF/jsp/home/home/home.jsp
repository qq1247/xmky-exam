<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>在线考试系统首页</title>
		<%@include file="/script/home/common.jspf"%>
	</head>
	<body class="bg-white">
		<%@include file="/script/home/head.jspf"%>
		<div class="container">
			<div class="row home-nav">
				<div class="col-md-12">
					<span>你好，${USER.name }</span>
				</div>
			</div>
			<div class="row home-nav2">
				<div class="col-md-8">
					<div class="row">
						<div class="col-md-6 stats">
							<span>待考试卷</span>
						</div>
						<div class="col-md-6 stats">
							<span>待判试卷</span>
						</div>
					</div>
					<div class="row">
						<div class="col-md-6 detail">
							<ul>
								<c:forEach var="myExam" items="${myExamList }">
								<li>
									<span class="head">${myExam.NAME }</span>
									<div class="mid"></div>
									<div class="tail"><a href="home/myExam/toPaper?examId=${myExam.ID }">开始</a></div>
								</li>
								</c:forEach>
							</ul>
						</div>
						<div class="col-md-6 detail">
							<ul>
								<c:forEach var="myMark" items="${myMarkList }">
								<li>
									<span class="head">${myMark.NAME }</span>
									<div class="mid"></div>
									<div class="tail"><a href="home/myMark/toList?one=${myMark.ID }">开始</a></div>
								</li>
								</c:forEach>
							</ul>
						</div>
					</div>
				</div>
				<div class="col-md-4 home-nav3">
					<p>快捷操作：</p>
					<a href="home/question/toList?nav=true">
						<i class="exam-nav"></i>
						<span>考试导航</span>
					</a>
				</div>
			</div>
		</div>
	</body>
	<script type="text/javascript">
		
	</script>
</html>