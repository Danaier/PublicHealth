from seleniumwire import webdriver

page: webdriver.Chrome = webdriver.Chrome()


def open_page(url):
    page.get(url)


def find_element_by_name(name):
    return page.find_element("name", name)


def find_element_by_id(id):
    return page.find_element("id", id)


def find_element_by_class_name(class_name):
    return page.find_element("class name", class_name)


def find_elements_by_class_name(class_name):
    return page.find_elements("class name", class_name)


def find_element_by_title(title):
    return page.find_element("title", title)


def find_element_by_xpath(xpath):
    return page.find_element("xpath", xpath)


def close_page():
    page.quit()


def function(func_name, *args):
    func = globals().get(func_name)
    if func:
        return func(*args)
    else:
        return f"Function '{func_name}' not found!"
