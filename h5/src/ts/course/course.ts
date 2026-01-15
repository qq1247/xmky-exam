// 课程接口
export interface Course {
    id: number | null; // ID
    name: string; // 名称
    content: string; // 内容
    orgIds: number[]; // 机构IDS
    userIds: number[]; // 用户IDS
    shareAuth: number | null // 共享权限（0：私有；1：只读；2：读写；）
    state: number | null; // 状态（0：删除；1：发布；2：暂停）
}
