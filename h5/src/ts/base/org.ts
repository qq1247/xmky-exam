export interface Org {
    id: number | null// ID
    name: string // 名称
    parentId: number | null// 父机构ID
    parentName: string // 父机构名称
    no?: number// 排序
}