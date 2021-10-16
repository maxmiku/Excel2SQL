# excel上传接口返回数据

```json
{
    "fileid":"文件在服务器中的id",
   	"uploadStatus":0
}
```

# 拉取表头

```json
{
    "fileid":""
}
```



```json
{
	"excelRows":123,//有效数据的行数
	"fheader"://文件的头信息
	[
		{"id":0,"n":"name该键的名称","t":"type 该键类型","r":"recommand推荐对应的id -1为没有","c":"类型中文"},
		{"id":1,"n":"......."}
	],
	"dfield"://数据库的字段名
	[
		{"id":"字段id","n":"name 字段名","t":"type 类型","c":"类型中文","cn":"字段解释","d(可选 出现时该字段可用)":"默认数据"},
		{}
	]
}
```

# 设置对应表头并拉取示例

## **注意舍弃的列也要传,只不过tid为-1**

```json
{
    "allData":false,//全部数据的示例
    "allowDefault":true,//允许使用默认列数据
    "fieldPair":[
        {
            "sid":0,//文件中的列id
            "tid":2,//字段的id -1为不导入
            "n":"excel中的表头名"
        },{
        	//...
        }
    ]
}
```

```json
{
    "headData":[//表头数据
        
    ],
    "formData":[//表格中的数据
        
    ],
    "fileid":"",
    "fieldPairid":"字段对的id 随机生成的8位字符串 存在session中"
}
```

错误:

-1表中没有数据

-2无法打开表



# 确认字段设置

```json
{
    "fileid":"",
    "fieldPairid":"字段匹配表的确认id",
    "confirm":true,
    "update":true,//有数据则更新 没有数据则插入
    "errInterrupt":false,//遇错终止
    "month":201001//数据月份
}
```

```json
{
    "confirmStatus":0//确定的结果 -1 fileid已在转换或者已转换 --> 前端显示进度    -2删不掉进度-->建议重试  -3 处理停止
}
```





# 获取进度

```json
{
    "fileid":""
}
```

```json
{
    "fileid":"",
    "process":-2.0,//Double 100为最大 -1.0为找不到 -2.0队列列中未开始 -3.0导入结束 -4.0遇到致命错误
    "ec":0,//已出现错误计数
    "ql":0,//前方队列长度,仅在process=-2的时候出现
    "importResult":{progress里面的对象}
}
```

