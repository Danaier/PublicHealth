import json
from collections import defaultdict

import ddddocr
from selenium.webdriver.common.by import By
from selenium.webdriver.support.wait import WebDriverWait

from public_health_data_crawler.selenium_adapter.HiddenPrints import HiddenPrints
from selenium.common import UnexpectedAlertPresentException
from selenium.webdriver.support import expected_conditions as EC

import public_health_data_crawler.selenium_adapter.Page
from public_health_data_crawler.selenium_adapter.util import *

# 控件定位
data_query_url = "https://www.phsciencedata.cn/Share/edtShareNew.jsp"
login_btn_xpath = "//*[@id=\"li1\"]/a[1]"
user_center_btn_xpath = "//*[@id=\"navul\"]/li[8]/a[1]"
username_input_id = "tx_userName"
password_input_id = "tx_password"
more_classification_btn_class_name = "checkClass12"
classification_name_btn_class_name1 = "checkClass1"
classification_name_btn_class_name2 = "checkClass11"
code_img_id = "tokenImage"
code_img_input_id = "tx_tokenValue"
login_btn_id = "btn_logon"  # 真逆天吧，这登录是logon，写这个代码的不用review吗？
refresh_img_xpath = '//*[@id="mima"]/div[6]/span[2]/img[2]'
search_text_input_id = "searchText"
search_btn_id = "searchList"
add_to_apply_list_btns_class = "btn-jiaru"

# 1.打开页面并登录
page = public_health_data_crawler.selenium_adapter.Page.Page()
page.open_page(data_query_url)
login_button = page.find_element_by_xpath(login_btn_xpath)
click(login_button)
page.find_element_by_id(username_input_id).send_keys("danaier")
page.find_element_by_id(password_input_id).send_keys("aier2333")

# try:
#     wait = WebDriverWait(page.page, timeout=15)  # 等待手动输入验证码
#     wait.until(EC.element_to_be_clickable((By.XPATH, user_center_btn_xpath)))  # 个人中心
# except Exception as e:
#     print("手动输入验证码失败:", e)
# page.open_page(data_query_url)  # 重新打开页面
# time.sleep(2)


ocr = ddddocr.DdddOcr()
code_img_input = page.find_element_by_id(code_img_input_id)
login_btn = page.find_element_by_id(login_btn_id)
refresh_img_button = page.find_element_by_xpath(refresh_img_xpath)

# 尝试识别验证码，如不成功则重新刷新验证码
while True:
    # 刷新验证码图片
    del page.driver.requests
    click(refresh_img_button)
    code_img_src = (page
                    .find_element_by_id(code_img_id)
                    .get_attribute("src").split('#')
                    )[0]
    print(code_img_src)
    # 保存验证码图片
    for r in page.driver.iter_requests():
        if r.url == code_img_src:
            print("拦截到验证码图片")
            print(r.response)
            with open('../resources/code-img/code-img.jpg', 'wb') as f:
                f.write(r.response.body)
            break
    # 识别验证码
    with open('../resources/code-img/code-img.jpg', 'rb') as f:
        img_bytes = f.read()
    result = ocr.classification(img_bytes)
    print("使用图像识别得到验证码为："+result)
    code_img_input.clear()
    code_img_input.send_keys(result)
    click(login_btn)
    try:
        wait = WebDriverWait(page.driver, timeout=20)  # 等待手动输入验证码
        wait.until(EC.element_to_be_clickable((By.ID, login_btn_id)))  # 登录按钮
    except UnexpectedAlertPresentException as e:
        print("验证码错误:" + e.msg)
        while EC.alert_is_present()(page.driver):  # 循环检测，可应对数量不定弹窗
            result = EC.alert_is_present()(page.driver)
            print(result.text)
            result.accept()
        continue
    break
print("登录成功")

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


# 最后.关闭页面
time.sleep(30)
page.close_page()
