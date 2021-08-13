<!--
 * @Description: 
 * @Version: 1.0
 * @Company: 
 * @Author: Che
 * @Date: 2021-08-11 10:26:50
 * @LastEditors: Che
 * @LastEditTime: 2021-08-12 11:32:27
-->
<script>
export default {
  name: 'Navtem',
  functional: true,
  props: {
    icon: {
      type: String,
      default: '',
    },
    title: {
      type: String,
      default: '',
    },
  },
  render(h, context) {
    const { icon, title } = context.props
    const vnodes = []

    if (icon) {
      vnodes.push(<i class={[icon, 'menu-icon']} />)
    }

    if (title) {
      vnodes.push(
        <span slot="title" class="menu-title">
          {title}
        </span>
      )
    }
    return vnodes
  },
}
</script>

<style scoped>
.menu-icon {
  margin-right: 5px;
}
</style>
