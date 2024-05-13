// 工具函数
export const findMax = (max, currentValue) => {
    return max.value > currentValue.value ? max : currentValue;
}
export const findMin = (min, currentValue) => {
    return min.value < currentValue.value ? min : currentValue;
}
export const getSum = (total, currentValue) => {
    return total + currentValue.value;
}

export const aggregateAge = (data) => {
    const result = [];
    const map = {};

    data.forEach(item => {
        // 提取数字部分
        const num = parseInt(item.name);
        // 确定新的分组
        let groupName = '';
        if (num >= 0 && num <= 4) {
            groupName = '0-';
        } else if (num >= 5 && num <= 9) {
            groupName = '5-';
        } else {
            groupName = item.name; // 保持原样
        }

        // 累加同组的值
        if (!map[groupName]) {
            map[groupName] = { name: groupName, value: 0 };
        }
        map[groupName].value += item.value;
    });

    // 将map对象转换回数组
    for (const key in map) {
        result.push(map[key]);
    }

    return result;
}
