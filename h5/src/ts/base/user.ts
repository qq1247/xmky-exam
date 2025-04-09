export interface User {
    id: number | null// ID
    orgId?: number // 机构ID（子管理员、阅卷用户没有）
    name: string // 名称
    loginName: string // 登录账号
    type: number | null // 类型（0：管理员；1：考试用户；2：子管理员；3：阅卷用户）
    state: number | null// 状态（1：正常；2：冻结）
    userIds?: number[] // 可管理用户IDS（子管理员有效）
    orgIds?: number[] // 可管理用户IDS（子管理员有效）
    [key: string]: any;// 扩展字段

}