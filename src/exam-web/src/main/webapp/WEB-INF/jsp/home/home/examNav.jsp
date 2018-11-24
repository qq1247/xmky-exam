<%@ page language="java" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
	<head>
		<title>考试导航</title>
		<%@include file="/script/home/common.jspf"%>
	</head>
	<body class="bg-white" style="height: 100%;">
		<div class="container">
			<div class="row">
				<div class="col-md-12">
					<ul class="process process-plus">
						<li onclick="alert(1)">
							<div class="item curr-pre">
								<div class="step">
									<i>1</i>
									<label>添加试题</label>
								</div>
								<span></span>
							</div>
						</li>
						<li>
							<div class="item current">
								<div class="step">
									<i>2</i>
									<label>添加试卷</label>
								</div>
								<span></span>
							</div>
						</li>
						<li>
							<div class="item last">
								<div class="step">
									<i>3</i>
									<label>添加考试</label>
								</div>
								<span></span>
							</div>
						</li>
					</ul>
				</div>
			</div>
			<div class="row">
				<div class="col-md-12" id="qq">
				</div>
			</div>
		</div>
	</body>
</html>