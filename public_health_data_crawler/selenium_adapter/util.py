from selenium.common import ElementNotInteractableException
import time

provinces = [
    '北京', '天津', '河北', '山西', '内蒙古',
    '辽宁', '吉林', '黑龙江', '上海', '江苏',
    '浙江', '安徽', '福建', '江西', '山东',
    '河南', '湖北', '湖南', '广东', '广西',
    '海南', '重庆', '四川', '贵州', '云南',
    '西藏', '陕西', '甘肃', '青海', '宁夏',
    '新疆'
]


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


def get_img_path_from_url(url):
    return f"../resources/{url.split('/')[-1].split('?')[0]}"


def response_interceptor(request, response):
    content_type = response.headers['Content-Type']
    print('拦截到链接:', request.url)
    if request.host == 'phsciencedata.cn' and content_type and 'image' in content_type:
        print('拦截到图片:', request.url)
        with open(get_img_path_from_url(request.url), 'wb') as f:
            f.write(response.body)

