package controllers;

import models.Lecture;
import models.LectureEvaluation;
import play.data.*;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import securesocial.core.Identity;
import securesocial.core.java.SecureSocial;
import services.utils.SecureInha;
import views.html.evaluation.index;

@SecureSocial.SecuredAction
@Security.Authenticated(SecureInha.class)
public class Evaluation extends Controller {
    static Form<LectureEvaluation> lectureEvaluationForm = Form.form(LectureEvaluation.class);
    static Form<String> searchForm = Form.form(String.class);

    /**
     * 인덱스 액션
     * @return
     */
    public static Result index() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        return ok(index.render(user, lectureEvaluationForm, searchForm, LectureEvaluation.all()));
    }

    /**
     * 강의평가 저장하는 액션
     * @return
     */
    public static Result save() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        Form<LectureEvaluation> filledForm = lectureEvaluationForm.bindFromRequest();

        if(filledForm.hasErrors()) {
            return badRequest(index.render(user, filledForm, searchForm, LectureEvaluation.all()));
        } else {
            LectureEvaluation.create(filledForm.get(), user);
            return redirect(routes.Evaluation.index());
        }
    }

    /**
     * 키워드 검색 액션
     * @return
     */
    public static Result search() {
        Identity user = (Identity) ctx().args.get(SecureSocial.USER_KEY);
        Form<String> filledForm = searchForm.bindFromRequest();

        for (String keyword : filledForm.data().values()) {
            return ok(index.render(user, lectureEvaluationForm, searchForm, LectureEvaluation.findByKeyword(keyword)));
        }

        return redirect(routes.Evaluation.index());
    }

    /**
     * 강의평가 Form 에서 ajax 로 호출되는 액션. DB 에서 강의명을 읽어와 json 으로 출력.
     * @param term
     * @return
     */
    public static Result getLectureNames(String term) {
        return ok(Json.toJson(Lecture.findLectureNameByKeyword(term)));
    }

    /**
     * 강의평가 Form 에서 ajax 로 호출되는 액션. DB 에서 교수명을 읽어와 json 으로 출력.
     * @param term
     * @return
     */
    public static Result getProfessorNames(String term) {
        return ok(Json.toJson(Lecture.findProfessorNameByKeyword(term)));
    }
}
