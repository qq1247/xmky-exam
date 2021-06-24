const questionList = {
  code: 200,
  msg: '请求成功',
  data: {
    total: 7,
    list: [
      {
        no: 1,
        ver: 1,
        updateUserId: 1,
        updateUserName: '管理员',
        typeName: '单选',
        scoreOptions: null,
        updateTime: '2021-06-11T06:44:06.000+0000',
        analysis: '2',
        questionTypeId: 13,
        title: '1+1=?',
        type: 1,
        difficultyName: '极易',
        difficulty: 1,
        questionTypeName: '原子弹',
        score: 1,
        answer: 'C',
        srcId: 4,
        state: 1,
        id: 4,
        option: [
          {
            id: 12,
            options: '<p>0</p>',
            no: 1,
            questionId: 4
          },
          {
            id: 13,
            options: '<p>1</p>',
            no: 2,
            questionId: 4
          },
          {
            id: 14,
            options: '<p>2</p>',
            no: 3,
            questionId: 4
          }
        ]
      },
      {
        no: 1,
        ver: 1,
        updateUserId: 1,
        updateUserName: '管理员',
        typeName: '单选',
        scoreOptions: null,
        updateTime: '2021-06-18T06:43:33.000+0000',
        analysis: '2',
        questionTypeId: 13,
        title: '1+1=?',
        type: 1,
        difficultyName: '极易',
        difficulty: 1,
        questionTypeName: '原子弹',
        score: 1,
        answer: 'C',
        srcId: 4,
        state: 1,
        id: 5,
        option: []
      },
      {
        no: 1,
        ver: 1,
        updateUserId: 1,
        updateUserName: '管理员',
        typeName: '单选',
        scoreOptions: null,
        updateTime: '2021-06-18T06:44:21.000+0000',
        analysis: '77777',
        questionTypeId: 20,
        title: '12345678977777',
        type: 1,
        difficultyName: '适中',
        difficulty: 3,
        questionTypeName: '86878',
        score: 1,
        answer: 'B',
        srcId: 6,
        state: 2,
        id: 6,
        option: [
          {
            id: 15,
            options: '<p>777</p>',
            no: 1,
            questionId: 6
          },
          {
            id: 16,
            options: '<p>77</p>',
            no: 2,
            questionId: 6
          },
          {
            id: 17,
            options: '<p>77</p>',
            no: 3,
            questionId: 6
          }
        ]
      },
      {
        no: 1,
        ver: 1,
        updateUserId: 1,
        updateUserName: '管理员',
        typeName: '多选',
        scoreOptions: '1',
        updateTime: '2021-06-18T10:04:50.000+0000',
        analysis: '123',
        questionTypeId: 20,
        title: 'ff',
        type: 2,
        difficultyName: '极易',
        difficulty: 1,
        questionTypeName: '86878',
        score: 1,
        answer: 'A,B,C',
        srcId: 7,
        state: 2,
        id: 7,
        option: [
          {
            id: 18,
            options: '<p>1</p>',
            no: 1,
            questionId: 7
          },
          {
            id: 19,
            options: '<p>2</p>',
            no: 2,
            questionId: 7
          },
          {
            id: 20,
            options: '<p>3</p>',
            no: 3,
            questionId: 7
          }
        ]
      },
      {
        no: 1,
        ver: 1,
        updateUserId: 1,
        updateUserName: '管理员',
        typeName: '填空',
        scoreOptions: '1,4',
        updateTime: '2021-06-21T01:33:59.000+0000',
        analysis: '嘎嘎嘎',
        questionTypeId: 20,
        title: '1111',
        type: 3,
        difficultyName: '极易',
        difficulty: 1,
        questionTypeName: '86878',
        score: 1,
        answer: '反对法\nfa方大',
        srcId: 8,
        state: 2,
        id: 8
      },
      {
        no: 1,
        ver: 1,
        updateUserId: 1,
        updateUserName: '管理员',
        typeName: '判断',
        scoreOptions: null,
        updateTime: '2021-06-21T01:34:18.000+0000',
        analysis: '999',
        questionTypeId: 20,
        title: '9999',
        type: 4,
        difficultyName: '极易',
        difficulty: 1,
        questionTypeName: '86878',
        score: 1,
        answer: '对',
        srcId: 9,
        state: 2,
        id: 9
      },
      {
        no: 1,
        ver: 1,
        updateUserId: 1,
        updateUserName: '管理员',
        typeName: '问答',
        scoreOptions: null,
        updateTime: '2021-06-21T01:34:28.000+0000',
        analysis: '888',
        questionTypeId: 20,
        title: '888',
        type: 5,
        difficultyName: '极易',
        difficulty: 1,
        questionTypeName: '86878',
        score: 1,
        answer: '<p>888</p>',
        srcId: 10,
        state: 2,
        id: 10
      }
    ]
  }
}

