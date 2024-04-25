<script setup>
import {onMounted, ref} from 'vue'
import * as echarts from 'echarts';
import axios from "axios";
import http from '../utils/request.js';
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


const disease = ref('伤寒与副伤寒');
const monthRange = ref([dayjs('2005-01-01'), dayjs('2008-01-01')]);

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
            age: '',
            nextAge: '',
            date: dayjs(monthRange.value[0]).format('YYYY-MM-DD'),
            nextDate: dayjs(monthRange.value[1]).format('YYYY-MM-DD')
        },
        method: 'post',
    }).then(data => {
        dataInProvinces = data;
        let averageNum = dataInProvinces.reduce(getSum, 0) / dataInProvinces.length;
        console.log(averageNum)

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
                min: dataInProvinces.reduce(findMin, dataInProvinces[0]).value - averageNum,
                max: dataInProvinces.reduce(findMax, dataInProvinces[0]).value + averageNum,
                inRange: {
                    color: [
                        '#313695',
                        '#4575b4',
                        '#74add1',
                        '#abd9e9',
                        '#e0f3f8',
                        '#ffffbf',
                        '#fee090',
                        '#fdae61',
                        '#f46d43',
                        '#d73027',
                        '#a50026'
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
        :key="item.value"
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
  <div id="hitmap" style="width: 800px;height:600px;"></div>
</template>

<style scoped>

</style>
