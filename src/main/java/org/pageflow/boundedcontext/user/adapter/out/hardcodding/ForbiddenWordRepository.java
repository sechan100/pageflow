package org.pageflow.boundedcontext.user.adapter.out.hardcodding;

import org.pageflow.boundedcontext.common.exception.InputValueException;
import org.pageflow.boundedcontext.user.domain.Penname;
import org.pageflow.boundedcontext.user.domain.Username;
import org.pageflow.boundedcontext.user.port.out.CheckForbiddenWordPort;
import org.pageflow.shared.annotation.PersistenceAdapter;

import java.util.Set;

/**
 * @author : sechan
 */
@PersistenceAdapter
public class ForbiddenWordRepository implements CheckForbiddenWordPort {
    private static final Set<String> USERNAME = Set.of(
        "admin","administrator","anonymous","pageflow"
    );
    private static final Set<String> PENNAME =
            Set.of(
                    "admin","administrator","anonymous","pageflow","관리자","어드민","페이지플로우","패이지플로우","매니저",
                    "패플","도우미","고객센터","고객센타","고객샌터","고객샌타","한게임","한개임","운영자","엔토이","앤토이","씨펄",
                    "메니저","매니져","메니져","스탭","스텝","갈보","강간","개년","개놈","개뇬","개보지","개삽년","개새끼","개세이",
                    "개쉐이","개자식","개자지","개지랄","꼬추","노브라","니미랄","니미럴","니애미","니에미","등신","씨발뇬","포르노",
                    "딸딸이","또라이","레즈비언","멜섹","몰카","미친넘","미친년","미친놈","병신","미친",
                    "보지","보댕이","부랄","부부교환","불알","빙신","빠구리","빠굴","빠꾸리","빡우리","빡울","뽀르노","새꺄","새끈",
                    "새끈남","새끈녀","새끼","색남","색녀","색스","성인엽기","성인영화","성인용",
                    "섹녀","섹스","쉬팔","쉬펄","스발","시발","시벌","시파","시펄","쌍넘","쌍년","썅년",
                    "쌍놈","쌔깐","쌔끈","쌕쓰","쌕폰","썅","쒸팔","쒸펄","쓰바","씌팍","씨바","씨발","씨발넘","씨발년","씨발놈","젖탱",
                    "씨방","씨방새","씨버럴","씨벌","씨보랄","씨보럴","씨부랄","씨부럴","씨부리","씨불","씨브랄","씨파","씨팍","씨팔",
                    "야캠","와레즈","원조교재","원조교제",
                    "젖탱이","젼나","조까","졸라","좃","좆","지랄","지미랄","창녀","창년","창놈","창뇬","캠색","캠섹","페니스","페티쉬",
                    "폰색","폰쌕","헨타이","호로새끼","호빠","호스테스바","화상섹","자살","청산가리","청산가루",
                    "븅신", "븅딱", "느금마"
            );


    @Override
    public void checkPennameContainsForbiddenWord(Penname penname) {
        String pennameValue = penname.getValue();
        for(String forbiddenWord : PENNAME) {
            if(pennameValue.contains(forbiddenWord)) {
                throw InputValueException.builder()
                    .message("'%s'은(는) 필명에 포함될 수 없습니다. 다른 필명을 사용해주세요.", forbiddenWord)
                    .field("penname", pennameValue)
                    .build();
            }
        }
    }

    @Override
    public void checkUsernameContainsForbiddenWord(Username username) {
        String usernameValue = username.getValue();
        for(String forbiddenWord : USERNAME) {
            if(usernameValue.contains(forbiddenWord)) {
                throw InputValueException.builder()
                    .message("'%s'은(는) 아이디에 포함될 수 없습니다. 다른 아이디를 사용해주세요.", forbiddenWord)
                    .field("username", usernameValue)
                    .build();
            }
        }
    }
}
