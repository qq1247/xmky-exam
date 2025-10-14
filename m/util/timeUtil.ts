
import dayjs from 'dayjs'
export const delay = (ms: number) => {
    return new Promise(resolve => setTimeout(resolve, ms));
}
export const diff = (startTime: string, endTime: string) => {
    if (!startTime || !endTime) {
        return '-'
    }
    return Math.ceil((dayjs(endTime, 'YYYY-MM-DD HH:mm:ss').toDate().getTime()
        - dayjs(startTime, 'YYYY-MM-DD HH:mm:ss').toDate().getTime()) / (60
            * 1000));
}
