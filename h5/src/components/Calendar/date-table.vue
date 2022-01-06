<script>
import fecha from 'element-ui/src/utils/date'
import {
  range as rangeArr,
  getFirstDayOfMonth,
  getPrevMonthLastDays,
  getMonthDays,
  getI18nSettings,
  validateRangeInOneMonth,
} from 'element-ui/src/utils/date-util'
import ElPopover from 'element-ui/packages/popover'

export default {
  props: {
    selectedDay: String, // formated date yyyy-MM-dd
    range: {
      type: Array,
      validator(val) {
        if (!(val && val.length)) return true
        const [start, end] = val
        return validateRangeInOneMonth(start, end)
      },
    },
    date: Date,
    hideHeader: Boolean,
    firstDayOfWeek: Number,
    timePopovers: Object,
  },

  inject: ['elCalendar'],

  components: { ElPopover },

  methods: {
    toNestedArr(days) {
      return rangeArr(days.length / 7).map((_, index) => {
        const start = index * 7
        return days.slice(start, start + 7)
      })
    },

    getFormateDate(day, type) {
      if (!day || ['prev', 'current', 'next'].indexOf(type) === -1) {
        throw new Error('invalid day or type')
      }
      let prefix = this.curMonthDatePrefix
      if (type === 'prev') {
        prefix = this.prevMonthDatePrefix
      } else if (type === 'next') {
        prefix = this.nextMonthDatePrefix
      }
      day = `00${day}`.slice(-2)
      return `${prefix}-${day}`
    },

    getCellClass({ text, type }) {
      const classes = [type]
      if (type === 'current') {
        const date = this.getFormateDate(text, type)
        if (date === this.selectedDay) {
          classes.push('is-selected')
        }
        if (date === this.formatedToday) {
          classes.push('is-today')
        }
      }
      return classes
    },

    getPopoverData({ text, type }) {
      let status = ['prev', 'current', 'next']
      const year = new Date(this.date).getFullYear()
      const month = (new Date(this.date).getMonth() + status.indexOf(type))
        .toString()
        .padStart(2, '0')
      const day = text.toString().padStart(2, '0')
      const date = `${year}-${month}-${day}`
      const popoverData = this.timePopovers[date]
      return popoverData || {}
    },

    pickDay({ text, type }) {
      const date = this.getFormateDate(text, type)
      this.$emit('pick', date)
    },

    cellRenderProxy({ text, type }) {
      const render = this.elCalendar.$scopedSlots.dateCell
      if (!render) return <span>{text}</span>

      const day = this.getFormateDate(text, type)
      const date = new Date(day)
      const data = {
        isSelected: this.selectedDay === day,
        type: `${type}-month`,
        day,
      }
      return render({ date, data })
    },
  },

  computed: {
    WEEK_DAYS() {
      return getI18nSettings().dayNames
    },
    prevMonthDatePrefix() {
      const temp = new Date(this.date.getTime())
      temp.setDate(0)
      return fecha.format(temp, 'yyyy-MM')
    },

    curMonthDatePrefix() {
      return fecha.format(this.date, 'yyyy-MM')
    },

    nextMonthDatePrefix() {
      const temp = new Date(
        this.date.getFullYear(),
        this.date.getMonth() + 1,
        1
      )
      return fecha.format(temp, 'yyyy-MM')
    },

    formatedToday() {
      return this.elCalendar.formatedToday
    },

    isInRange() {
      return this.range && this.range.length
    },

    rows() {
      let days = []
      // if range exists, should render days in range.
      if (this.isInRange) {
        const [start, end] = this.range
        const currentMonthRange = rangeArr(
          end.getDate() - start.getDate() + 1
        ).map((_, index) => ({
          text: start.getDate() + index,
          type: 'current',
        }))
        let remaining = currentMonthRange.length % 7
        remaining = remaining === 0 ? 0 : 7 - remaining
        const nextMonthRange = rangeArr(remaining).map((_, index) => ({
          text: index + 1,
          type: 'next',
        }))
        days = currentMonthRange.concat(nextMonthRange)
      } else {
        const date = this.date
        let firstDay = getFirstDayOfMonth(date)
        firstDay = firstDay === 0 ? 7 : firstDay
        const firstDayOfWeek =
          typeof this.firstDayOfWeek === 'number' ? this.firstDayOfWeek : 1
        const offset = (7 + firstDay - firstDayOfWeek) % 7
        const prevMonthDays = getPrevMonthLastDays(date, offset).map((day) => ({
          text: day,
          type: 'prev',
        }))
        const currentMonthDays = getMonthDays(date).map((day) => ({
          text: day,
          type: 'current',
        }))
        days = [...prevMonthDays, ...currentMonthDays]
        const nextMonthDays = rangeArr(42 - days.length).map((_, index) => ({
          text: index + 1,
          type: 'next',
        }))
        days = days.concat(nextMonthDays)
      }
      return this.toNestedArr(days)
    },

    weekDays() {
      const start = this.firstDayOfWeek
      const { WEEK_DAYS } = this

      if (typeof start !== 'number' || start === 0) {
        return WEEK_DAYS.slice()
      } else {
        return WEEK_DAYS.slice(start).concat(WEEK_DAYS.slice(0, start))
      }
    },
  },

  render() {
    const thead = this.hideHeader ? null : (
      <thead>
        {this.weekDays.map((day) => (
          <th key={day}>{day}</th>
        ))}
      </thead>
    )

    const plan = (data, type) => (
      <div class="plan">
        <div>{type === 1 ? '考试：' : '阅卷：'}</div>
        <div class="plan-table">
          <div class="plan-table-th">
            <span>开始时间</span>
            <span>结束时间</span>
            <span>状态</span>
          </div>
          {data.map((item) => (
            <div class="plan-table-td">
              <span>{item.startTime}</span>
              <span>{item.endTime}</span>
              <span>{item.state ? item.state : '未知'}</span>
            </div>
          ))}
        </div>
      </div>
    )

    return (
      <table
        class={{
          'el-calendar-table': true,
          'is-range': this.isInRange,
        }}
        cellspacing="0"
        cellpadding="0"
      >
        {thead}
        <tbody>
          {this.rows.map((row, index) => (
            <tr
              class={{
                'el-calendar-table__row': true,
                'el-calendar-table__row--hide-border':
                  index === 0 && this.hideHeader,
              }}
              key={index}
            >
              {row.map((cell, key) => (
                <td
                  key={key}
                  class={this.getCellClass(cell)}
                  onClick={this.pickDay.bind(this, cell)}
                >
                  {Object.keys(this.getPopoverData(cell)).length > 0 ? (
                    <el-popover placement="bottom" trigger="hover">
                      {this.getPopoverData(cell)?.exam &&
                        plan(this.getPopoverData(cell).exam, 1)}
                      {this.getPopoverData(cell)?.mark &&
                        plan(this.getPopoverData(cell).mark, 2)}
                      <div
                        class={{
                          'el-calendar-day': true,
                          'el-calendar-exam': this.getPopoverData(cell)?.exam,
                          'el-calendar-mark': this.getPopoverData(cell)?.mark,
                          'el-calendar-both':
                            this.getPopoverData(cell)?.exam &&
                            this.getPopoverData(cell)?.mark,
                        }}
                        slot="reference"
                      >
                        <span>{this.cellRenderProxy(cell)}</span>
                        <div>
                          {this.getPopoverData(cell)?.exam ? (
                            <span style="font-size: 13px;">考</span>
                          ) : (
                            ''
                          )}
                          {this.getPopoverData(cell)?.mark ? (
                            <span style="font-size: 13px;">阅</span>
                          ) : (
                            ''
                          )}
                        </div>
                      </div>
                    </el-popover>
                  ) : (
                    <span class="el-calendar-day">
                      {this.cellRenderProxy(cell)}
                    </span>
                  )}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    )
  },
}
</script>

<style lang="scss">
.el-calendar-exam {
  background: rgba(#f73131, 0.15);
  // border: 2px solid rgba(#f73131, 0.8);
  border-radius: 8px;
  span {
    color: rgba(#f73131, 1);
  }
}
.el-calendar-mark {
  background: rgba(#4e6ef2, 0.15);
  // border: 2px solid rgba(#4e6ef2, 0.8);
  border-radius: 8px;
  span {
    color: rgba(#4e6ef2, 1);
  }
}
.el-calendar-both {
  background: rgba(#f89913, 0.15);
  // border: 2px solid rgba(#f7c173, 0.8);
  border-radius: 8px;
  span {
    color: rgba(#f89913, 1);
  }
}
.plan {
  display: flex;
  font-size: 13px;
  line-height: 30px;
  .plan-table {
    width: 240px;
    margin-bottom: 15px;
    .plan-table-th,
    .plan-table-td {
      display: flex;
      span {
        flex: 1;
        padding: 0 6px;
        text-align: center;
      }
      &:not(:last-child) {
        border-bottom: 1px solid #f5f5f5;
      }
    }
  }
}
</style>
