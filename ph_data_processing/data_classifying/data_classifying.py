import re
import zipfile
import rarfile
import shutil

from ph_data_processing.util.root_path import root_path

print(root_path)
resources_path = root_path / 'resources'
downloaded_data_path = resources_path / 'downloaded-data'
classified_data_path = resources_path / 'classified-data'


# 1.统一格式
def unify_format():
    for file in downloaded_data_path.iterdir():
        file_name = file.name

        # 判断字符串是否以指定子字符串结尾
        if (file_name.endswith("分年龄组按月统计数据.zip")
                or file_name.endswith("分年龄组按月统计数据.rar")
                or file_name.endswith("分年龄组分月统计数据.zip")
                or file_name.endswith("分年龄组分月统计数据.rar")):
            if file_name.endswith("分年龄组分月统计数据.rar"):
                # "分年龄组分月统计数据.rar"改为"分年龄组按月统计数据.rar"
                last_index = file_name.rfind("分年龄组分月统计数据.rar")
                new_filename = file_name[:last_index] + "分年龄组按月统计数据.rar"
                try:
                    file.rename(file.with_name(new_filename))
                    print("分年龄组分月统计数据.rar 改为 分年龄组按月统计数据.rar")
                except FileNotFoundError:
                    print("文件不存在")
                except PermissionError:
                    print("没有权限重命名文件")
                except Exception as e:
                    print("重命名文件时发生错误:", e)
            if file_name.endswith("分年龄组分月统计数据.zip"):
                # "分年龄组分月统计数据.zip"改为"分年龄组按月统计数据.zip"
                last_index = file_name.rfind("分年龄组分月统计数据.zip")
                new_filename = file_name[:last_index] + "分年龄组按月统计数据.zip"
                try:
                    file.rename(file.with_name(new_filename))
                    print("分年龄组分月统计数据.zip 改为 分年龄组按月统计数据.zip")
                except FileNotFoundError:
                    print("文件不存在")
                except PermissionError:
                    print("没有权限重命名文件")
                except Exception as e:
                    print("重命名文件时发生错误:", e)
        else:
            # 删除冗余文件
            try:
                file.unlink()
                print("文件删除成功")
            except FileNotFoundError:
                print("文件不存在")
            except PermissionError:
                print("没有权限删除文件")
            except Exception as e:
                print("删除文件时发生错误:", e)


# 2.分类数据
def move_data():
    for file in downloaded_data_path.iterdir():
        file_name = file.name
        result = re.split('年至|分', file_name)
        province = result[0][:-4]
        disease_type = result[1][5:]
        print("省份:", province, "    疾病类型:", disease_type)
        if disease_type not in [disease_type_path.name for disease_type_path in classified_data_path.iterdir()]:
            (classified_data_path / disease_type).mkdir()
        destination_file = classified_data_path / disease_type / file_name
        try:
            shutil.copy(file, destination_file)
            print("文件复制成功")
        except FileNotFoundError:
            print("文件不存在")
        except PermissionError:
            print("没有权限复制文件")
        except Exception as e:
            print("复制文件时发生错误:", e)


# 3.重命名数据
def rename_data():
    for disease_type_path in classified_data_path.iterdir():
        for file in disease_type_path.iterdir():
            file_name = file.name
            result = re.split('年至|分', file_name)
            province = result[0][:-4]
            disease_type = result[1][5:]
            print("省份:", province, "    疾病类型:", disease_type)
            new_file_name = province + '-' + disease_type + file.suffix
            print("新文件名:", new_file_name)
            try:
                file.rename(file.with_name(new_file_name))
                print("文件重命名成功")
            except FileNotFoundError:
                print("文件不存在")
            except PermissionError:
                print("没有权限重命名文件")
            except Exception as e:
                print("重命名文件时发生错误:", e)


# 4.解压数据
def unzip_data():
    for disease_type_path in classified_data_path.iterdir():
        for compressed_file in disease_type_path.iterdir():
            compressed_file_suffix = compressed_file.suffix
            # 对rar文件夹进行标识，因为rar下的xls文件与zip下的格式不一样
            if compressed_file_suffix == ".rar":
                disease_type_path = compressed_file.parent
                if not disease_type_path.name.endswith('-rar'):
                    disease_type_path.rename(disease_type_path.with_name(disease_type_path.name + '-rar'))
                break
            if compressed_file_suffix == ".zip":
                working_dir = (compressed_file.parent/compressed_file.stem)
                working_dir.mkdir()
                with zipfile.ZipFile(compressed_file, 'r') as zip_ref:
                    zip_ref.extractall(working_dir)
                for extracted_file in working_dir.iterdir():
                    new_file_name = (compressed_file.parent / (compressed_file.stem+extracted_file.suffix))
                    extracted_file.rename(new_file_name)
                compressed_file.unlink()
                working_dir.rmdir()


unify_format()
move_data()
rename_data()
unzip_data()
