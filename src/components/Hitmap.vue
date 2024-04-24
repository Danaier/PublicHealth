<script setup>
import {onMounted} from 'vue'
import * as echarts from 'echarts';
import axios from "axios";

onMounted(() => {

  // 初始化地图
  const hitmap = document.getElementById('hitmap');
  const hitmapChart = echarts.init(hitmap);
  hitmapChart.showLoading();

  let disease = '病种'
  let showMap = response => {
    const mapJson = response.data;
    hitmapChart.hideLoading();
    echarts.registerMap('China', mapJson);
    let option = {
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
        min: 500000,
        max: 38000000,
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
          name: '公共卫生数据',
          type: 'map',
          roam: true,
          map: 'China',
          emphasis: {
            label: {
              show: true
            }
          },
          data: [
            { name: '河北', value: 4822023 }
          ]
        }
      ]
    };
    hitmapChart.setOption(option);
  }
  axios.get('https://geojson.cn/api/data/china.json').then(showMap);


});




</script>

<template>
  <div id="hitmap" style="width: 800px;height:600px;"></div>
</template>

<style scoped>

</style>
