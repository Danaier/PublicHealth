from public_health_data_crawler.selenium_adapter.HiddenPrints import HiddenPrints

import public_health_data_crawler.selenium_adapter.Page
from public_health_data_crawler.selenium_adapter.element_position import *


# 1.打开页面并登录
page = public_health_data_crawler.selenium_adapter.Page.Page()
page.open_page(data_query_url)

