from selenium.common import ElementNotInteractableException
import time


def click(web_element):
    try:
        web_element.click()
        time.sleep(1)
    except ElementNotInteractableException:
        print("点击元素失败")
        pass


def text(web_element):
    return web_element.text \
        if (getattr(web_element, 'text', None) is not None
            and web_element.text.strip() != '') \
        else ""