const cardList = {
  code: 200,
  msg: '请求成功',
  data: {
    total: 1,
    list: [
      {
        createUserId: 1,
        rwState: 2,
        imgId: null,
        updateUserId: 1,
        createTime: '2017-08-01T14:31:43.000+0000',
        name: '试题分类',
        writeUserIds: null,
        updateTime: '2017-08-01T14:31:43.000+0000',
        createUserName: '管理员',
        state: 1,
        id: 1,
        readUserIds: null
      }
    ]
  }
}

const paperList = {
  code: 200,
  msg: '请求成功',
  data: [
    {
      chapter: {
        name: '判断题',
        description: '',
        id: 25,
        parentId: 24
      },
      questionList: [
        {
          difficulty: 1,
          score: 1,
          answer: 'C',
          updateUserName: '管理员',
          typeName: '单选',
          options: [
            {
              id: 12,
              options: '0',
              no: 1,
              questionId: 4
            },
            {
              id: 13,
              options: '1',
              no: 2,
              questionId: 4
            },
            {
              id: 14,
              options: '2',
              no: 3,
              questionId: 4
            }
          ],
          scoreOptions: null,
          id: 4,
          type: 1,
          title: '1+1=?',
          analysis: '2',
          difficultyName: '极易'
        },
        {
          difficulty: 1,
          score: 1,
          answer: 'C',
          updateUserName: '管理员',
          typeName: '单选',
          options: [
            {
              id: 12,
              options: '0',
              no: 1,
              questionId: 4
            },
            {
              id: 13,
              options: '1',
              no: 2,
              questionId: 4
            },
            {
              id: 14,
              options: '2',
              no: 3,
              questionId: 4
            }
          ],
          scoreOptions: null,
          id: 4,
          type: 1,
          title: '1+1=?',
          analysis: '2',
          difficultyName: '极易'
        },
        {
          difficulty: 3,
          score: 1,
          answer: 'B',
          updateUserName: '管理员',
          typeName: '单选',
          options: [
            {
              id: 15,
              options: '777',
              no: 1,
              questionId: 6
            },
            {
              id: 16,
              options: '77',
              no: 2,
              questionId: 6
            },
            {
              id: 17,
              options: '77',
              no: 3,
              questionId: 6
            }
          ],
          scoreOptions: null,
          id: 6,
          type: 1,
          title: '12345678977777',
          analysis: '77777',
          difficultyName: '适中'
        },
        {
          difficulty: 1,
          score: 1,
          answer: 'C',
          updateUserName: '管理员',
          typeName: '单选',
          options: [
            {
              id: 12,
              options: '0',
              no: 1,
              questionId: 4
            },
            {
              id: 13,
              options: '1',
              no: 2,
              questionId: 4
            },
            {
              id: 14,
              options: '2',
              no: 3,
              questionId: 4
            }
          ],
          scoreOptions: null,
          id: 4,
          type: 1,
          title: '1+1=?',
          analysis: '2',
          difficultyName: '极易'
        },
        {
          difficulty: 1,
          score: 1,
          answer: 'C',
          updateUserName: '管理员',
          typeName: '单选',
          options: [
            {
              id: 12,
              options: '0',
              no: 1,
              questionId: 4
            },
            {
              id: 13,
              options: '1',
              no: 2,
              questionId: 4
            },
            {
              id: 14,
              options: '2',
              no: 3,
              questionId: 4
            }
          ],
          scoreOptions: null,
          id: 4,
          type: 1,
          title: '1+1=?',
          analysis: '2',
          difficultyName: '极易'
        },
        {
          difficulty: 1,
          score: 1,
          answer: 'C',
          updateUserName: '管理员',
          typeName: '单选',
          options: [],
          scoreOptions: null,
          id: 5,
          type: 1,
          title: '1+1=?',
          analysis: '2',
          difficultyName: '极易'
        },
        {
          difficulty: 1,
          score: 1,
          answer: 'C',
          updateUserName: '管理员',
          typeName: '单选',
          options: [],
          scoreOptions: null,
          id: 5,
          type: 1,
          title: '1+1=?',
          analysis: '2',
          difficultyName: '极易'
        }
      ]
    },
    {
      chapter: {
        name: '单选题',
        description: '',
        id: 26,
        parentId: 24
      },
      questionList: [
        {
          difficulty: 1,
          score: 1,
          answer: '对',
          updateUserName: '管理员',
          typeName: '判断',
          scoreOptions: null,
          id: 9,
          type: 4,
          title: '9999',
          analysis: '999',
          difficultyName: '极易'
        },
        {
          difficulty: 1,
          score: 1,
          answer: '反对法 fa方大',
          updateUserName: '管理员',
          typeName: '填空',
          scoreOptions: '1,4',
          id: 8,
          type: 3,
          title: '1111',
          analysis: '嘎嘎嘎',
          difficultyName: '极易'
        }
      ]
    }
  ]
}

export { questionList, cardList, paperList }
