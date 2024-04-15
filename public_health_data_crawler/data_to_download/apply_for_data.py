from selenium.webdriver.support.ui import Select
import json
from collections import defaultdict
from selenium.webdriver.support import expected_conditions as EC

from public_health_data_crawler.selenium_adapter.element_position import *
from public_health_data_crawler.selenium_adapter.util import *

paper_title = "面向公共卫生事件的多源数据抽取和可视化"
data_usage_description = "课题：面向公共卫生事件的多源数据抽取和可视化。课题的目标是研发复杂公共卫生事件数据爬取和可视化展示平台，从而支持公共卫生事件的分析和决策。"


def apply_for_data(page):
    # 1.加载申请进度
    with open("../resources/applied_data.json", 'r', encoding='utf-8') as applied_data_file:
        applied_data_dict = defaultdict(list, json.load(applied_data_file))
    # 2.打开拟申请列表页面并跳转到当前进度
    page.open_page(data_download_url)  # 重新打开页面
    time.sleep(2)
    click(page.find_element_by_id(data_to_apply_list_btn_id))  # 点击“拟申请列表”
    current_page = max(int(key) for key in applied_data_dict.keys())  # 获取已申请进度
    page.find_element_by_id(page_num_input_id).send_keys(str(current_page))  # 输入页数
    click([item for item in page.find_li_elements_by_parent_id(page_num_table_id)
           if text(item) == "跳转"][0])  # 点击“跳转”
    # 3.将当前页的数据集申请
    paged_apply_list = page.find_li_elements_by_parent_id(paged_apply_list_id)  # 获取当前页的数据集
    for paged_apply_data in paged_apply_list:
        paged_apply_data_name = text(paged_apply_data)
        if paged_apply_data_name not in applied_data_dict[str(current_page)]:
            # 将未加入申请列表的数据库名称加入申请列表
            click(paged_apply_data.find_element("tag name", "input"))  # 点击申请数据
            click(page.find_element_by_id(apply_accept_clause_btn_id))  # 勾选“同意条款”
            click(page.find_element_by_id(apply_data_btn_id))  # 点击“申请”

            serve_object_type = Select(page.find_element_by_id(serve_object_type_id))
            serve_object_name = page.find_element_by_id(serve_object_name_id)
            data_usage = Select(page.find_element_by_id(data_usage_id))
            data_usage_description_input = page.find_element_by_id(data_usage_description_id)
            paper_title_input = page.find_element_by_id(paper_title_input_id)
            submit_apply_btn = page.find_element_by_id(submit_apply_btn_id)

            serve_object_type.select_by_index(3)  # 选择“用户人员”
            serve_object_name.send_keys("黄丹青")  # 输入“黄丹青”
            data_usage.select_by_index(2)  # 选择“论文撰写”
            data_usage_description_input.send_keys(data_usage_description)  # 输入数据使用说明
            paper_title_input.send_keys(paper_title)  # 输入论文标题
            click(submit_apply_btn)  # 点击“提交申请”

            confirm_submit_btn = page.find_element_by_id(confirm_submit_btn_id)
            click(confirm_submit_btn)
            while EC.alert_is_present()(page.driver):  # 循环检测，可应对数量不定弹窗
                result = EC.alert_is_present()(page.driver)
                print(result.text)
                result.accept()

            # 4.更新已申请进度
            applied_data_dict[str(current_page)].append(paged_apply_data_name)
            with open("../resources/applied_data.json", 'w', encoding='utf-8') as applied_data_file:
                json.dump(applied_data_dict, applied_data_file, ensure_ascii=False)
            return True
    # 5.当前页的数据集已全部申请，跳转到下一页
    applied_data_dict[str(current_page + 1)] = []
    with open("../resources/applied_data.json", 'w', encoding='utf-8') as applied_data_file:
        json.dump(applied_data_dict, applied_data_file, ensure_ascii=False)
    return current_page < 179

