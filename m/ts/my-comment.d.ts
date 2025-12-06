// 我的评论接口
export interface MyCommon {
    id: number | null; // 主键
    userId: number | null; // 用户ID
    userName: string; // 用户姓名
    content: string; // 内容
    updateTime: string; // 更新时间
    likeNum: number | null; // 点赞数量
    isLike: boolean; // 是否点赞
    replyUserId: number | null; // 回复用户ID，二级评论有效
    replyUserName: string; // 回复用户姓名，二级评论有效
    children: MyCommon[]
}
