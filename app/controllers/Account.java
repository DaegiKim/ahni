package controllers;

import models.User;
import models.UserDetail;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import services.InhaAuthenticateHelper;
import views.html.account.index;
import views.html.account.login;
import views.html.account.startAuthenticate;

@SecureSocial.SecuredAction
public class Account extends Controller{
    static Form<String> authenticateForm = Form.form(String.class);

    public static Result login() {
        return ok(login.render());
    }

    public static Result index() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        User dbUser = User.findByIdentity(user);
        UserDetail userDetail = UserDetail.findByUserId(dbUser.id);
        return ok(index.render(user, userDetail));
    }

    /**
     * 인하대학생 인증 시작 화면 렌더링
     * @return
     */
    public static Result startAuthenticate() {
        return ok(startAuthenticate.render(authenticateForm));
    }

    /**
     * 인증 메일을 전송
     * @return
     */
    public static Result requestAuthenticate() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        User dbUser = User.findByIdentity(user);

        Form<String> filledForm = authenticateForm.bindFromRequest();

        for (String emailId : filledForm.data().values()) {
            InhaAuthenticateHelper.sendEmail(emailId, dbUser.id, ctx()._requestHeader());
            flash("success", String.format("입력하신 %s@inha.edu로 인증 메일을 발송하였습니다.",emailId));
            break;
        }
        return redirect(controllers.routes.Account.index());
    }

    public static Result completeAuthenticate(String token) {
        if(InhaAuthenticateHelper.validateToken(token)) {
            flash("success","인하대학교 학생 인증에 성공하였습니다.");
        }
        else {
            flash("error","인하대학교 학생 인증에 실패하였습니다.");
        }

        return redirect(routes.Account.index());
    }
}
