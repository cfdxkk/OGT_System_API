package OGTSystem.repository;

import OGTSystem.entity.UserAuthEntity;
import OGTSystem.entity.UserInfoEntity;
import OGTSystem.mapper.UserAuthMapper;
import OGTSystem.mapper.UserOftenEditMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import sun.tools.jconsole.JConsole;

import java.util.ArrayList;

@Repository
public class UserCreateRepository {

    @Autowired
    UserOftenEditMapper userofteneditmapper;

    @Autowired
    UserAuthMapper userauthmapper;

    public int createUser(UserInfoEntity userinfoentity, UserAuthEntity userauthentity){

        int result = 0;

        try {
            userofteneditmapper.createUser(userinfoentity);
            userauthmapper.createUserAuth(userauthentity);
            result = 1;
        } catch (Exception e){
            System.out.println("--------------------");
            System.out.println("error is: "+e);
            System.out.println("--------------------");
        }
        return result;
    }
}