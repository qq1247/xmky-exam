// 公告接口
export interface Bulletin {
    id: number | null// ID
    title: string // 标题
    content: string // 内容
    times: Date[] // 时间范围
}