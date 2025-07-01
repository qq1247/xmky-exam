// 我的练习试题接口
export interface MyExerQuestion {
    questionId: number | null; // 试题ID
    no: number | null; // 排序
    shuffleNo: number | null; // 随机排序
    score: number | null; // 分数
    userScore: number | null; // 用户得分
    fav: number | null; // 收藏（1：是；2：否）
    wrongAnswerNum: number | null; // 答错次数
}
