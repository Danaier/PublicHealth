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
