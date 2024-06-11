#!/bin/bash

RESULT=$1

if [ "$RESULT" = "true" ]; then
  RESULT="成功";
else
  RESULT="失败";
fi

MESSAGE="GitLab CI已完成执行，执行结果为：$RESULT！"

# 示例：发送HTTP POST请求到钉钉机器人通知群
curl -H "Content-Type: application/json" -X POST \
-d '{"msgtype": "text", "text": {"content": "Lodsve '"$MESSAGE"'"}}' \
https://oapi.dingtalk.com/robot/send?access_token=${env.DING_TOKEN}
