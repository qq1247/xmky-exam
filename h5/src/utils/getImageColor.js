const getMainColor = (image) => {
  return new Promise((resolve, reject) => {
    try {
      const canvas = document.createElement('canvas')
      const img = new Image() // 创建img元素
      img.src = image // 设置图片源地址
      img.onload = () => {
        const color = getImageColor(canvas, img)
        const colorHex = getColorHex(color)
        const colorReverse = getColorReverse(colorHex)
        resolve({ colorHex, colorReverse })
      }
    } catch (e) {
      reject(e)
    }
  })
}

const getImageColor = (canvas, img) => {
  const context = canvas.getContext('2d')
  context.drawImage(img, 0, 0)

  // 获取像素数据
  const pixelData = context.getImageData(0, 0, canvas.width, canvas.height).data
  return getCountsArr(pixelData)
}

const getCountsArr = (pixelData) => {
  const colorList = []
  const rgba = []
  let rgbaStr = ''
  const arr = []

  // 分组循环
  for (let i = 0; i < pixelData.length; i += 4) {
    rgba[0] = pixelData[i]
    rgba[1] = pixelData[i + 1]
    rgba[2] = pixelData[i + 2]
    rgba[3] = pixelData[i + 3]

    if (rgba.indexOf(undefined) !== -1 || pixelData[i + 3] === 0) {
      continue
    }
    rgbaStr = rgba.join(',')
    if (rgbaStr in colorList) {
      ++colorList[rgbaStr]
    } else {
      colorList[rgbaStr] = 1
    }
  }

  for (const prop in colorList) {
    arr.push({
      // 如果只获取rgb,则为`rgb(${prop})`
      color: `rgba(${prop})`,
      count: colorList[prop],
    })
  }

  // 数组排序
  arr.sort((a, b) => {
    return b.count - a.count
  })

  return arr[0].color
}

const getColorHex = (color) => {
  // RGBA颜色值的正则
  let reg = /^(rgba|RGBA)/
  if (reg.test(color)) {
    let strHex = '#'
    // 把RGBA的4个数值变成数组
    let colorArr = color.replace(/(?:\(|\)|rgba|RGBA)*/g, '').split(',')
    // 转成16进制
    for (let i = 0; i < colorArr.length - 1; i++) {
      let hex = Number(colorArr[i]).toString(16)
      if (hex === '0') {
        hex += hex
      }
      strHex += hex
    }
    return strHex
  } else {
    return String(color)
  }
}

const getColorReverse = (colorHex) => {
  let $_colorHex = '0x' + colorHex.replace(/#/g, '')
  let str = '000000' + (0xffffff - $_colorHex).toString(16)
  return '#' + str.substring(str.length - 6, str.length)
}

export default getMainColor
