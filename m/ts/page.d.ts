// 分页接口
export interface Page<T> {
	curPage: number;// 当前第几页
	pageSize: number;// 每页长度
	total: number;// 总条数
	list: T[];// 列表数据
	[temp: string]: any;// 扩展字段
}
