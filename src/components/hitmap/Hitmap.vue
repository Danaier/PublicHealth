<script setup>
import {onMounted, ref} from 'vue'
import * as echarts from 'echarts';
import axios from "axios";
import http from '../../utils/request.js';
import {dayjs} from "element-plus";

// 工具函数
let findMax = (max, currentValue) => {
    return max.value > currentValue.value ? max : currentValue;
}
let findMin = (min, currentValue) => {
    return min.value < currentValue.value ? min : currentValue;
}
let getSum = (total, currentValue) => {
    return total + currentValue.value;
}

const disease = ref('包虫病');
const monthRange = ref([dayjs('2005-01-01'), dayjs('2008-01-01')]);
const ageRange = ref([0, 6])
const ageRangeMarks = ref({
    0: '0-',
    1: '5-',
    2: '10-',
    3: '15-',
    4: '20-',
    5: '25-',
    6: '30-',
    7: '35-',
    8: '40-',
    9: '45-',
    10: '50-',
    11: '55-',
    12: '60-',
    13: '65-',
    14: '70-',
    15: '75-',
    16: '80-',
    17: '85及以上'
})
const formatTooltip = (val) => {
    return ageRangeMarks.value[val];
}

const diseaseOptions = [
    {value: "出血热", label: "出血热"},
    {value: "包虫病", label: "包虫病"},
    {value: "急性出血性结膜炎", label: "急性出血性结膜炎"},
    {value: "手足口病", label: "手足口病"},
    {value: "斑疹伤寒", label: "斑疹伤寒"},
    {value: "新生儿破伤风", label: "新生儿破伤风"},
    {value: "流行性感冒", label: "流行性感冒"},
    {value: "流行性腮腺炎", label: "流行性腮腺炎"},
    {value: "甲型H1N1流感", label: "甲型H1N1流感"},
    {value: "登革热", label: "登革热"},
    {value: "白喉", label: "白喉"},
    {value: "百日咳", label: "百日咳"},
    {value: "肺结核", label: "肺结核"},
    {value: "艾滋病", label: "艾滋病"},
    {value: "血吸虫病", label: "血吸虫病"},
    {value: "钩体病", label: "钩体病"},
    {value: "霍乱", label: "霍乱"},
    {value: "麻疹", label: "麻疹"},
    {value: "麻风病", label: "麻风病"},
    {value: "黑热病", label: "黑热病"}
]


let dataInProvinces
let option
let hitmap
let hitmapChart
let dataType = 'cases_data'


// 绘制地图
let drawMap = () => {
    hitmapChart.showLoading();
    http({
        url: '/data/getDataInProvinces',
        data: {
            disease: disease.value,
            dataType: dataType,
            age: ageRangeMarks.value[ageRange.value[0]],
            nextAge: ageRangeMarks.value[ageRange.value[1]],
            date: dayjs(monthRange.value[0]).format('YYYY-MM-DD'),
            nextDate: dayjs(monthRange.value[1]).format('YYYY-MM-DD')
        },
        method: 'post',
    }).then(data => {

        dataInProvinces = data;
        let averageNum = dataInProvinces.reduce(getSum, 0) / dataInProvinces.length;
        let maxNum = dataInProvinces.reduce(findMax, dataInProvinces[0]).value + averageNum
        let minNum = dataInProvinces.reduce(findMin, dataInProvinces[0]).value - averageNum
        minNum = minNum < 0 ? 0 : minNum

        option = {
            title: {
                text: disease.value,
                subtext: '来源：公共卫生科学数据中心',
                sublink: 'https://www.phsciencedata.cn/',
                left: 'right'
            },
            // 鼠标悬浮提示
            tooltip: {
                trigger: 'item',  // 数据项图形触发
                showDelay: 0,  // 显示延迟，添加显示延迟可以避免频繁切换
                transitionDuration: 0.2  // 提示框浮层的移动动画过渡时间，单位是 s，设置为 0 的时候会紧跟着鼠标移动
            },
            // 右侧图例
            visualMap: {
                left: 'right',
                min: minNum,
                max: maxNum,
                inRange: {
                    color: [
                        "#EEEEEE",  // 浅灰色，适用于表示极低的数据或零数据
                        "#f7e4e4",  // 较浅的红色
                        "#f0c8c8",  // 浅红色
                        "#e9acac",  // 中等浅红
                        "#e29090",  // 明亮的红色
                        "#db7474",  // 较深的红色
                        "#d45858",  // 深红色
                        "#cd3c3c",  // 更深的红色
                        "#c62020",  // 强烈的红色
                        "#bf0404"   // 鲜艳的红色，适用于表示非常高的数据
                    ]

                },
                text: ['High', 'Low'],
                calculable: true
            },
            toolbox: {
                show: false,
                //orient: 'vertical',
                left: 'left',
                top: 'top',
                feature: {
                    dataView: {readOnly: false},
                    restore: {},
                    saveAsImage: {}
                }
            },
            series: [
                {
                    name: disease.value,
                    type: 'map',
                    roam: true,
                    map: 'China',
                    emphasis: {
                        label: {
                            show: true
                        }
                    },
                    data: dataInProvinces
                }
            ]
        };
        axios.get('https://geojson.cn/api/data/china.json').then(updateMapData);
    })
}

// 更新数据
let updateMapData = response => {
    const mapJson = response.data;
    hitmapChart.hideLoading();
    echarts.registerMap('China', mapJson);
    hitmapChart.setOption(option);
}


onMounted(() => {

    // 初始化地图
    hitmap = document.getElementById('hitmap');
    hitmapChart = echarts.init(hitmap);

    drawMap();

});


</script>

<template>
  <!-- 病种选择 -->
  <el-select
      v-model="disease"
      placeholder="Select"
      style="width: 240px"
      @change="drawMap"
  >
    <el-option
        v-for="item in diseaseOptions"
        :label="item.label"
        :value="item.value"
    />
  </el-select>
  <!-- 时间范围选择 -->
  <el-date-picker
      v-model="monthRange"
      type="monthrange"
      unlink-panels
      range-separator="到"
      start-placeholder="起始日期"
      end-placeholder="结束日期"
      @change="drawMap"
  />
  <!-- 数据地图显示 -->
  <div id="hitmap" style="width: 1000px;height:750px;"></div>
  <!-- 年龄范围选择 -->
  <el-slider
      v-model="ageRange"
      range
      :marks="ageRangeMarks"
      :format-tooltip="formatTooltip"
      :min=0
      :max=16
      :step=1
      @change="drawMap"
  />
</template>

<style scoped>

</style>
