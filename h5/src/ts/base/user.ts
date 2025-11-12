export interface User {
    id: number | null// ID
    orgId?: number // 机构ID（子管理员、阅卷用户没有）
    name: string // 名称
    loginName: string // 登录账号
    role: string // 角色（ADMIN：管理员；EXAM_USER：考试用户；SUB_ADMIN：子管理员；MARK_USER：阅卷用户；TEMP_USER：临时用户）
    state: number | null// 状态（1：正常；2：冻结）
    userIds?: number[] // 可管理用户IDS（子管理员有效）
    orgIds?: number[] // 可管理用户IDS（子管理员有效）
    [key: string]: any;// 扩展字段

}