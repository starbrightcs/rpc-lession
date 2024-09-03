include "test.thrift"
namespace java com.service

service UserService {
    void register(1:test.User user)
}
