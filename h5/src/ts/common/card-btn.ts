// 卡片按钮接口
export interface CardBtn {
    name: string // 名称
    icon: string // 图标
    event: () => void // 点击后回调方法
}