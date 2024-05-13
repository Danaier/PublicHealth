<script setup>
import {onMounted, ref} from 'vue'
import * as echarts from 'echarts';
import http from '../../utils/request.js';
import {dayjs} from "element-plus";
import {
    diseaseOptions,
    fixedMapOption,
    ageRangeMarks,
    ageRangeValue,
    formatTooltip,
    fixedBarOption,
    fixedLineOption,
    chartTypeOptions,
} from './fixed.js'
import {findMax, findMin, getSum} from "../../utils/calculation.js";
import _ from 'lodash';
import {VideoPause, VideoPlay} from "@element-plus/icons-vue";
import {diseaseIntroduction} from "./diseaseIntroduction.js";
import chinaMap from "../../assets/china.json";


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
const chartType = ref("分布图")

const DiseaseIntroActivePage = ref('1')

const dataAnalyseInProvince = ref(false)

let hitmap

let hitmapChart
let option
let mapOption
let barOption

let lineOption

// 点击具体省份折线图
const showLineChart = (province) => {
    if (dataAnalyseInProvince.value) {
        return
    }
    hitmapChart.showLoading();
    http({
        url: '/data/getDataVaryInDates',
        data: {
            disease: disease.value,
            dataType: dataType.value,
            age: ageRangeValue.value[ageRange.value[0]],
            nextAge: ageRangeValue.value[ageRange.value[1]],
            date: dayjs(monthRange.value[0]).format('YYYY-MM-DD'),
            nextDate: dayjs(monthRange.value[1]).format('YYYY-MM-DD'),
            province: province.name
        },
        method: 'post',
    }).then(dataInProvincesVaryInDates => {
        let data_dates = Object.keys(dataInProvincesVaryInDates)
        let data_values = Object.values(dataInProvincesVaryInDates)
        data_dates = data_dates.map(date => dayjs(date).format('YY年MM月'))
        lineOption = {
            title: {
                text: province.name + ' ' + disease.value + ' 随时间变化折线图'
            },
            xAxis: {
                data: data_dates
            },
            series: [{
                data: data_values
            }]
        }
        lineOption = _.merge(lineOption, fixedLineOption)
        hitmapChart.setOption(lineOption, true)
    })
    dataAnalyseInProvince.value = true
    hitmapChart.hideLoading();
}

// 点击返回**图
const quitAnalyseInProvince = () => {
    hitmapChart.showLoading();
    dataAnalyseInProvince.value = false
    changeChartType()
    hitmapChart.hideLoading();
}

// 时间演变
const varyInDates = () => {
    pauseTheProgress.value = !pauseTheProgress.value
    http({
        url: '/data/getDataInProvincesVaryInDates',
        data: {
            disease: disease.value,
            dataType: dataType.value,
            age: ageRangeValue.value[ageRange.value[0]],
            nextAge: ageRangeValue.value[ageRange.value[1]],
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
            age: ageRangeValue.value[ageRange.value[0]],
            nextAge: ageRangeValue.value[ageRange.value[1]],
            date: dayjs(monthRange.value[0]).format('YYYY-MM-DD'),
            nextDate: dayjs(monthRange.value[1]).format('YYYY-MM-DD')
        },
        method: 'post',
    }).then(drawMap)
}

const changeChartType = () => {
    switch (chartType.value) {
        case '分布图':
            option = mapOption;
            break;
        case '条形图':
            option = barOption;
            break;
    }
    hitmapChart.setOption(option, true);
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
    dataInProvinces = dataInProvinces.slice(-20)
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
    updateChartData();
}


// 更新数据
const updateChartData = () => {
    const mapJson = chinaMap
    hitmapChart.hideLoading();
    echarts.registerMap('China', mapJson);
    switch (chartType.value) {
        case '分布图':
            option = mapOption;
            break;
        case '条形图':
            option = barOption;
            break;
    }
    hitmapChart.setOption(option, true);
}


onMounted(() => {

    // 初始化地图
    hitmap = document.getElementById('hitmap');
    hitmapChart = echarts.init(hitmap);
    hitmapChart.on('click', 'series', showLineChart);

    refresh();

});


