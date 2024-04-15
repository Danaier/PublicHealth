from public_health_data_crawler.data_to_download.apply_for_data import apply_for_data
from public_health_data_crawler.selenium_adapter.HiddenPrints import HiddenPrints
import public_health_data_crawler.selenium_adapter.Page
from public_health_data_crawler.selenium_adapter.element_position import *
from public_health_data_crawler.selenium_adapter.login_to_phsdc import login_to_phsdc
from public_health_data_crawler.selenium_adapter.util import *

# 1.登录
page = public_health_data_crawler.selenium_adapter.Page.Page()
page.open_page(data_query_url)
login_to_phsdc(page)

# 3.打开个人中心并申请数据
while apply_for_data(page):
    pass

time.sleep(6)
page.close_page()
