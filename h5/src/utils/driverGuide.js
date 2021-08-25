/*
 * @Description: 引导页配置
 * @Version: 1.0
 * @Company:
 * @Author: Che
 * @Date: 2021-08-24 09:56:37
 * @LastEditors: Che
 * @LastEditTime: 2021-08-24 11:34:39
 */

import { setDriver } from '@/utils/storage'

const driverSetting = {
  className: 'scoped-class', // className to wrap driver.js popover
  animate: true, // Animate while changing highlighted element
  opacity: 0.75, // Background opacity (0 means only popovers and without overlay)
  padding: 10, // Distance of element from around the edges
  allowClose: false, // Whether clicking on overlay should close or not
  overlayClickNext: false, // Should it move to next step on overlay click
  doneBtnText: '结束', // Text on the final button
  closeBtnText: '关闭', // Text on the close button for this step
  nextBtnText: '下一步', // Next button text for this step
  prevBtnText: '上一步', // Previous button text for this step
  onReset: () => {
    setDriver(true)
  },
}

// 试题编辑页引导步骤
const questionDriverStep = [
  {
    element: '#question_driver_query',
    popover: {
      title: '查询试题',
      description: '根据试题名称，类型，难度，分数查询试题',
      position: 'bottom',
    },
  },
  {
    element: '#question_driver_types',
    popover: {
      title: '试题类型',
      description: '单选，多选，填空，判断，阅读',
      position: 'right',
    },
  },
  {
    element: '#question_driver_template',
    popover: {
      title: '试题模板',
      description: '下载试题模板，上传自定义试题并解析',
      position: 'right',
    },
  },
  {
    element: '#question_driver_editor',
    popover: {
      title: '编辑内容',
      description: '试题内容的编辑,如题目，选项，答案，解析，智能设置等',
      position: 'left',
    },
  },
  {
    element: '#question_driver_content',
    popover: {
      title: '试题内容',
      description: '查询或者编辑成功后的试题展示区域',
      position: 'right',
    },
  },
]

export { driverSetting, questionDriverStep }
