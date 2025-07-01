// 我的练习接口
export interface MyExer {
    id: number | null; // 主键
    exerId: number | null; // 练习ID
    userId: number | null; // 用户ID
    completedNum: number | null; // 已练题数
    questionNum: number | null; // 试题数量
}
