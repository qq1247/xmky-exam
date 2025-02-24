// 定时任务接口
export interface Cron {
    id: number | null// ID
    name: string // 名称
    jobClass: string // 实现类
    cron: string // cron表达式
    state: number // 状态（1：启动；2：停止；）
}