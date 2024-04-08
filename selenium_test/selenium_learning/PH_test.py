import time

from selenium.common import ElementNotInteractableException

import selenium_test.selenium_adapter.page as page

classification_names = ["传染病", "慢性非传染性疾病", "健康危险因素", "生命登记", "基本信息", "其他"]
page.open_page("https://www.phsciencedata.cn/Share/edtShareNew.jsp")

database_classifications = page.find_elements_by_class_name("checkClass")


def click_database_classification(classification_name):
    for database_classification in database_classifications:
        if database_classification.text == classification_name:
            database_classification.click()
            time.sleep(1)
            try:
                more_classification = page.find_element_by_class_name("checkClass12")
                more_classification.click()
            except ElementNotInteractableException:
                pass
            return [
                element for element in
                page.find_elements_by_class_name("checkClass1") + page.find_elements_by_class_name("checkClass11")
                if getattr(element, 'text', None) is not None and element.text.strip() != ''
            ]


database_names = click_database_classification(classification_names[0])
names = []
for database_name in database_names:
    names.append(database_name.text)

print("\n")


database_names = click_database_classification(classification_names[1])
for database_name in database_names:
    names.append(database_name.text)

print(names)

page.close_page()
