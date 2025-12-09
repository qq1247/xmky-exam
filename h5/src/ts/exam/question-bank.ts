export interface QuestionBank {
    id?: number | null // ID
    name: string // 名称
    shareAuth: number | null // 共享权限（0：私有；1：只读；2：读写；）
}
