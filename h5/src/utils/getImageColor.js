const getMainColor = (image) => {
  return new Promise((resolve, reject) => {
    try {
      const canvas = document.createElement('canvas')
      const img = new Image() // 创建img元素
      img.src = image // 设置图片源地址
      img.onload = () => {
        let color = getImageColor(canvas, img)
        resolve(color)
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
  let pixelData = context.getImageData(0, 0, canvas.width, canvas.height).data
  return getCountsArr(pixelData)
}

const getCountsArr = (pixelData) => {
  let colorList = []
  let rgba = []
  let rgbaStr = ''
  let arr = []

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

  for (let prop in colorList) {
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

export default getMainColor
