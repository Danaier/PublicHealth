from seleniumwire import webdriver


def function(func_name, *args):
    func = globals().get(func_name)
    if func:
        return func(*args)
    else:
        return f"Function '{func_name}' not found!"


class Page(webdriver.Chrome):

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.driver = webdriver.Chrome(*args, **kwargs)

    def open_page(self, url):
        self.driver.get(url)

    def find_element_by_name(self, name):
        return self.driver.find_element("name", name)

    def find_element_by_id(self, id):
        return self.driver.find_element("id", id)

    def find_element_by_class_name(self, class_name):
        return self.driver.find_element("class name", class_name)

    def find_elements_by_class_name(self, class_name):
        return self.driver.find_elements("class name", class_name)

    def find_element_by_title(self, title):
        return self.driver.find_element("title", title)

    def find_element_by_xpath(self, xpath):
        return self.driver.find_element("xpath", xpath)

    def close_page(self):
        self.driver.quit()
