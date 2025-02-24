import type { Question } from "@/ts/exam/question";

export const hasSubjective = (question: Question): boolean => {
    return (question.markType == 2 && (question.type == 3 || question.type == 5))
};
export const hasObjective = (question: Question): boolean => {
    return !hasSubjective(question)
};
export const hasSingleChoice = (question: Question): boolean => {
    return question.type == 1
};
export const hasMultipleChoice = (question: Question): boolean => {
    return question.type == 2
};
export const hasFillBlank = (question: Question): boolean => {
    return question.type == 3
};
export const hasJudge = (question: Question): boolean => {
    return question.type == 4
};
export const hasQA = (question: Question): boolean => {
    return question.type == 5
};
export const fillBlankNum = (question: Question): number => {
    let fillblanksNum = question.title.match(/[_]{5,}/g)?.length
    if (!fillblanksNum) {
        fillblanksNum = 0
    }

    return fillblanksNum
}