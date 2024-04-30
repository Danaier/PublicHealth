<script setup>
import {onMounted, ref} from 'vue'
import * as echarts from 'echarts';
import axios from "axios";
import http from '../../utils/request.js';
import {dayjs} from "element-plus";
import {diseaseOptions, fixedMapOption, ageRangeMarks, formatTooltip, fixedBarOption} from './fixed.js'
import {findMax, findMin, getSum} from "../../utils/calculation.js";
import _ from 'lodash';
import {VideoPause, VideoPlay} from "@element-plus/icons-vue";


// 设立一些初始值
const disease = ref('包虫病');  // 病种只会被手动改变
const monthRange = ref([dayjs('2005-01-01'), dayjs('2008-01-01')]);
const ageRange = ref([0, 6])
const dataType = ref('cases_data')

// 日期变化进度条
const currentChosenDate = ref('05年01月')
const currentDatePercentage = ref(0)
const pauseTheProgress = ref(false)

// 地图类型
const chartTypeIsMap = ref(true)

let option
let hitmap
let hitmapChart
let mapOption
let barOption


// 切换图表类型
const switchChartType = () => {
    chartTypeIsMap.value = !chartTypeIsMap.value;
    option = chartTypeIsMap.value ? mapOption : barOption;
    hitmapChart.setOption(option, true);
}

// 随时间变化
const varyInDates = () => {
    pauseTheProgress.value = !pauseTheProgress.value
    http({
        url: '/data/getDataInProvincesVaryInDates',
        data: {
            disease: disease.value,
            dataType: dataType.value,
            age: ageRangeMarks.value[ageRange.value[0]],
            nextAge: ageRangeMarks.value[ageRange.value[1]],
            date: dayjs(monthRange.value[0]).format('YYYY-MM-DD'),
            nextDate: dayjs(monthRange.value[1]).format('YYYY-MM-DD')
        },
        method: 'post',
    }).then(dataInProvincesVaryInDates => {
        let index = 0;
        const intervalId = setInterval(() => {
            if (index < dataInProvincesVaryInDates.length && pauseTheProgress.value) {
                const dataInProvinces = dataInProvincesVaryInDates[index];
                currentDatePercentage.value = (index + 1) / dataInProvincesVaryInDates.length * 100;
                currentChosenDate.value = dayjs(dataInProvinces.date).format('YY年MM月');
                drawMap(dataInProvinces.data);
                index++; // Move to the next index
            } else {
                clearInterval(intervalId);
                pauseTheProgress.value = false;
            }
        }, 1000);
    })
}

// 刷新地图
const refresh = () => {
    hitmapChart.showLoading();
    currentDatePercentage.value = 0;
    currentChosenDate.value = dayjs(monthRange.value[0]).format('YY年MM月');
    pauseTheProgress.value = false;
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
    dataInProvinces.sort(function (a, b) {
        return a.value - b.value;
    });
    mapOption = {
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
    barOption = {
        yAxis: {
            data: dataInProvinces.map(item => item.name)
        },
        series: {
            data: dataInProvinces.map(item => item.value)
        }

    }
    mapOption = _.merge(mapOption, fixedMapOption)
    barOption = _.merge(barOption, fixedBarOption)
    axios.get('/china.json').then(updateChartData);
}


// 更新数据
const updateChartData = response => {
    const mapJson = response.data;
    hitmapChart.hideLoading();
    echarts.registerMap('China', mapJson);
    option = chartTypeIsMap.value ? mapOption : barOption;
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

  <div class="page-content">
    <!-- 第一行 -->
    <div class="row">
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
      <el-button
          type="primary"
          @click="varyInDates"

      >
        <el-icon v-if="!pauseTheProgress">
          <VideoPlay/>
        </el-icon>
        <el-icon v-if="pauseTheProgress">
          <VideoPause/>
        </el-icon>
      </el-button>

      <el-button
          type="primary"
          @click="switchChartType"
      >
        <span v-if="chartTypeIsMap">切换至坐标图</span>
        <span v-if="!chartTypeIsMap">切换至地图</span>
      </el-button>
    </div>


    <div class="row">

      <el-progress
          :text-inside="true"
          style="width: 100%"
          :stroke-width="20"
          :percentage="currentDatePercentage"
          status="exception"
          color="#409EFF"
      >
        <span>{{ currentChosenDate }}</span>
      </el-progress>

    </div>


    <!-- 数据地图显示 -->
    <div class="row">
      <el-card style="width: 100%;">
        <div id="hitmap" style="width: 800px;height:600px;"></div>
      </el-card>
    </div>


    <!-- 年龄范围选择 -->
    <el-slider
        style="height: 30px"
        v-model="ageRange"
        range
        :marks="ageRangeMarks"
        :format-tooltip="formatTooltip"
        :min=0
        :max=16
        :step=1
        @change="refresh"
    />


  </div>

</template>

<style scoped>
.page-content {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.row {
    display: flex;
    justify-content: space-between;
    gap: 10px;
}
</style>
