/**
 * 数字转汉字
 * @param num 
 */
export const toChinaNum = (num: number): string => {
    // 将接收到的num转换为字符串
    const strNum = num.toString()
    // 定义单位
    // var unit = ['拾', '佰', '仟', '万', '拾', '佰', '仟', '亿', '拾', '佰', '仟']
    const unit = ['十', '百', '千', '万', '十', '百', '千', '亿', '十', '百', '千']
    // 结果中放一个符号，用来解决最后的零去不掉的问题
    const result = ['@']
    // 单位下标
    let unitNo = 0
    // 从后往前遍历接收到的数据，省略结束条件
    for (let i = strNum.length - 1; ; i--) {
        // 调用转大写函数，将每一个数字转换成中文大写，一次放入一个到结果数组中
        result.unshift(numToChinese(parseInt(strNum[i])))
        // 如果不大于0
        if (i <= 0) {
            // 结束循环
            break
        }
        // 放入一个数字，放入一个单位
        result.unshift(unit[unitNo])
        // 单位下标加1
        unitNo++
    }
    // 将结果数组转换成字符串，并使用正则替换一些关键位置，让结果符合语法
    // return result.join('').replace(/(零[仟佰拾]){1,3}/g, '零').replace(/零{2,}/g, '零').replace(/零([万亿])/g, '$1').replace(/亿万/g, '亿').replace(/零*@/g, '')
    return result.join('').replace(/(零[千百十]){1,3}/g, '零').replace(/零{2,}/g, '零').replace(/零([万亿])/g, '$1').replace(/亿万/g, '亿').replace(/零*@/g, '')

    function numToChinese(n: number) {
        // var chineseBigNum = '零壹贰叁肆伍陆柒捌玖'
        const chineseBigNum = '零一二三四五六七八九'
        return chineseBigNum[n]
    }
}

/**
 * 数字转字母
 * 
 * @param num 
 */
export const toLetter = (num: number): string => {
    if (num < 0 || num > 26) {
        throw new Error('数字超出范围');
    }
    return String.fromCharCode(num + 65);
}