import type { Exam } from "@/ts/exam/exam";

export const hasManualGen = (exam: Exam): boolean => {
    return exam.genType === 1
};