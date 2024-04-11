import json
import time

from selenium.webdriver.common.by import By
from selenium.webdriver.support.wait import WebDriverWait
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

# 1.打开页面并登录
page = public_health_data_crawler.selenium_adapter.Page.Page()
page.response_interceptor = response_interceptor  # 设置验证码图片拦截器
del page.driver.requests
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

code_img_src = page.find_element_by_id(code_img_id).get_attribute("src")
for r in page.driver.iter_requests():
    print(r)
    if r.url == code_img_src:
        with open('../resources/code-img/code-img.jpg', 'wb') as f:
            f.write(r.response.body)

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
for database_name in database_names:
    print(text(database_name))

# 4.获取申请列表
with open("../resources/apply_list.json", 'r', encoding='utf-8') as apply_list_file:
    apply_list = json.load(apply_list_file)
print(apply_list)

# 5.将未加入申请列表的数据库名称加入申请列表


# 6.更新申请列表
with open("../resources/apply_list.json", 'w', encoding='utf-8') as apply_list_file:
    apply_list = {'白喉': ['湖南', '江西']}
    json.dump(apply_list, apply_list_file, ensure_ascii=False)

# 最后.关闭页面
time.sleep(30)
page.close_page()
