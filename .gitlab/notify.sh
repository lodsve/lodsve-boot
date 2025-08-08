#!/bin/bash

RESULT=$1

if [ "$RESULT" = "true" ]; then
  RESULT="成功";
else
  RESULT="失败";
fi

MESSAGE="GitLab CI已完成执行，执行结果为：$RESULT！"

# 示例：发送HTTP POST请求到飞书机器人通知群
curl --location --request POST 'https://open.feishu.cn/open-apis/bot/v2/hook/e030e934-b084-42e8-af4d-1f5fa7f47926' \
--header 'Content-Type: application/json' \
--data-raw '{
  "msg_type": "interactive",
  "card": {
    "schema": "2.0",
    "config": {
      "update_multi": true,
      "style": {
        "text_size": {
          "normal_v2": {
            "default": "normal",
            "pc": "normal",
            "mobile": "heading"
          }
        }
      }
    },
    "body": {
      "direction": "vertical",
      "padding": "12px 12px 12px 12px",
      "elements": [
        {
          "tag": "markdown",
          "content": "'${MESSAGE}'",
          "text_align": "left",
          "text_size": "normal_v2",
          "margin": "0px 0px 0px 0px"
        },
        {
          "tag": "button",
          "text": {
            "tag": "plain_text",
            "content": "🌞点击查看更多"
          },
          "type": "default",
          "width": "default",
          "size": "medium",
          "behaviors": [
            {
              "type": "open_url",
              "default_url": "https://nexus.lodsve.com:9000/repository/maven-public/com/lodsve/boot",
              "pc_url": "",
              "ios_url": "",
              "android_url": ""
            }
          ],
          "margin": "0px 0px 0px 0px"
        }
      ]
    },
    "header": {
      "title": {
        "tag": "plain_text",
        "content": "Lodsve Gitlab CI执行结果通知"
      },
      "subtitle": {
        "tag": "plain_text",
        "content": ""
      },
      "template": "blue",
      "padding": "12px 12px 12px 12px"
    }
  }
}'
