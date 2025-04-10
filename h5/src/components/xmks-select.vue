<template>
    <!-- 下拉选择框（popper-class：class是页面外层的） -->
    <el-select v-model="selectedValue" :multiple="multiple" filterable remote :automatic-dropdown="true"
        :placeholder="placeholder" collapse-tags collapse-tags-tooltip :max-collapse-tags="3" remote-show-suffix
        popper-class="xmks-select" @visible-change="(visible: boolean) => { visible && query() }"
        @change="emit('update:modelValue', selectedValue)">
        <!-- 搜索框 -->
        <el-input v-model="listpage.searchParmValue" :placeholder="searchPlaceholder" class="xmks-select__search"
            @input="() => { listpage.curPage = 1; query() }">
            <template #prefix>
                <span class="iconfont icon-sousuo xmks-select__search-icon"></span>
            </template>
        </el-input>
        <div class="xmks-select__wrap">
            <!-- 分页条件 -->
            <el-pagination v-model:current-page="listpage.curPage" v-model:page-size="listpage.pageSize"
                :total="listpage.total" background layout="prev, pager, next, sizes" :page-sizes="[5, 20, 100]"
                :default-page-size="props.pageSize" :pager-count="5" class="pagination"
                @size-change="listpage.curPage = 1; query()" @current-change="query" @prev-click="query"
                @next-click="query" />
            <!-- 工具条 -->
            <div class="toolbar">
                <el-button v-if="multiple" type="info" link class="toolbar__btn" @click="selectAll">
                    <span class="iconfont icon-tubiaoziti-52">全选</span>
                </el-button>
                <el-button v-if="multiple" type="info" link class="toolbar__btn" @click="invertAll">
                    <span class="iconfont icon-tubiaoziti-51">反选</span>
                </el-button>
            </div>
        </div>
        <!-- 选项 -->
        <el-option v-for="option in listpage.list" :key="option[optionValue]" :label="option[optionLabel]"
            :value="option[optionValue]" :disabled="disabledValues.includes(option[optionValue])"
            class="xmks-select__option">
            <slot :option="option"></slot>
        </el-option>
        <!-- 无选项时显示 -->
        <el-option v-if="listpage.list.length === 0" key="" lable="" value="暂无数据" disabled>
        </el-option>
    </el-select>
</template>

<script lang="ts" setup>
import { ref, watch, reactive } from 'vue'
import http from '@/request'
import type { Listpage } from '@/ts/common/listpage'

/************************变量定义相关***********************/
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
    pageSize: { type: [Number], required: false, default: 5 },
    disabledValues: { type: [Array], required: false, default: () => [] },// 禁用选项的值
})
const selectedValue = ref(props.modelValue)// 单选为字符串或数字，多选为列表数据
const listpage = reactive<Listpage>({// 分页列表
    curPage: 1,// 当前第几页
    pageSize: props.pageSize,// 每页显示多少条
    total: 0,// 总条数
    list: props.options,// 下拉选项
    searchParmValue: '' // 搜索框文本
})

/************************监听相关*****************************/
watch(() => props.options, () => {
    listpage.list = props.options
})
watch(() => props.modelValue, () => {
    selectedValue.value = props.modelValue
})

/************************事件相关*****************************/
// 查询
async function query() {
    const { data: { data } } = await http.post(props.url, {
        curPage: listpage.curPage,
        pageSize: listpage.pageSize,
        [props.searchParmName]: listpage.searchParmValue,
        ...props.params,
    })
    listpage.list = data.list
    listpage.total = data.total
}

// 全选
function selectAll() {
    if (!props.multiple) {
        return
    }
    listpage.list.forEach((option) => {
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

    listpage.list.forEach((option) => {
        const index = (selectedValue.value as any[]).indexOf(option[props.optionValue])
        if (index === -1) {
            (selectedValue.value as any[]).push(option[props.optionValue])
        } else {
            (selectedValue.value as any[]).splice(index, 1)
        }
    })

    emit('update:modelValue', selectedValue.value)
}

</script>

<style lang="scss">
// 已经脱离dom内部，不要scoped。改完不生效需要刷新一下页面。
.xmks-select {
    padding: 10px 20px 20px 20px;

    .el-select-dropdown__wrap {
        max-height: 285px;
    }

    .xmks-select__search {
        margin-bottom: 10px;

        .xmks-select__search-icon {
            font-size: 18px;
        }

        .el-input__wrapper {
            border-bottom: 1px solid #E5E5E5;
            border-radius: 0px;
            box-shadow: initial;

            &.is-focus {
                box-shadow: initial;
            }
        }
    }

    .xmks-select__wrap {
        display: flex;
        justify-content: space-between;

        .pagination {
            margin-bottom: 20px;

            .btn-prev,
            .btn-next {
                width: 38px;
                height: 38px;
                border-radius: 4px;
            }

            .el-pager {

                .number,
                .more {
                    width: 38px;
                    height: 38px;
                    border-radius: 4px;
                }

                .is-active {
                    border: 0px;
                    color: #FFFFFF;
                    font-size: 18px;
                    background-image: linear-gradient(to right, #04C7F2, #259FF8);
                }
            }

            .el-select__wrapper {
                height: 38px;

                .el-select__input {
                    font-size: 14px;
                    color: #999999;
                    line-height: 45px;
                }
            }
        }

        .toolbar {
            margin: 0px 10px 20px 50px;
            display: flex;
            align-items: center;

            .toolbar__btn {
                &:hover {
                    color: #1EA1EE;
                }
            }
        }
    }

    .xmks-select__option {
        display: flex;
        align-items: center;
        font-size: 14px;
        color: #333333;
        line-height: 45px;

        &.is-selected {
            color: #1EA1EE;
        }

        &.is-hovering {
            background: #F2F6F9;
        }
    }
}
</style>
