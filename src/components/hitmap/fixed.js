import {ref} from "vue";

export const fixedMapOption = {
    title: {
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
            id: '病种',
            type: 'map',
            roam: true,
            map: 'China',
            emphasis: {
                label: {
                    show: true
                }
            },
            universalTransition: true,
            animationDurationUpdate: 1000,
        }
    ]
};

export const fixedBarOption = {
    xAxis: {
        type: 'value'
    },
    yAxis: {
        type: 'category',
        axisLabel: {
            rotate: 30
        }
    },
    // 鼠标悬浮提示
    tooltip: {
        trigger: 'item',  // 数据项图形触发
        showDelay: 0,  // 显示延迟，添加显示延迟可以避免频繁切换
        transitionDuration: 0.2  // 提示框浮层的移动动画过渡时间，单位是 s，设置为 0 的时候会紧跟着鼠标移动
    },
    color: ['#cd3c3c'],
    animationDurationUpdate: 1000,
    series: {
        type: 'bar',
        id: '病种',
        universalTransition: true
    }
};
export const fixedLineOption = {
    xAxis: {
        type: 'category',
    },
    yAxis: {
        type: 'value'
    },
    series: [
        {
            type: 'line'
        }
    ]
};
export const fixedPieOption = {
    tooltip: {
        trigger: 'item',
    },
    legend: {
        type: 'scroll',
        top: "bottom",
    },
    toolbox: {
        show: false,
    },
    series: [{
        type: 'pie',
        radius: ['20%', '80%'],
        avoidLabelOverlap: false,
        itemStyle: {
            borderRadius: 7,
            borderColor: '#fff',
            borderWidth: 2
        },
        label: {
            show: true,
            position: 'outside'
        },
        emphasis: {
            label: {
                show: true,
                fontSize: 30,
                fontWeight: 'bold'
            }
        },
        labelLine: {
            show: true
        },
    }]
}

export const fixedAgePieOption = {
    tooltip: {
        trigger: 'item',
        formatter: '{b}: {d}%'
    },
    legend: {
        type: 'scroll',
        top: "bottom",
    },
    toolbox: {
        show: false,
    },
    series: [{
        type: 'pie',
        radius: ['20%', '80%'],
        avoidLabelOverlap: false,
        itemStyle: {
            borderRadius: 7,
            borderColor: '#fff',
            borderWidth: 2
        },
        label: {
            show: true,
            position: 'inside'
        },
        emphasis: {
            label: {
                show: true,
                fontSize: 30,
                fontWeight: 'bold'
            }
        },
        labelLine: {
            show: true
        },
    }]
}

export const diseaseOptions = [
    {value: "出血热", label: "出血热"},
    {value: "包虫病", label: "包虫病"},
    {value: "急性出血性结膜炎", label: "急性出血性结膜炎"},
    {value: "手足口病", label: "手足口病"},
    {value: "斑疹伤寒", label: "斑疹伤寒"},
    {value: "流行性感冒", label: "流行性感冒"},
    {value: "流行性腮腺炎", label: "流行性腮腺炎"},
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
];

export const chartTypeOptions = [
    {value: "分布图", label: "分布图"},
    {value: "条形图", label: "条形图"},
    {value: "饼状图", label: "饼状图"},
]

export const ageRangeMarks = ref({
    0: '0岁',
    1: '5岁',
    2: '10岁',
    3: '15岁',
    4: '20岁',
    5: '25岁',
    6: '30岁',
    7: '35岁',
    8: '40岁',
    9: '45岁',
    10: '50岁',
    11: '55岁',
    12: '60岁',
    13: '65岁',
    14: '70岁',
    15: '75岁',
    16: '80岁'
})
export const ageRangeValue = ref({
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
    16: '80-'
})
export const formatAge = (val) => {
    return ageRangeMarks.value[val].replace('-', '岁')
}
