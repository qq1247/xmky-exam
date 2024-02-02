<template>
    <!-- 下拉选择 -->
    <el-select v-model="selectedValue" :multiple="multiple" filterable remote :automatic-dropdown="true"
        :placeholder="placeholder" collapse-tags collapse-tags-tooltip :max-collapse-tags="8"
        @visible-change="(visible: Boolean) => { if (visible) { query() } }"
        @change="emit('update:modelValue', selectedValue)" popper-class="my-select">

        <!-- 搜索框 -->
        <el-input v-model="listpage.searchParmValue" @click.stop="" @input="() => { listpage.curPage = 1; query() }"
            :placeholder="searchPlaceholder">
            <template #prefix>
                <span class="iconfont icon-search"></span>
            </template>
        </el-input>

        <div style="display: flex;justify-content: space-between;margin: 5px 15px;">
            <!-- 工具条 -->
            <div>
                <el-button v-if="multiple" type="info" link @click="selectAll">
                    <span class="iconfont icon-quanxuan">全选</span>
                </el-button>
                <el-button v-if="multiple" type="info" link @click="invertAll">
                    <span class="iconfont icon-list-row">反选</span>
                </el-button>
            </div>
            <!-- 分页条件 -->
            <el-pagination small v-model:current-page="listpage.curPage" v-model:page-size="listpage.pageSize"
                :total="listpage.total" background layout="prev, pager, next, sizes" :page-sizes="[5, 10, 100]"
                @size-change="listpage.curPage = 1; query()" @current-change="query" @prev-click="query"
                @next-click="query" />
        </div>

        <!-- 选项 -->
        <el-option v-for="option in listpage.options" :key="option[optionValue]" :label="option[optionLabel]"
            :value="option[optionValue]">
            <slot :option="option"></slot>
        </el-option>
        <!-- 无选项时显示 -->
        <el-option v-if="listpage.options.length === 0" key="" lable="" value="暂无数据" disabled>
        </el-option>
    </el-select>
</template>

<script lang="ts" setup>
import { ref, watch, reactive, type Ref } from 'vue';
import http from '@/request'

// 定义变量
const emit = defineEmits(['update:modelValue'])
const props = defineProps({
    url: { type: [String], required: true, default: '' },// 请求地址
    params: { type: [Object], required: false, default: () => { } },// 请求参数
    searchParmName: { type: [String], required: false, default: '' },// 搜索框参数名称（请求接口时要携带的参数）
    optionLabel: { type: [String], required: true, default: 'id' },// 选项显示名称
    optionValue: { type: [String, Number], required: true, default: 'name' },// 选项实际值
    multiple: { type: [Boolean], required: false, default: true },// 是否多选，默认多选
    modelValue: { type: [String, Number, Array], required: true, default: '' }, // 该组件上v-model的值
    options: { type: [Array], required: false, default: () => [] },// 用于解决回显数据时，只显示option-value的值的问题。解决方式为本地先缓存一份数据
    placeholder: { type: [String], required: false, default: '请选择' },
    searchPlaceholder: { type: [String], required: false, default: '请输入查询条件' },
})
const selectedValue = ref(props.modelValue)// 单选为字符串或数字，多选为列表数据
const listpage = reactive({// 分页列表
    curPage: 1,// 当前第几页
    pageSize: 5,// 每页显示多少条
    total: 0,// 总条数
    options: props.options as any[],// 下拉选项
    searchParmValue: '' // 搜索框文本
})

// 监听属性
watch(() => props.options, (n, o) => {
    listpage.options = props.options
})
watch(() => props.modelValue, (n, o) => {
    selectedValue.value = props.modelValue
})

// 查询
async function query() {
    let { data: { data } } = await http.post(props.url, {
        curPage: listpage.curPage,
        pageSize: listpage.pageSize,
        [props.searchParmName]: listpage.searchParmValue,
        ...props.params,
    })
    listpage.options = data.list
    listpage.total = data.total
}

// 全选
function selectAll() {
    if (!props.multiple) {
        return
    }
    listpage.options.forEach((option) => {
        if ((selectedValue.value as any[]).indexOf(option[props.optionValue]) === -1) {
            (selectedValue.value as any[]).push(option[props.optionValue])
        }
    })

    emit('update:modelValue', selectedValue.value)
}

// 反选
function invertAll() {
    if (!props.multiple) {
        return
    }

    listpage.options.forEach((option) => {
        let index = (selectedValue.value as any[]).indexOf(option[props.optionValue])
        if (index === -1) {
            (selectedValue.value as any[]).push(option[props.optionValue])
        } else {
            (selectedValue.value as any[]).splice(index, 1)
        }
    })

    emit('update:modelValue', selectedValue.value)
}

</script>

<style lang="scss" scoped>
.my-select {
    .iconfont {
        font-size: 14px;
        color: var(--el-text-color-regular);
    }
    .el-input {
        margin-bottom: 5px;

        .el-select__caret {
            color: red !important;
        }

        .iconfont {
            font-size: 18px;
            color: var(--el-text-color-regular);
        }

        :deep(.el-input__wrapper) {
            border-bottom: 1px solid var(--el-border-color);
            box-shadow: none;
            border-radius: 0;
            margin: 0px 10px;
            padding: 0px 0px;

            &.is-focus {
                box-shadow: none !important;
            }
        }
    }
}

.el-select {
    flex: 1;

    .el-button {
        margin-left: 10px;

        span {
            font-size: 14px;
            color: var(--el-text-color-regular);
        }

        &:hover {
            span {
                color: var(--el-color-primary);
            }
        }
    }

    

    :deep(.el-select-dropdown__wrap) {
        max-height: 435px; // 当分页是100，数据超过10个的时候，显示10个半，表示下面还有数据
    }

    :deep(.el-pagination) {
        margin-right: 10px;
    }
}
</style>

<style lang="scss">
.el-select .el-input .el-select__caret {
    color: var(--el-color-primary);
}
</style>