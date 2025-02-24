// 练习接口
export interface Exer {
    id: number | null; // ID
    name: string; // 名称
    startTime: string; // 开始时间
    endTime: string; // 结束时间
    questionBankId: number | undefined; // 题库ID（下拉选需要undefined）
    userIds: number[]; // 考试用户IDS
    rmkState: number | null; // 允许评论（1：是；2：否）
}
