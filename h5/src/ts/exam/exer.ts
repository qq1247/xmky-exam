// 练习接口
export interface Exer {
    id: number | null; // ID
    name: string; // 名称
    questionBankIds: number[]; // 题库ID
    userIds: number[]; // 用户IDS
    orgIds: number[]; // 机构IDS
    state: number | null; // 状态（0：删除；1：发布；2：暂停）
}
