// 练习接口
export interface Exer {
    id: number | null; // ID
    name: string; // 名称
    questionBankId: number | undefined; // 题库ID（下拉选需要undefined）
    userIds: number[]; // 用户IDS
    orgIds: number[]; // 机构IDS
    state: number | null; // 状态（0：删除；1：发布；2：暂停）
    rmkState: number | null; // 允许评论（1：是；2：否）
}
