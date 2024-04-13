import json
from collections import defaultdict
from selenium.webdriver.support import expected_conditions as EC

import public_health_data_crawler.selenium_adapter.Page
from public_health_data_crawler.selenium_adapter.util import *
from public_health_data_crawler.selenium_adapter.element_position import *
from public_health_data_crawler.selenium_adapter.login_to_phsdc import login_to_phsdc


# 1.打开页面并登录
page = public_health_data_crawler.selenium_adapter.Page.Page()
page.open_page(data_query_url)
login_to_phsdc(page)
page.open_page(data_query_url)  # 重新打开页面
time.sleep(2)
print("打开页面成功")

# 2.点击“更多”按钮展开更多数据库名称
more_classification = page.find_element_by_class_name(more_classification_btn_class_name)
click(more_classification)

# 3.获取所有数据库名称
database_names = [
    element
    for element in
    page.find_elements_by_class_name(classification_name_btn_class_name1) +
    page.find_elements_by_class_name(classification_name_btn_class_name2)
    if text(element)
]

# 4.获取申请列表
with open("../resources/apply_list.json", 'r', encoding='utf-8') as apply_list_file:
    apply_list = defaultdict(list, json.load(apply_list_file))
print(apply_list)

# 5.将未加入申请列表的数据库名称加入申请列表
for database_name in database_names:
    for province in provinces:
        if text(database_name) not in apply_list or province not in apply_list[text(database_name)]:
            print(apply_list[text(database_name)])
            print(province)
            click(database_name)
            search_input = page.find_element_by_id(search_text_input_id)
            search_btn = page.find_element_by_id(search_btn_id)
            search_input.clear()
            search_input.send_keys(province)
            click(search_btn)
            add_to_apply_list_btns = page.find_elements_by_class_name(add_to_apply_list_btns_class)
            for add_to_apply_list_btn in add_to_apply_list_btns:
                if '分年龄组按月' in add_to_apply_list_btn.get_attribute("onclick"):
                    click(add_to_apply_list_btn)
                    while EC.alert_is_present()(page.driver):  # 循环检测，可应对数量不定弹窗
                        result = EC.alert_is_present()(page.driver)
                        print(result.text)
                        result.accept()
                    break

            apply_list[text(database_name)].append(province)
            with open("../resources/apply_list.json", 'w', encoding='utf-8') as apply_list_file:
                json.dump(apply_list, apply_list_file, ensure_ascii=False)
            time.sleep(3)


# 6.关闭页面
time.sleep(10)
page.close_page()
