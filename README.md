# 爬虫
## 爬虫步骤
1. 初始化爬虫队列，将初始URl放入队列中，又称之为种子URL，需要爬取的URL都是通过这些种子URL直接或者间接获取的；
2. 从爬虫队列选取一个URL，如果获取成功，进入3，否则队列中没有需要爬的地址，爬虫爬取完毕，直接退出；
3. 获取URL对应的数据，数据格式为HTML、JSON、XML等待；
4. 将获取的数据进行解析，获取我们需要的资源，存入数据库；
5. 在获取的数据中通常会有新的URL出现，将他们以一定的策略放入爬虫队列中；
6. 将该URL标记为已爬，然后重新进入2.

![Alt text](https://github.com/YHGui/NeteaseMusicCrawler/blob/master/images/crawlertmy.png)

- 刚开始学习的时候，通常会做一些爬虫类似的工作，通过爬虫来获取一些数据，爬取网易云音乐评论数据是最开始学习Spring Boot和Spring Data Jpa时小项目。爬取的路径就是：歌单列表->歌单->歌曲。
- 在歌单页，右键歌单“检查”，可以找到其超链接，通过过滤class为".tit.f-thide.s-fc0"来找到有歌单element，它的链接加上基础链接以及其他相关组成得到对应的url，同理也可以通过相应的选择器得到歌单详情页的所有歌曲的url，然后根据单曲url获取其评论数，这里遇到的问题就是评论获取的时候，通过开发者工具找到与评论相关的post请求，包含两个参数：params和encSecKey，网易对其进行了加密，其中params为AES对称加密后的参数，encSecKey为RSA加密后的AES密钥。
同时为了提高爬虫效率，使用ExecutorService来实现多线程爬虫，最大线程池数量为20，同时加入了页面缓存和定时更新功能，每天定时更新评论数超过5000歌曲评论数。
- Jsoup是一款Java的HTML的解析器，可直接解析某个URL地址，HTML文本内容，从URL，文件或字符串中解析HTML，使用DOM或CSS选择器来查找、取出数据。
- 其中包括两个实体类，一个是WebPage，用来存储页面相关的信息，url，页面类型，是否被爬过，另一个是Song，存储歌曲相关信息，url，歌曲名，评论数。

## 技术栈
- Spring Boot
- Spring Data Jpa
- Encache
- Lombok