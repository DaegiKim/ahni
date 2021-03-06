package models;

import com.mongodb.BasicDBObject;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.ObjectId;
import net.vz.mongodb.jackson.Id;
import play.Logger;
import play.modules.mongodb.jackson.MongoDB;
import securesocial.core.Identity;

import java.util.List;

public class UserDetail {
    public static JacksonDBCollection<UserDetail, String> coll = MongoDB.getCollection("user_detail", UserDetail.class, String.class);

    @Id
    @ObjectId
    public String id;               //MongoDB Object ID
    @ObjectId
    public String user;             //userId
    public Object validatedTime;    //학교 인증 시간
    public boolean isValidated;     //학교 인증 여부
    public String gender;           //성별
    public String major;            //전공
    public String entrance;         //입학년도

    public static List<UserDetail> all() {
        return UserDetail.coll.find().toArray();
    }

    public static UserDetail findByUserId(String user) {
        return UserDetail.coll.findOne(new BasicDBObject("user", new org.bson.types.ObjectId(user)));
    }

    public static boolean isValidatedUser(UserDetail userDetail) {
        if(userDetail != null) {
            return userDetail.isValidated;
        } else {
            return false;
        }
    }

    public boolean update(Identity user) {
        User dbUser = User.findByIdentity(user);
        UserDetail userDetail = UserDetail.findByUserId(dbUser.id);

        if(userDetail==null) {
            return false;
        }
        else {
            this.isValidated = userDetail.isValidated;
            this.validatedTime = userDetail.validatedTime;
            this.user = userDetail.user;

            coll.update(userDetail, this);

            return true;
        }
    }
}