</script>

<template>

  <el-container>

    <el-main>

      <div class="page-container">

        <el-card>
          <div class="chart-content">
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
              <el-tooltip
                  class="box-item"
                  effect="light"
                  content="请先点击开始月份再点击结束月份选择时间范围"
                  placement="bottom"
              >
                <div>
                  <el-date-picker
                      v-model="monthRange"
                      type="monthrange"
                      unlink-panels
                      range-separator="到"
                      start-placeholder="起始日期"
                      end-placeholder="结束日期"
                      @change="refresh"
                  />
                </div>
              </el-tooltip>

              <!-- 时间演变按钮 -->
              <el-tooltip
                  class="box-item"
                  effect="light"
                  content="开始时间演变"
                  placement="bottom"
              >
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
              </el-tooltip>

              <!-- 取消省内分析按钮 -->
              <div
                  style="width: 100px"
                  v-if="dataAnalyseInProvince"
              >
                <el-button
                    type="primary"
                    @click="quitAnalyseInProvince"
                >
                  <span>返回{{ chartType }}</span>
                </el-button>
              </div>


              <!-- 图表类型选择 -->
              <div
                  style="width: 100px"
                  v-if="!dataAnalyseInProvince"
              >
                <el-select
                    v-model="chartType"
                    placeholder="Select"
                    @change="changeChartType"
                >
                  <el-option
                      v-for="item in chartTypeOptions"
                      :label="item.label"
                      :value="item.value"
                  />
                </el-select>
              </div>

            </div>

            <!-- 数据地图显示 -->
            <el-card shadow="hover" style="width: 100%;">
              <div id="hitmap" style="width: 800px;height:600px;"></div>
              <!-- 时间范围演变进度条 -->
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
            </el-card>

            <!-- 年龄范围选择 -->
            <div class="row">
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

          </div>
        </el-card>

        <!-- 病种介绍部分 -->
        <div class="introduction-content">
          <el-card style="width: 350px;height: 100%">
            <el-collapse accordion v-model="DiseaseIntroActivePage" style="border: none">

              <el-card class="introduction-content-item" shadow="hover">
                <el-collapse-item name="1">
                  <template #title>
                    <h3>{{ disease }}</h3>
                  </template>
                  <el-scrollbar max-height="380px">
                    {{ diseaseIntroduction.find(item => item.name === disease).briefIntroduction }}
                  </el-scrollbar>
                </el-collapse-item>
              </el-card>

              <el-card class="introduction-content-item" shadow="hover">
                <el-collapse-item name="2">
                  <template #title>
                    <h3>病因</h3>
                  </template>
                  <el-scrollbar max-height="380px">
                    {{ diseaseIntroduction.find(item => item.name === disease).etiology }}
                  </el-scrollbar>
                </el-collapse-item>
              </el-card>

              <el-card class="introduction-content-item" shadow="hover">
                <el-collapse-item name="3">
                  <template #title>
                    <h3>临床表现</h3>
                  </template>
                  <el-scrollbar max-height="380px">
                    {{ diseaseIntroduction.find(item => item.name === disease).clinicalPicture }}
                  </el-scrollbar>
                </el-collapse-item>
              </el-card>

              <el-card class="introduction-content-item" shadow="hover">
                <el-collapse-item name="4">
                  <template #title>
                    <h3>治疗方法</h3>
                  </template>
                  <el-scrollbar max-height="380px">
                    {{ diseaseIntroduction.find(item => item.name === disease).treatment }}
                  </el-scrollbar>
                </el-collapse-item>
              </el-card>

            </el-collapse>
          </el-card>
        </div>

      </div>
    </el-main>

  </el-container>


</template>

<style scoped>

.chart-content {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.page-container {
    display: flex;
    flex-direction: row;
    justify-content: center;
    gap: 10px;
}

.row {
    display: flex;
    justify-content: space-between;
    gap: 10px;
}

.introduction-content-item {
    border: none;
}

</style>
