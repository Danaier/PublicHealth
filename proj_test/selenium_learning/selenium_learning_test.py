import time

from selenium.webdriver import Keys
import proj_test.selenium_adapter.page as page


# Navigate to the application home page
page.open_page("https://www.google.com")

# get the search textbox
search_field = page.find_element_by_name("q")
search_field.send_keys("Python")
search_field.send_keys(Keys.ENTER)

time.sleep(20)

# Close the browser window
page.close_page()
