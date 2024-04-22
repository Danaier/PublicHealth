from datetime import datetime
from ph_data_processing.util.root_path import root_path
import pandas as pd

import pymysql

resources_path = root_path / 'resources'
downloaded_data_path = resources_path / 'downloaded-data'
classified_data_path = resources_path / 'classified-data'


# 连接数据库
def connect_to_database():
    connection = pymysql.connect(host='localhost',
                                 user='aier',
                                 password='huangdan',
                                 db='public_health_data',
                                 charset='utf8',
    )
    return connection

# 插入数据
def insert_data(connection, disease, specific_disease, province, data_type, age, date, value):
    try:
        with connection.cursor() as cursor:
            sql = "INSERT INTO public_health_data (disease, specific_disease, province, type, age, date, value) VALUES (%s, %s, %s, %s, %s, %s, %s)"
            cursor.execute(sql, (disease, specific_disease, province, data_type, age, date, value))
        connection.commit()
    except Exception as e:
        print("Error inserting data:", e)

# 解析日期字符串
def parse_date(date_str):
    # 解析日期字符串，这里假设日期字符串格式为 "2005年1月"
    parts = date_str.split('年')
    year = int(parts[0])
    month = int(parts[1].rstrip('月'))
    # 返回日期对象
    return datetime(year, month, 1)  # 这里假设日期都是月初

def test_insert():
    # 测试插入数据
    insert_data(connect_to_database(),
                '伤寒与副伤寒',
                '伤寒',
                '广东省',
                '发病率',
                '15-',
                parse_date('2005年1月'),
                0.0001)

def storage():
    connection = connect_to_database()
    for disease_path in classified_data_path.iterdir():
        disease = disease_path.name
        for province_file in disease_path.iterdir():
            province = province_file.name.split('-')[0]
            print(f"处理 {province} 的 {disease}")
            xls_file = pd.read_excel(province_file, header=[0, 1])
            columns = [column for column in xls_file.columns]
            for index, row in xls_file.iterrows():
                age = row[columns[0]]
                date = row[columns[1]]
                for column in columns[2:]:
                    specific_disease = column[0]
                    data_type = column[1]
                    value = row[column]
                    if not value == '-':
                        insert_data(connection, disease, specific_disease, province, data_type, age, parse_date(date), value)


# test_insert()
storage()