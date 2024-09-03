# 单行注释
// 单行注释
/*
    多行注释
 */

// 指定生成代码的包
namespace java com.starbright

struct User {
    required string name,
    optional i32 age,
    list<i32> arr = [1,2,3,4]
}

enum SEASON {
	SPRING = 1,
	SUMMER = 2,
}

exception MyException {
    1:i32 errorCode,
    2:string errorMsg
}

service UserService {
    bool login(1:string username, 2:string password) throws (1: MyException e,)
    oneway void register(1:User user) // user是idl语言的结构体
}

service BaseService {
    void method(1:string type)
}

service OrderService extends BaseService {

}
