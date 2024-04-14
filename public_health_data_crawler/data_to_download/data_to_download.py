import json
import time
from collections import defaultdict

from public_health_data_crawler.selenium_adapter.HiddenPrints import HiddenPrints
import public_health_data_crawler.selenium_adapter.Page
from public_health_data_crawler.selenium_adapter.element_position import *
from public_health_data_crawler.selenium_adapter.login_to_phsdc import login_to_phsdc
from public_health_data_crawler.selenium_adapter.util import *

# 1.登录
page = public_health_data_crawler.selenium_adapter.Page.Page()
page.open_page(data_query_url)
login_to_phsdc(page)

# 2.获取已申请数据集
with open("../resources/applied_data.json", 'r', encoding='utf-8') as applied_data_file:
    applied_data_dict = defaultdict(list, json.load(applied_data_file))

# 3.打开个人中心并申请数据
page.open_page(data_download_url)  # 重新打开页面
time.sleep(2)
click(page.find_element_by_id(data_to_apply_list_btn_id))  # 点击“拟申请列表”
current_page = max(int(key) for key in applied_data_dict.keys())  # 获取已申请进度
page.find_element_by_id(page_num_input_id).send_keys(str(current_page))  # 输入页数
click([item for item in page.find_li_elements_by_parent_id(page_num_table_id)
       if text(item) == "跳转"][0])  # 点击“跳转”
paged_apply_list = page.find_li_elements_by_parent_id(paged_apply_list_id)  # 获取当前页的数据集
for paged_apply_data in paged_apply_list:
    paged_apply_data_name = text(paged_apply_data)
    if paged_apply_data_name not in applied_data_dict[str(current_page)]:
        # 将未加入申请列表的数据库名称加入申请列表
        click(paged_apply_data.find_element("tag name", "input"))  # 点击申请数据
        click(page.find_element_by_id(apply_accept_clause_btn_id))  # 勾选“同意条款”
        click(page.find_element_by_id(apply_data_btn_id))  # 点击“申请”

        # 更新已申请进度
        applied_data_dict[str(current_page)].append(paged_apply_data_name)
        with open("../resources/applied_data.json", 'w', encoding='utf-8') as applied_data_file:
            json.dump(applied_data_dict, applied_data_file, ensure_ascii=False)
        break


time.sleep(6)
page.close_page()
