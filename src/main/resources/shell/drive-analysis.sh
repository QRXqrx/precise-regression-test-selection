#!/bin/bash

# 两个参数，一个zip参数，一个解压完的文件夹名称

# Debug
echo "zip path: $1"
echo "folder path: $2"
echo "workplace: $3"

# 先移动到上传文件的文件夹
cd $3
# 解压文件
# 如果这个已经有同名文件夹，那么先删除
if [[ -d $2 ]]; then
  rm -rf $2
fi

unzip $1
# rm -rf $1 # 暂时先不删除，正常流程中解压完毕是要删除zip的。由于数据库数据已经改变，这个zip也不能直接找到了。

cd $2
# mvn命令创建target文件夹，需要包含项目所有的.class文件
# 先clean删除原有的target，再test更新
mvn clean test

