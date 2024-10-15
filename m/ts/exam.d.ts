// 考试接口
export interface Exam {
	id: number | null; // ID
	name: string; // 考试名称
	startTime: string; // 考试开始时间（yyyy-MM-dd HH:mm:ss）
	endTime: string; // 考试结束时间（yyyy-MM-dd HH:mm:ss）
	markStartTime: string; //阅卷开始时间（yyyy-MM-dd HH:mm:ss）
	markEndTime: string; //阅卷结束时间（yyyy-MM-dd HH:mm:ss）
	markState: number | null; //阅卷状态（1：未阅卷；2：阅卷中；3：已阅卷；）
	scoreState: number | null; //成绩查询状态（1：考试结束后；2：不公布；3：交卷后）
	rankState: number | null; //排名状态（1：公布；2：不公布）
	anonState: number | null; //匿名阅卷状态（1：是；2：否）
	passScore: number | null; //及格分数
	totalScore: number | null; //总分数
	markType: number | null; //阅卷方式（1：客观题；2：主观题；）
	genType: number | null; //组卷方式（1：人工组卷；2：随机组卷）
	loginType: number | null; //登录方式（1：正常登录；2：免登录；）
	sxes: number[]; //反作弊（1：试题乱序；2：选项乱序；）
	state: number | null; //状态（0：删除；1：发布；2：暂停；）
	userNum: number | null; //考试用户数量
	limitMinute: number | null; //限制分钟（考试开始时间由用户第一次打开试卷时计时）
}
