from selenium.webdriver.support.ui import Select
import json
from collections import defaultdict
from selenium.webdriver.support import expected_conditions as EC

from public_health_data_crawler.selenium_adapter.element_position import *
from public_health_data_crawler.selenium_adapter.util import *


def download_data(page):
    # 1.加载申请进度
    with open("../resources/downloaded_data.json", 'r', encoding='utf-8') as downloaded_data_file:
        downloaded_data_dict = defaultdict(list, json.load(downloaded_data_file))
    page.open_page(data_download_url)  # 重新打开页面
    time.sleep(2)
    click(page.find_element_by_id(data_to_download_list_btn_id))  # 点击“已完成”
    time.sleep(2)
    current_page = max(int(key) for key in downloaded_data_dict.keys())  # 获取已申请进度
    page.find_element_by_id(page_num_input_id).send_keys(str(current_page))  # 输入页数
    click([item for item in page.find_li_elements_by_parent_id(page_num_table_id)
           if text(item) == "跳转"][0])  # 点击“跳转”
    # 3.将当前页的数据集下载
    paged_download_list = page.find_li_elements_by_parent_id(paged_download_list_id)  # 获取当前页的数据集
    for paged_download_data in paged_download_list:
        paged_download_data_name = text(paged_download_data)
        if paged_download_data_name not in downloaded_data_dict[str(current_page)]:
            buttons = paged_download_data.find_elements("tag name", "input")
            for button in buttons:
                if button.get_attribute("value") == "下载":
                    click(button)  # 点击下载

            # 4.更新已申请进度
            downloaded_data_dict[str(current_page)].append(paged_download_data_name)
            with open("../resources/downloaded_data.json", 'w', encoding='utf-8') as applied_data_file:
                json.dump(downloaded_data_dict, applied_data_file, ensure_ascii=False)
            return True
    # 5.当前页的数据集已全部申请，跳转到下一页
    downloaded_data_dict[str(current_page + 1)] = []
    with open("../resources/downloaded_data.json", 'w', encoding='utf-8') as applied_data_file:
        json.dump(downloaded_data_dict, applied_data_file, ensure_ascii=False)
    return current_page < 182
