### 面向公共卫生事件的数据抽取和可视化

#### 面向公共卫生事件的数据抽取

> 抽取多源的公共卫生数据是对公共卫生事件进行建模和认知的前提
>
> 利用爬虫、手动整理等手段，对多源的公共卫生数据进行获取

**数据来源**：公共卫生科学数据中心

公共卫生科学数据中心平台上，对于每一种病症，都有各省份05年至20年对于**各个年龄段/各个月份**的，**发病人数/发病率/死亡人数/死亡率** 四种数据的归档。本项目的`PHDataCrawler`分支上的项目致力于这些数据，`PHDataProcessing`分支解决了这些数据的处理

#### 面向公共卫生事件的数据可视化

**前端**：Vue3/Vite/Element-plus/Echart 项目位于`PHDataFront`上

**后端**：SpringKotlin/Gradle/ktorm/SpringRedis 项目位于`PHDataApi`上

**运维**：Docker/docker-compose/Nginx/MySQL/Redis 全部使用docker部署 
