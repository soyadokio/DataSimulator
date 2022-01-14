```
= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
2022/01/13更新：
可预期的未来再用不上这个工具了，那么也没有优化和升级的必要了，索性便归档了。
= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
```



# DataSimulator

[![SoyaDokio's GitHub stats](https://github-readme-stats.vercel.app/api?username=sdokio&show_icons=true&bg_color=fff&hide_title=true&icon_color=0366d6&text_color=24292e&include_all_commits=true)](https://github.com/anuraghazra/github-readme-stats)

## :point_right: 介绍
按 *数据表描述文件* 生成可插入/导入数据库的模拟数据记录，主要用作数据测试。

p.s. 目前尚未开发一键导入数据库功能。

## :point_right: 获取
最新版（DataSimulator_v1.0.5） [下载](https://github.com/SoyaDokio/DataSimulator/releases/download/v1.0.5/DataSimulator_v1.0.5.jar) 。

各版本详见 [Release](https://github.com/SoyaDokio/DataSimulator/releases) 页面。

## :point_right: 使用
1. 双击运行`DataSimulator_v*.*.*.jar`。
2. （非首次运行跳过此步骤）首次运行后无任何提示，但会在同级（子级）目录下生成下列3个文件：
```
.\log\log.yyyy-MM-dd.log    // 运行日志
.\tableinfo.conf            // 数据表描述文件（默认的配置文件）
.\person.sql                // 使用默认 数据表描述文件 生成的 SQL Script
```
3. 按[规范](https://github.com/SoyaDokio/DataSimulator#user-content-point_right-格式--规范)和需求修改`.\tableinfo.conf`文件。
4. 再次双击运行`DataSimulator_v*.*.*.jar`。
5. 同级目录下会生成SQL Script `.\table_name.sql`。

## :point_right: 格式 & 规范
```
table:table_name_1 rows
field_name_1 field_type_1 [min,max)|{val_1,val_2,...,val_n} pk
field_name_2 field_type_2 [min,max)|{val_1,val_2,...,val_n} nn
...
field_name_n field_type_n [min,max)|{val_1,val_2,...,val_n}

table:table_name_2 rows
field_name_1 field_type_1 [min,max)|{val_1,val_2,...,val_n} pk
field_name_2 field_type_2 [min,max)|{val_1,val_2,...,val_n} nn
...
field_name_n field_type_n [min,max)|{val_1,val_2,...,val_n}

...

table:table_name_n rows
field_name_1 field_type_1 [min,max)|{val_1,val_2,...,val_n} pk
field_name_2 field_type_2 [min,max)|{val_1,val_2,...,val_n} nn
...
field_name_n field_type_n [min,max)|{val_1,val_2,...,val_n}
```

- 以`#`和`//`为起始的行为注释行
- 当 *数据表描述文件* 中单次出现多表（超过1个）的描述时，执行后生成的 SQL Script 文件以第一个表名命名
- 记录的行数为**必填**信息
- 字段的每一项描述信息之间**以空格隔开**
- pk 表示主键，可选
- nn 表示非空，可选
- 字段类型支持：`int`、`long`、`float`、`double`、`string`和`varchar`（`string`和`varchar`效果相同）
- （可选）当字段类型选择`float`或`double`时，可在类型后紧跟`.`加数字，约束保留小数位数
- 对字段取值的约束包括三种情况：`[min,max)`或`{val1,...,valn}`或`无约束`，**最多只可出现一种约束**
- `[min,max)`表示可以取到从min（包含）到max（不包含）的所有值，当主键用此约束时该区间的取值范围应不小于所需生成字段的行数
- `{val1,...,valn}`表示取值集合，当元素为字串时**不添加任何单/双引号**

## :point_right: 实例
现设定*数据表描述文件*内容如下：
```
table:employee 20             // 表名设置为employee，生成20条数据
id int [1,100) pk             // 字段名为id，数据类型为int，取值范围为1（包括）到100（不包括），设为主键
gender varchar {男,女}        // 字段名为gender，数据类型为varchar，取值集合为{'男','女'}
age int [20,60)               // 字段名为age，数据类型为int，取值范围为20（包括）到60（不包括）
salary double.2 (2000,20000]  // 字段名为salary，数据类型为double，取值范围为2000（不包括）到20000（包括），“.2”表示保留两位小数
title varchar {PM,PG,UI}      // 字段名为title，数据类型为varchar，取值集合为{'PM','PG','UI'}
```
所得`employee.sql`内容为：
```sql
insert into employee(id,gender,age,salary,title) values
(75,'女',33,0.053412216308810656,'PM'),
(2,'女',22,0.7373495886901945,'UI'),
(31,'女',29,0.5466897458391717,'PG'),
(81,'男',56,0.15709275796406597,'UI'),
(60,'女',46,0.11366817390196249,'PM'),
(37,'女',50,0.19773740297508902,'UI'),
(20,'女',44,0.7869982163966656,'PM'),
(65,'男',29,0.7024254510631527,'PG'),
(80,'男',28,0.5929413550962656,'PG'),
(93,'女',42,0.32266202529378485,'UI'),
(46,'男',44,0.6921483413816466,'PG'),
(26,'女',32,0.4350518267144764,'UI'),
(52,'男',38,0.8919574955689352,'PM'),
(72,'男',30,0.972047441269007,'PM'),
(53,'男',48,0.30833638137585206,'UI'),
(82,'女',30,0.6349515457561012,'PM'),
(35,'女',49,0.5818325137221259,'UI'),
(58,'女',41,0.23495425464630737,'UI'),
(69,'女',42,0.8938033638341923,'UI'),
(71,'女',20,0.979530811769143,'PG');
```
