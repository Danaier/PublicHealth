import ddddocr
from selenium.common import UnexpectedAlertPresentException
from selenium.webdriver.common.by import By
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


from public_health_data_crawler.selenium_adapter.element_position import *
from public_health_data_crawler.selenium_adapter.util import click


def login_to_phsdc(page):
    login_button = page.find_element_by_xpath(login_btn_xpath)
    click(login_button)
    page.find_element_by_id(username_input_id).send_keys("danaier")
    page.find_element_by_id(password_input_id).send_keys("aier2333")
    ocr = ddddocr.DdddOcr()
    code_img_input = page.find_element_by_id(code_img_input_id)
    login_btn = page.find_element_by_id(login_btn_id)
    refresh_img_button = page.find_element_by_xpath(refresh_img_xpath)

    # 尝试识别验证码，如不成功则重新刷新验证码
    while True:
        # 1.刷新验证码图片
        del page.driver.requests
        click(refresh_img_button)
        code_img_src = (page
                        .find_element_by_id(code_img_id)
                        .get_attribute("src").split('#')
                        )[0]
        print(code_img_src)
        # 2.保存验证码图片
        for r in page.driver.iter_requests():
            if r.url == code_img_src:
                print("拦截到验证码图片")
                print(r.response)
                with open('../resources/code-img/code-img.jpg', 'wb') as f:
                    f.write(r.response.body)
                break
        # 3.识别验证码
        with open('../resources/code-img/code-img.jpg', 'rb') as f:
            img_bytes = f.read()
        result = ocr.classification(img_bytes)
        print("使用图像识别得到验证码为："+result)
        code_img_input.clear()
        code_img_input.send_keys(result)
        # 4.尝试登录
        click(login_btn)
        try:
            wait = WebDriverWait(page.driver, timeout=20)  # 等待手动输入验证码
            wait.until(
                EC.element_to_be_clickable((By.ID, login_btn_id))
            )  # 为什么这里是登录按钮？因为登录成功后会跳转到首页，此时登录按钮会重新出现
        except UnexpectedAlertPresentException as e:
            print("验证码错误:" + e.msg)
            while EC.alert_is_present()(page.driver):  # 循环检测，可应对数量不定弹窗
                result = EC.alert_is_present()(page.driver)
                print(result.text)
                result.accept()
            continue
        break
    print("登录成功")
