from seleniumwire import webdriver

from public_health_data_crawler.selenium_adapter.ph_data_crawler_root_path import root_path


def function(func_name, *args):
    func = globals().get(func_name)
    if func:
        return func(*args)
    else:
        return f"Function '{func_name}' not found!"


class Page:

    def __init__(self, *args, **kwargs):
        options = webdriver.ChromeOptions()
        prefs = {'profile.default_content_settings.popups': 2,
                 'download.default_directory': root_path+'\\resources\\downloaded-data\\'}
        print(root_path+'\\resources\\downloaded-data\\')
        options.add_experimental_option('prefs', prefs)
        self.driver = webdriver.Chrome(options=options)

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

    def find_element_by_element_name(self, name):
        return self.driver.find_element("name", name)

    def find_elements_by_parent_id_and_tag(self, id, tag):
        return self.driver.find_element("id", id).find_elements("tag name", tag)

    def find_li_elements_by_parent_id(self, id):
        return self.find_elements_by_parent_id_and_tag(id, "li")

    def find_input_elements_by_parent_id(self, id):
        return self.find_elements_by_parent_id_and_tag(id, "li")

    def close_page(self):
        self.driver.quit()
