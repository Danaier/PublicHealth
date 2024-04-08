import public_health_data_crawler.selenium_adapter.page as page
from public_health_data_crawler.selenium_adapter.util import *

# 1.打开页面
page.open_page("https://www.phsciencedata.cn/Share/edtShareNew.jsp")
print("打开页面成功")


# 2.点击“更多”按钮展开更多数据库名称
more_classification = page.find_element_by_class_name("checkClass12")
click(more_classification)


# 3.获取所有数据库名称
database_names = [
    element
    for element in
    page.find_elements_by_class_name("checkClass1") + page.find_elements_by_class_name("checkClass11")
    if text(element)
]
for database_name in database_names:
    print(text(database_name))


# 最后.关闭页面
time.sleep(30)
page.close_page()
