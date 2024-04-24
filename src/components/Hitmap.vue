<script setup>
import {onMounted} from 'vue'
import * as echarts from 'echarts';
import axios from "axios";
import http from '../utils/request.js';

onMounted(() => {

  // 初始化地图
  const hitmap = document.getElementById('hitmap');
  const hitmapChart = echarts.init(hitmap);
  hitmapChart.showLoading();

  let findMax = (max, currentValue) => {
    return max.value > currentValue.value ? max : currentValue;
  }
  let findMin = (min, currentValue) => {
    return min.value < currentValue.value ? min : currentValue;
  }

  let disease = '百日咳'
  let dataType = 'cases_data'
  let dataInProvinces
  let option
  http({
    url: '/data/getDataInProvinces',
    data: {
      disease: disease,
      dataType: dataType,
      age: '',
      nextAge: '',
      date: '2005-01-01',
      nextDate: '2008-01-01'
    },
    method: 'post',
  }).then(data => {
    console.log(data);
    dataInProvinces = data;
    console.log(dataInProvinces.reduce(findMin, dataInProvinces[0]))
    option = {
      title: {
          text: disease,
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
        min: dataInProvinces.reduce(findMin, dataInProvinces[0]).value,
        max: dataInProvinces.reduce(findMax, dataInProvinces[0]).value,
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
          dataView: { readOnly: false },
          restore: {},
          saveAsImage: {}
        }
      },
      series: [
        {
          name: disease,
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
    axios.get('https://geojson.cn/api/data/china.json').then(showMap);
  })



  // 显示地图
  let showMap = response => {
    const mapJson = response.data;
    hitmapChart.hideLoading();
    echarts.registerMap('China', mapJson);
    hitmapChart.setOption(option);
  }





});




</script>

<template>
  <div id="hitmap" style="width: 800px;height:600px;"></div>
</template>

<style scoped>

</style>
