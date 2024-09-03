// 这是一个thrift文件
/**
* UserService {
*   User queryUserByNameAndPassword(String name, String password)
*   void save(User user)
*  }
**/

namespace java com.starbright

struct User {
    1:string name,
    2:string password
}

service UserService {

    User queryUserByNameAndPassword(1:string name, 2:string password)

    void save(1:User user)

}
