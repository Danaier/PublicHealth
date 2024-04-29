<script setup>
import {onMounted, ref} from 'vue'
import * as echarts from 'echarts';
import axios from "axios";
import http from '../../utils/request.js';
import {dayjs} from "element-plus";
import {diseaseOptions, fixedOption, ageRangeMarks, formatTooltip} from './fixed.js'
import {findMax, findMin, getSum} from "../../utils/calculation.js";
import _ from 'lodash';

// 设立一些初始值
const disease = ref('包虫病');  // 病种只会被手动改变
const monthRange = ref([dayjs('2005-01-01'), dayjs('2008-01-01')]);
const ageRange = ref([0, 6])
const dataType = ref('cases_data')

let option
let hitmap
let hitmapChart


// 刷新地图
const refresh = () => {
    hitmapChart.showLoading();
    http({
        url: '/data/getDataInProvinces',
        data: {
            disease: disease.value,
            dataType: dataType.value,
            age: ageRangeMarks.value[ageRange.value[0]],
            nextAge: ageRangeMarks.value[ageRange.value[1]],
            date: dayjs(monthRange.value[0]).format('YYYY-MM-DD'),
            nextDate: dayjs(monthRange.value[1]).format('YYYY-MM-DD')
        },
        method: 'post',
    }).then(drawMap)
}


// 绘制地图
const drawMap = dataInProvinces => {
    // 计算图例的最大值和最小值
    let averageNum = dataInProvinces.reduce(getSum, 0) / dataInProvinces.length;
    let maxNum = dataInProvinces.reduce(findMax, dataInProvinces[0]).value + averageNum
    let minNum = dataInProvinces.reduce(findMin, dataInProvinces[0]).value - averageNum
    minNum = minNum < 0 ? 0 : minNum
    option = {
        title: {
            text: disease.value,
        },
        visualMap: {
            min: minNum,
            max: maxNum,
        },
        series: [
            {
                name: disease.value,
                data: dataInProvinces
            }
        ]
    };
    option = _.merge(option, fixedOption)
    axios.get('/china.json').then(updateMapData);
}


// 更新数据
const updateMapData = response => {
    const mapJson = response.data;
    hitmapChart.hideLoading();
    echarts.registerMap('China', mapJson);
    hitmapChart.setOption(option);
}


onMounted(() => {

    // 初始化地图
    hitmap = document.getElementById('hitmap');
    hitmapChart = echarts.init(hitmap);

    refresh();

});


</script>

<template>
  <!-- 病种选择 -->
  <el-select
      v-model="disease"
      placeholder="Select"
      style="width: 240px"
      @change="refresh"
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
      @change="refresh"
  />
  <!-- 数据地图显示 -->
  <div id="hitmap" style="width: 800px;height:600px;"></div>
  <!-- 年龄范围选择 -->
  <el-slider
      v-model="ageRange"
      range
      :marks="ageRangeMarks"
      :format-tooltip="formatTooltip"
      :min=0
      :max=16
      :step=1
      @change="refresh"
  />
</template>

<style scoped>

</style>
