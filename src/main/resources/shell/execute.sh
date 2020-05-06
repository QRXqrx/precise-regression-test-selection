#!/bin/bash

# For debug
echo $1

cd $1
if [ -d $1/target ];then
    rm -rf $1/target
fi

logPath=$1/.mooctest/log
if [ -d "$logPath" ]; then
    rm -rf "$logPath"
fi

mkdir -p "$logPath"
logTxt=$logPath/log.txt
errTxt=$logPath/err.txt

nodePath=$1/.mooctest/node
if [ ! -d $nodePath ]; then
    mkdir -p $nodePath
fi

#time=`sh /lib/sh/venv/timestamp.sh`
#nodeTxt="$nodePath/node."$time


#生成xml
mvn clean clover:setup test clover:aggregate clover:clover > $logTxt 2>&1

#生成coverage.txt, project.txt, color.txt
xmlPath=$1/target/site/clover/clover.xml
#node.txt
coverageJson=$logPath/node.txt
#metanode.txt
projectJson=$logPath/metanode.txt
#color.txt
colorJson=$logPath/color.txt

#java -jar /Users/duomen/MooctestProject/code_snippets_slice/src/main/resources/shell/venv/ParseXml.jar $xmlPath $coverageJson $projectJson $colorJson > $errTxt 2>&1
java -jar /c/Users/QRX/IdeaProjects/node-analyzer/src/main/resources/shell/venv/ParseXml.jar $xmlPath $coverageJson $projectJson $colorJson > $errTxt 2>&1

#将每一次的node输出到node.timestamp
#cat $coverageJson > $nodeTxt 2>&1

#java -jar /Users/duomen/MooctestProject/code_snippets_slice/src/main/resources/shell/venv/jcovUtil.jar $coverageJson $projectJson > $logPath/score.txt 2>&1
java -jar /c/Users/QRX/IdeaProjects/node-analyzer/src/main/resources/shell/venv/jcovUtil.jar $coverageJson $projectJson > $logPath/score.txt 2>&1