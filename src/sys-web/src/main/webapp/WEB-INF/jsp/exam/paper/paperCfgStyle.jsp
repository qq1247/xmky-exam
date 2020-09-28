<%@ page language="java" pageEncoding="utf-8"%>
<style>
.exam-card {
	position: fixed;
	right: 50px;
	top: 65px;
	width: 300px;
	font-family: "Microsoft YaHei";
}

.exam-card .select {
	background: #1e9fff;
	color: #fff;
}

.exam-card .layui-colla-title:HOVER {
	background: #1E9FFF;
	color: #fff;
}

.exam-card a {
	width: 30px;
	height: 23px;
	border: 1px solid #eceaea;
	color: #888;
	text-align: center;
	line-height: 23px;
	margin-left: 1px;
	display: inline-block;
	margin-top: 5px;
	border-radius: 3px;
}

.exam-card a:hover {
	color: #1e9fff;
	border: 1px solid #1e9fff;
}

.exam-card .layui-colla-title span {
	float: right;
	padding: 0px 3px;
	color: #1e9fff;
}

.exam-card .exam-head span {
	float: right;
	padding: 0px 3px;
	color: #fff;
}

.exam-card .layui-colla-title:HOVER span {
	color: #fff;
}

.exam-card .layui-colla-title {
	height: 36px;
	line-height: 36px;
}

.exam-card .exam-head {
	background: #1E9FFF;
	color: #fff;
	height: 40px;
	line-height: 40px;
	font-size: 18px;
	text-align: center;
	padding: 0px 18px;
	cursor: pointer;
}

.exam-qst-op {
	margin: 5px 0px;
	padding-left: 20px;
	border: 1px solid #e6e6e6;
	line-height: 45px;
	height: 45px;
	cursor: pointer;
}

.exam {
	margin-bottom: 10px;
}

.exam ul {
	padding-left: 30px;
}

.exam textarea {
	margin-left: 30px;
	width: calc(100% - 30px);
}

.exam .btn {
	display: inline-block;
	width: 190px;
}

.exam h1 {
	background-color: #fff !important;
	padding-left: 15px !important;
	font-size: 26px !important;
	text-align: center;
}

.exam h2 {
	background-color: #fff !important;
	padding-left: 15px !important;
	font-size: 22px !important;
	width: calc(100% - 90px);
	display: inline-block;
}

.exam h5 {
	background-color: #fff !important;
	padding-left: 15px !important;
	height: auto;
	line-height: 30px;
}

.exam .qst-no {
	width: 30px;
	display: inline-block;
	vertical-align: top;
}

.exam .span {
	padding: 5px 0px 0px 30px;
	display: block;
}

.exam .span .layui-form-switch {
	margin-top: 0px;
}

.exam .qst-title {
	width: calc(100% - 34px);
	display: inline-block;
}

.exam .exam-qst-op:hover {
	color: #5FB878;
	border: 1px solid #5FB878;
}

.exma .exam-qst-op:hover i {
	color: #5FB878;
}

.exam .exam-qst-op-checkbox:hover i {
	color: #FFF;
	background-color: #5FB878;
}

.exam .exam-qst-add {
	color: #1E9FFF;
	display: inline-block;
	width: 20px;
	height: 42px;
	line-height: 42px;
	font-size: 22px;
	cursor: pointer;
	vertical-align: top;
}

.exam .txt:HOVER {
	border-color: #5FB878 !important;
}

.exam .btn2 {
	font-weight: bold;
	display: inline-block;
	width: 22px;
	height: 22px;
	color: #1e9fff;
	cursor: pointer;
	line-height: 22px;
	text-align: center;
	font-size: 18px;
}

.exam .btn3 {
	width: 50px;
	padding: 0px 10px;
	text-align: center;
	border-width: 0px 0px 1px 0px;
	height: 18px;
}
</style>
