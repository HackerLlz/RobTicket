# RobTicket
模拟12306订票网站，实现抢票功能和抢票任务的监控，作为毕设

## 使用准备
将mysql包下的数据导入数据库并修改项目数据库连接
修改applicatin.yml里的tessdataPath
启动项目前启动redis server
若要发送邮件功能需要配置mail.property的参数
若要训练验证码图库需要修改applicatin.yml里的codeImagePath
