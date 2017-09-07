<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="my" uri="myTag/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>在线考试系统</title>
		<%@include file="/script/myJs/commonHome.jspf"%>
	</head>
	<body class="homepage" style="background-color: #f2f2f2">
		<%@include file="/script/myJs/commonHomeHead.jspf"%>
		<section class="service-item" style="padding-top: 100px;">
			<div class="container">
				<div class="center wow fadeInDown">
					<h2>当前考试</h2>
					<p class="lead"></p>
					<div class="row">
						<c:forEach var="unFinishExam" items="${unFinishExamList }">
							<div class="col-sm-6 col-md-4">
								<div class="media services-wrap wow fadeInDown">
									<div class="pull-left">
										<img class="img-responsive" src="script/home/images/services/services2.png">
									</div>
									<div class="media-body">
										<h3 class="media-heading">
											<a href="home/myExam/toPaper?examUserId=${unFinishExam.EXAM_USER_ID }">${unFinishExam.EXAM_NAME }</a>
										</h3>
										<p>${unFinishExam.EXAM_DESCRIPTION }</p>
									</div>
								</div>
							</div>
						</c:forEach>
					</div>
				</div>
			</div>
		</section>
		<%@include file="/script/myJs/commonHomeFooter.jspf"%>
	</body>
</html>