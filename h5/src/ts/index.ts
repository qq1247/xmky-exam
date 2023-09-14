// 用户
export interface User {
    id: number // 主键
    name: string // 姓名
    loginName: string // 登录账号
    pwd: string // 密码
    type: number // 类型（0：管理员；1：考试用户；2：子管理员；3：阅卷用户）
    orgId: number // 机构ID
    userIds: number[]// 可管理用户IDS（子管理员有效）
    orgIds: number[]// 可管理机构IDS（子管理员有效）
}

// 机构
export interface Org {
    id: number // 主键
    name: string // 名称
    parentId: number // 父机构ID
    parentName: string // 父机构名称
}