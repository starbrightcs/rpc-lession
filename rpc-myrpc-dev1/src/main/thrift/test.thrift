// 定义为java语言，对应的包名
namespace java com.starbright.thrift

// 定义对象
struct User {
    1:string name;
}

/*
    执行命令：thrift --gen java test.thrift 生成java语言的代码
    maven插件 maven-thrift-plugin,非官方的插件
*/

