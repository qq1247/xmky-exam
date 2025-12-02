export interface RegistUser {
    id: number | null// ID
    name: string // 姓名
    loginName: string // 登录账号
    orgId: number | null // 机构ID
    registTime: string // 注册时间
    state: number | null// 状态（1：通过；2：拒绝；3：待审核）
    remark: string // 审批意见
}